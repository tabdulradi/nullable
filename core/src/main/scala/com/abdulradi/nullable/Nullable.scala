package com.abdulradi.nullable

object Nullable {

  /** Returns an empty Nullable (wrapping null) */
  def empty[A <: AnyRef]: Nullable[A] = {
    Nullable[A](null.asInstanceOf[A])
  }
}

final case class Nullable[+A <: AnyRef](private val a: A) extends AnyVal {

  /** Returns true if the Nullable is wrapping a null, false otherwise. */
  @inline def isEmpty: Boolean = a == null

  /** Returns true if the Nullable is wraping a non-null value, false if wrapping a null. */
  @inline def isDefined: Boolean = !isEmpty

  /** Returns the wrapped value, 
   * unless it's null, in which case it returns the result of evaluating `default`.
   *  @param default  the default (fallback) expression.
   */
  @inline final def getOrElse[B >: A](default: => B): B =
    if (isEmpty) default else a

  /** Simply returns the wrapped value as is, even if it is null */
  @inline final def orNull = a

  /** Returns a $some containing the result of applying $f to this $option's
   * value if this $option is nonempty.
   * Otherwise return $Nullable.empty.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => Some(f(x))
   *   case Nullable.empty    => Nullable.empty
   * }
   * }}}
   *  @note This is similar to `flatMap` except here,
   *  $f does not need to wrap its result in an $option.
   *
   *  @param  f   the function to apply
   *  @see flatMap
   *  @see foreach
   */
  @inline final def map[B <: AnyRef](f: A => B): Nullable[B] =
    if (isEmpty) Nullable.empty else Nullable(f(a))

  /** Returns the result of applying $f to this $option's
   *  value if the $option is nonempty.  Otherwise, evaluates
   *  expression `ifEmpty`.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => f(x)
   *   case Nullable.empty    => ifEmpty
   * }
   * }}}
   * This is also equivalent to:
   * {{{
   * option map f getOrElse ifEmpty
   * }}}
   *  @param  ifEmpty the expression to evaluate if empty.
   *  @param  f       the function to apply if nonempty.
   */
  @inline final def fold[B](ifEmpty: => B)(f: A => B): B =
    if (isEmpty) ifEmpty else f(a)

  /** Returns the result of applying $f to this $option's value if
   * this $option is nonempty.
   * Returns $Nullable.empty if this $option is empty.
   * Slightly different from `map` in that $f is expected to
   * return an $option (which could be $Nullable.empty).
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => f(x)
   *   case Nullable.empty    => Nullable.empty
   * }
   * }}}
   *  @param  f   the function to apply
   *  @see map
   *  @see foreach
   */
  @inline final def flatMap[B <: AnyRef](f: A => Nullable[B]): Nullable[B] =
    if (isEmpty) Nullable.empty[B] else f(a)

  /** Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns true. Otherwise, return $Nullable.empty.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) if p(x) => Some(x)
   *   case _               => Nullable.empty
   * }
   * }}}
   *  @param  p   the predicate used for testing.
   */
  @inline final def filter(p: A => Boolean): Nullable[A] =
    if (isEmpty || p(a)) this else Nullable.empty

  /** Returns this $option if it is nonempty '''and''' applying the predicate $p to
   * this $option's value returns false. Otherwise, return $Nullable.empty.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) if !p(x) => Some(x)
   *   case _                => Nullable.empty
   * }
   * }}}
   *  @param  p   the predicate used for testing.
   */
  @inline final def filterNot(p: A => Boolean): Nullable[A] =
    if (isEmpty || !p(a)) this else Nullable.empty

  /** Returns false if the option is $Nullable.empty, true otherwise.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(_) => true
   *   case Nullable.empty    => false
   * }
   * }}}
   *  @note   Implemented here to avoid the implicit conversion to Iterable.
   */
  final def nonEmpty = isDefined

  /** Tests whether the option contains a given value as an element.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => x == elem
   *   case Nullable.empty    => false
   * }
   * }}}
   *  @example {{{
   *  // Returns true because Some instance contains string "something" which equals "something".
   *  Some("something") contains "something"
   *
   *  // Returns false because "something" != "anything".
   *  Some("something") contains "anything"
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
   * $p returns true when applied to this $option's value.
   * Otherwise, returns false.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => p(x)
   *   case Nullable.empty    => false
   * }
   * }}}
   *  @param  p   the predicate to test
   */
  @inline final def exists(p: A => Boolean): Boolean =
    !isEmpty && p(a)

  /** Returns true if this option is empty '''or''' the predicate
   * $p returns true when applied to this $option's value.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => p(x)
   *   case Nullable.empty    => true
   * }
   * }}}
   *  @param  p   the predicate to test
   */
  @inline final def forall(p: A => Boolean): Boolean = isEmpty || p(a)

  /** Apply the given procedure $f to the option's value,
   *  if it is nonempty. Otherwise, do nothing.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => f(x)
   *   case Nullable.empty    => ()
   * }
   * }}}
   *  @param  f   the procedure to apply.
   *  @see map
   *  @see flatMap
   */
  @inline final def foreach[U](f: A => U): Unit = if (!isEmpty) f(a)

  /** Returns a $some containing the result of
   * applying `pf` to this $option's contained
   * value, '''if''' this option is
   * nonempty '''and''' `pf` is defined for that value.
   * Returns $Nullable.empty otherwise.
   *
   *  @example {{{
   *  // Returns Some(HTTP) because the partial function covers the case.
   *  Some("http") collect {case "http" => "HTTP"}
   *
   *  // Returns Nullable.empty because the partial function doesn't cover the case.
   *  Some("ftp") collect {case "http" => "HTTP"}
   *
   *  // Returns Nullable.empty because the option is empty. There is no value to pass to the partial function.
   *  Nullable.empty collect {case value => value}
   *  }}}
   *
   *  @param  pf   the partial function.
   *  @return the result of applying `pf` to this $option's
   *  value (if possible), or $Nullable.empty.
   */
  @inline final def collect[B <: AnyRef](pf: PartialFunction[A, B]): Nullable[B] =
    if (isDefined && pf.isDefinedAt(a)) Nullable(pf(a)) else Nullable.empty

  /** Returns this $option if it is nonempty,
   *  otherwise return the result of evaluating `alternative`.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => Some(x)
   *   case Nullable.empty    => alternative
   * }
   * }}}
   *  @param alternative the alternative expression.
   */
  @inline final def orElse[B >: A <: AnyRef](alternative: => Nullable[B]): Nullable[B] =
    if (isEmpty) alternative else this

  /** Returns a $some formed from this option and another option
   *  by combining the corresponding elements in a pair.
   *  If either of the two options is empty, $Nullable.empty is returned.
   *
   *  This is equivalent to:
   *  {{{
   *  (option1, option2) match {
   *    case (Some(x), Some(y)) => Some((x, y))
   *    case _                  => Nullable.empty
   *  }
   *  }}}
   *  @example {{{
   *  // Returns Some(("foo", "bar")) because both options are nonempty.
   *  Some("foo") zip Some("bar")
   *
   *  // Returns Nullable.empty because `that` option is empty.
   *  Some("foo") zip Nullable.empty
   *
   *  // Returns Nullable.empty because `this` option is empty.
   *  Nullable.empty zip Some("bar")
   *  }}}
   *
   *  @param  that   the options which is going to be zipped
   */
  final def zip[A1 >: A, B <: AnyRef](that: Nullable[B]): Nullable[(A1, B)] =
    if (isEmpty || that.isEmpty) Nullable.empty else Nullable((a, that.a))

  /** Converts an Option of a pair into an Option of the first element and an Option of the second element.
    *
    *  This is equivalent to:
    *  {{{
    *  option match {
    *    case Some((x, y)) => (Some(x), Some(y))
    *    case _            => (Nullable.empty,    Nullable.empty)
    *  }
    *  }}}
    *  @tparam A1    the type of the first half of the element pair
    *  @tparam A2    the type of the second half of the element pair
    *  @param asPair an implicit conversion which asserts that the element type
    *                of this Option is a pair.
    *  @return       a pair of Options, containing, respectively, the first and second half
    *                of the element pair of this Option.
    */
  final def unzip[A1 <: AnyRef, A2 <: AnyRef](implicit asPair: A <:< (A1, A2)): (Nullable[A1], Nullable[A2]) = 
    if (isEmpty) 
      (Nullable.empty, Nullable.empty) 
    else {
      val e = asPair(a)
      (Nullable(e._1), Nullable(e._2))
    }

  /** Converts an Option of a triple into three Options, one containing the element from each position of the triple.
    *
    *  This is equivalent to:
    *  {{{
    *  option match {
    *    case Some((x, y, z)) => (Some(x), Some(y), Some(z))
    *    case _               => (Nullable.empty,    Nullable.empty,    Nullable.empty)
    *  }
    *  }}}
    *  @tparam A1      the type of the first of three elements in the triple
    *  @tparam A2      the type of the second of three elements in the triple
    *  @tparam A3      the type of the third of three elements in the triple
    *  @param asTriple an implicit conversion which asserts that the element type
    *                  of this Option is a triple.
    *  @return         a triple of Options, containing, respectively, the first, second, and third
    *                  elements from the element triple of this Option.
    */
  final def unzip3[A1 <: AnyRef, A2 <: AnyRef, A3 <: AnyRef](implicit asTriple: A <:< (A1, A2, A3)): (Nullable[A1], Nullable[A2], Nullable[A3]) = 
    if (isEmpty) 
      (Nullable.empty, Nullable.empty, Nullable.empty) 
    else {
      val e = asTriple(a)
      (Nullable(e._1), Nullable(e._2), Nullable(e._3))
    }

  /** Returns a singleton iterator returning the $option's value
   * if it is nonempty, or an empty iterator if the option is empty.
   */
  def iterator: Iterator[A] =
    if (isEmpty) collection.Iterator.empty else collection.Iterator.single(a)

  /** Returns a singleton list containing the $option's value
   * if it is nonempty, or the empty list if the $option is empty.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => List(x)
   *   case Nullable.empty    => Nil
   * }
   * }}}
   */
  def toList: List[A] =
    if (isEmpty) List() else new ::(a, Nil)

  def toOption: Option[A] = 
    Option(a)

  /** Returns a [[scala.util.Left]] containing the given
   * argument `left` if this $option is empty, or
   * a [[scala.util.Right]] containing this $option's value if
   * this is nonempty.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => Right(x)
   *   case Nullable.empty    => Left(left)
   * }
   * }}}
   * @param left the expression to evaluate and return if this is empty
   * @see toLeft
   */
  @inline final def toRight[X](left: => X): Either[X, A] =
    if (isEmpty) Left(left) else Right(a)

  /** Returns a [[scala.util.Right]] containing the given
   * argument `right` if this is empty, or
   * a [[scala.util.Left]] containing this $option's value
   * if this $option is nonempty.
   *
   * This is equivalent to:
   * {{{
   * option match {
   *   case Some(x) => Left(x)
   *   case Nullable.empty    => Right(right)
   * }
   * }}}
   * @param right the expression to evaluate and return if this is empty
   * @see toRight
   */
  @inline final def toLeft[X](right: => X): Either[A, X] =
    if (isEmpty) Right(right) else Left(a)
}
