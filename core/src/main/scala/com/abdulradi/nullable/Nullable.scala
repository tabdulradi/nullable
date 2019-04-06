package com.abdulradi.nullable

object Nullable {

  def apply[A](a: A): Nullable[A] = new Nullable(a)

  /** Returns an empty Nullable (wrapping null) */
  def empty[A]: Nullable[A] = Nullable(null.asInstanceOf[A])
}

final case class Nullable[+A](private val a: A) extends AnyVal {

  /** Returns true if the Nullable is wrapping a null, false otherwise. */
  @inline def isEmpty: Boolean = a == null

  /** Returns true if the Nullable is wraping a non-null value, false if wrapping a null. */
  @inline def isDefined: Boolean = !isEmpty

  @inline final def cata[B](ifEmpty: => B, f: A => B): B =
    if (isEmpty) ifEmpty else f(a)

  @inline final def get(): A =
    if (isEmpty) throw new NullPointerException("Nullable(null).get") else a

  def toOption: Option[A] = 
    Option(a)

  /** 
   * Simply returns the wrapped value as is, even if it is null 
   * 
   * Note: It won't compile if wrapped value type is a primitive type like Int or Bool, 
   * use getOrElse instead to provide a fallback value.
   * */
  @inline final def orNull(implicit asAnyRef: A <:< AnyRef): A = a



  /** Code Below has been adaped from scala.Option whose license is:
    * 
    * Scala (https://www.scala-lang.org)
    *
    * Copyright EPFL and Lightbend, Inc.
    *
    * Licensed under Apache License 2.0
    * (http://www.apache.org/licenses/LICENSE-2.0).
    *
    * See the NOTICE file distributed with this work for
    * additional information regarding copyright ownership.
    */

  /** Returns the wrapped value, 
   * unless it's null, in which case it returns the result of evaluating `default`.
   *  @param default  the default (fallback) expression.
   */
  @inline final def getOrElse[B >: A](default: => B): B =
    if (isEmpty) default else a

  /** Returns a Nullable containing the result of applying $f to this Nullable's
   * value if this Nullable is nonempty.
   * Otherwise return Nullable.empty.
   * 
   *  @note Result in wrapped in a Nullable, and might violate the monad laws
   *
   *  @param  f   the function to apply
   *  @see flatMap
   *  @see foreach
   */
  @inline final def map[B](f: A => B): Nullable[B] =
    if (isEmpty) Nullable.empty else Nullable(f(a))

  /** Returns the result of applying $f to this Nullable's
   *  value if the Nullable is nonempty.  Otherwise, evaluates
   *  expression `ifEmpty`.
   *
   * This is equivalent to:
   * {{{
   * nullable map f getOrElse ifEmpty
   * }}}
   *  @param  ifEmpty the expression to evaluate if empty.
   *  @param  f       the function to apply if nonempty.
   */
  @inline final def fold[B](ifEmpty: => B)(f: A => B): B =
    if (isEmpty) ifEmpty else f(a)

  /** Returns the result of applying $f to this Nullable's value if
   * this Nullable is nonempty.
   * Returns $Nullable.empty if this Nullable is empty.
   * Slightly different from `map` in that $f is expected to
   * return an Nullable (which could be $Nullable.empty).
   *  @param  f   the function to apply
   *  @see map
   *  @see foreach
   */
  @inline final def flatMap[B](f: A => Nullable[B]): Nullable[B] =
    if (isEmpty) Nullable.empty[B] else f(a)

  @inline final def flatten[B](implicit ev: A <:< Nullable[B]): Nullable[B] =
    if (isEmpty) Nullable.empty[B] else ev(this.get)

  /** Returns this Nullable if it is nonempty '''and''' applying the predicate $p to
   * this Nullable's value returns true. Otherwise, return $Nullable.empty.
   *
   *  @param  p   the predicate used for testing.
   */
  @inline final def filter(p: A => Boolean): Nullable[A] =
    if (isEmpty || p(a)) this else Nullable.empty

  /** Returns this Nullable if it is nonempty '''and''' applying the predicate $p to
   * this Nullable's value returns false. Otherwise, return $Nullable.empty.
   *  @param  p   the predicate used for testing.
   */
  @inline final def filterNot(p: A => Boolean): Nullable[A] =
    if (isEmpty || !p(a)) this else Nullable.empty

  /** Returns false if the option is $Nullable.empty, true otherwise. */
  final def nonEmpty = isDefined

  /** Tests whether the option contains a given value as an element.
   *  @example {{{
   *  // Returns true because Nullable instance contains string "Nullablething" which equals "Nullablething".
   *  Nullable("Nullablething") contains "Nullablething"
   *
   *  // Returns false because "Nullablething" != "anything".
   *  Nullable("Nullablething") contains "anything"
   *
   *  // Returns false when method called on Nullable.empty.
   *  Nullable.empty contains "anything"
   *  }}}
   *
   *  @param elem the element to test.
   *  @return `true` if the option has an element that is equal (as
   *  determined by `==`) to `elem`, `false` otherwise.
   */
  final def contains[A1 >: A](elem: A1): Boolean =
    !isEmpty && a == elem

  /** Returns true if this option is nonempty '''and''' the predicate
   * $p returns true when applied to this Nullable's value.
   * Otherwise, returns false.
   *  @param  p   the predicate to test
   */
  @inline final def exists(p: A => Boolean): Boolean =
    !isEmpty && p(a)

  /** Returns true if this option is empty '''or''' the predicate
   * $p returns true when applied to this Nullable's value.
   *
   *  @param  p   the predicate to test
   */
  @inline final def forall(p: A => Boolean): Boolean = isEmpty || p(a)

  /** Apply the given procedure $f to the option's value,
   *  if it is nonempty. Otherwise, do nothing.
   *
   *  @param  f   the procedure to apply.
   *  @see map
   *  @see flatMap
   */
  @inline final def foreach[U](f: A => U): Unit = if (!isEmpty) f(a)

  /** Returns a $Nullable containing the result of
   * applying `pf` to this Nullable's contained
   * value, '''if''' this option is
   * nonempty '''and''' `pf` is defined for that value.
   * Returns $Nullable.empty otherwise.
   *
   *  @example {{{
   *  // Returns Nullable(HTTP) because the partial function covers the case.
   *  Nullable("http") collect {case "http" => "HTTP"}
   *
   *  // Returns Nullable.empty because the partial function doesn't cover the case.
   *  Nullable("ftp") collect {case "http" => "HTTP"}
   *
   *  // Returns Nullable.empty because the option is empty. There is no value to pass to the partial function.
   *  Nullable.empty collect {case value => value}
   *  }}}
   *
   *  @param  pf   the partial function.
   *  @return the result of applying `pf` to this Nullable's
   *  value (if possible), or $Nullable.empty.
   */
  @inline final def collect[B](pf: PartialFunction[A, B]): Nullable[B] =
    if (isDefined && pf.isDefinedAt(a)) Nullable(pf(a)) else Nullable.empty

  /** Returns this Nullable if it is nonempty,
   *  otherwise return the result of evaluating `alternative`.
   *
   *  @param alternative the alternative expression.
   */
  @inline final def orElse[B >: A](alternative: => Nullable[B]): Nullable[B] =
    if (isEmpty) alternative else this

  /** Returns a $Nullable formed from this option and another option
   *  by combining the corresponding elements in a pair.
   *  If either of the two options is empty, $Nullable.empty is returned.
   *
   *  @example {{{
   *  // Returns Nullable(("foo", "bar")) because both options are nonempty.
   *  Nullable("foo") zip Nullable("bar")
   *
   *  // Returns Nullable.empty because `that` option is empty.
   *  Nullable("foo") zip Nullable.empty
   *
   *  // Returns Nullable.empty because `this` option is empty.
   *  Nullable.empty zip Nullable("bar")
   *  }}}
   *
   *  @param  that   the options which is going to be zipped
   */
  final def zip[A1 >: A, B](that: Nullable[B]): Nullable[(A1, B)] =
    if (isEmpty || that.isEmpty) Nullable.empty else Nullable((a, that.a))

  /** Converts a Nullable of a pair into a Nullable of the first element and a Nullable of the second element.
    *
    *  @tparam A1    the type of the first half of the element pair
    *  @tparam A2    the type of the second half of the element pair
    *  @param asPair an implicit conversion which asserts that the element type
    *                of this Option is a pair.
    *  @return       a pair of Options, containing, respectively, the first and second half
    *                of the element pair of this Option.
    */
  final def unzip[A1, A2](implicit asPair: A <:< (A1, A2)): (Nullable[A1], Nullable[A2]) = 
    if (isEmpty) 
      (Nullable.empty, Nullable.empty) 
    else {
      val e = asPair(a)
      (Nullable(e._1), Nullable(e._2))
    }

  /** Converts a Nullable of a triple into three Options, one containing the element from each position of the triple.
    *  @tparam A1      the type of the first of three elements in the triple
    *  @tparam A2      the type of the second of three elements in the triple
    *  @tparam A3      the type of the third of three elements in the triple
    *  @param asTriple an implicit conversion which asserts that the element type
    *                  of this Option is a triple.
    *  @return         a triple of Options, containing, respectively, the first, second, and third
    *                  elements from the element triple of this Option.
    */
  final def unzip3[A1, A2, A3](implicit asTriple: A <:< (A1, A2, A3)): (Nullable[A1], Nullable[A2], Nullable[A3]) = 
    if (isEmpty) 
      (Nullable.empty, Nullable.empty, Nullable.empty) 
    else {
      val e = asTriple(a)
      (Nullable(e._1), Nullable(e._2), Nullable(e._3))
    }

  /** Returns a singleton iterator returning the Nullable's value
   * if it is nonempty, or an empty iterator if the option is empty.
   */
  def iterator: Iterator[A] =
    if (isEmpty) collection.Iterator.empty else collection.Iterator.single(a)

  /** Returns a singleton list containing the Nullable's value
   * if it is nonempty, or the empty list if the Nullable is empty.
   *
   */
  def toList: List[A] =
    if (isEmpty) List() else new ::(a, Nil)



  /** Returns a [[scala.util.Left]] containing the given
   * argument `left` if this Nullable is empty, or
   * a [[scala.util.Right]] containing this Nullable's value if
   * this is nonempty.
   * @param left the expression to evaluate and return if this is empty
   * @see toLeft
   */
  @inline final def toRight[X](left: => X): Either[X, A] =
    if (isEmpty) Left(left) else Right(a)

  /** Returns a [[scala.util.Right]] containing the given
   * argument `right` if this is empty, or
   * a [[scala.util.Left]] containing this Nullable's value
   * if this Nullable is nonempty.
   * @param right the expression to evaluate and return if this is empty
   * @see toRight
   */
  @inline final def toLeft[X](right: => X): Either[A, X] =
    if (isEmpty) Right(right) else Left(a)
}
