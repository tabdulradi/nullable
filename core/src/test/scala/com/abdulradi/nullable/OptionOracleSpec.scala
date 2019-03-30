package com.abdulradi.nullable

import org.scalacheck._, Prop.forAll
import org.scalactic.TripleEquals._

object Utils {
  def nullAs[A]: A = null.asInstanceOf[A]

  def genNullable[A](genA: Gen[A]): Gen[A] = 
    Gen.oneOf(genA, Gen.const(nullAs[A]))
}

final case class Foo(str: String)
object Foo {
  val genNotNull: Gen[Foo] = Gen.alphaNumStr.map(Foo.apply)
  val genMaybeNull: Gen[Foo] = Utils.genNullable(genNotNull)

  implicit val genInstance: Arbitrary[Foo] = Arbitrary(genMaybeNull)
}

object OptionOracleSpec extends Properties("String") {
  import Utils._
  import Foo._

  property("apply") = forAll { a: Foo =>
    equivalent(Nullable(a), Option(a))
  }

  property("getOrElse") = forAll(genMaybeNull, genNotNull) { (maybeNull, notNull) =>
    Nullable(maybeNull).getOrElse(notNull) === Option(maybeNull).getOrElse(notNull)
  }

  property("map") = forAll { a: Foo =>
    equivalent(
      Nullable(a).map(_.str),
      Option(a).map(_.str)
    )
  }

  property("flatMap") = forAll { (a: Foo, b: Foo) =>
    equivalent(
      for {
        aa <- Nullable(a)
        bb <- Nullable(b)
      } yield aa.str + bb.str,
      for {
        aa <- Option(a)
        bb <- Option(b)
      } yield aa.str + bb.str
    )
  }

  property("filter") = forAll { (a: Foo, bool: Boolean) =>
    equivalent(
      Nullable(a).filter(_ => bool),
      Option(a).filter(_ => bool)
    )
  }

  property("filterNot") = forAll { (a: Foo, bool: Boolean) =>
    equivalent(
      Nullable(a).filterNot(_ => bool),
      Option(a).filterNot(_ => bool)
    )
  }

  property("contains") = forAll { a: Foo =>
    Nullable(a).contains(a) === Option(a).contains(a)
  }

  property("exists") = forAll { (a: Foo, bool: Boolean) =>
    Nullable(a).exists(_ => bool) === Option(a).exists(_ => bool)
  }

  property("forall") = forAll { (a: Foo, bool: Boolean) =>
    Nullable(a).forall(_ => bool) === Option(a).forall(_ => bool)
  }

  property("foreach") = forAll { a: Foo =>
    var n: Option[Foo] = None
    var o: Option[Foo] = None
    Nullable(a).foreach { aa => n = Some(aa) }
    Option(a).foreach { aa => o = Some(aa) }

    n === o
  }

  property("collect") = forAll { a: Foo => 
    val pf: PartialFunction[Foo, String] = { 
      case Foo(str) if str.length > 10 => str
    }
    equivalent(Nullable(a).collect(pf), Option(a).collect(pf))
  }

  property("orElse") = forAll { (a: Foo, b: Foo) => 
    equivalent(
      Nullable(a).orElse(Nullable(b)),
      Option(a).orElse(Option(b))
    )
  }

  property("zip") = forAll { (a: Foo, b: Foo) =>
    equivalent(
      Nullable(a) zip Nullable(b),
      (Option(a) zip Option(b)).headOption
    )
  }

  property("unzip") = forAll { (a: Foo, b: Foo) =>
    val (na, nb) = (for {
      aa <- Nullable(a)
      bb <- Nullable(b)
    } yield (aa, bb)).unzip

    val (oa, ob) = (for {
      aa <- Option(a)
      bb <- Option(b)
    } yield (aa, bb)).unzip

    equivalent(na, oa.headOption) && equivalent(nb, ob.headOption)
  }


  property("unzip3") = forAll { (a: Foo, b: Foo, c: Foo) =>
    val (na, nb, nc) = (for {
      aa <- Nullable(a)
      bb <- Nullable(b)
      cc <- Nullable(c)
    } yield (aa, bb, cc)).unzip3

    val (oa, ob, oc) = (for {
      aa <- Option(a)
      bb <- Option(b)
      cc <- Option(c)
    } yield (aa, bb, cc)).unzip3

    equivalent(na, oa.headOption) && equivalent(nb, ob.headOption) && equivalent(nc, oc.headOption)
  }

  property("iterator") = forAll { a: Foo =>
    Nullable(a).iterator.toList === Option(a).iterator.toList
  }

  property("toList") = forAll { a: Foo =>
    Nullable(a).toList === Option(a).toList
  }

  property("toOption") = forAll { a: Foo =>
    Nullable(a).toOption === Option(a)
  }

  property("toRight") = forAll { a: Foo =>
    Nullable(a).toRight("") === Option(a).toRight("")
  }

  property("toLeft") = forAll { a: Foo =>
    Nullable(a).toLeft("") === Option(a).toLeft("")
  }

  def equivalent[A <: AnyRef](nullable: Nullable[A], option: Option[A]): Boolean =
    nullable.isDefined === option.isDefined &&
    nullable.isEmpty === option.isEmpty &&
    nullable.nonEmpty === option.nonEmpty &&
    nullable.orNull == option.orNull &&
    nullable.toOption === option
}