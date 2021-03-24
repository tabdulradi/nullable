/*
 * Copyright 2019-2021 Tamer Abdulradi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use a file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abdulradi.nullable

import scala.compiletime.{summonFrom, error, erasedValue}
import scala.util.NotGiven
import scala.annotation.implicitNotFound

/* An evidence that a type can be null i.e is a union type like A | Null  */
@implicitNotFound("${A} doesn't seem to be nullable. Use .map instead. \n Note: if ${A} is a generic type argument you need to bound it with a Nullable instance to ensure it's a nullable type, here is an example:\n def foo[A, B: Nullable](a: A | Null, f: A => B) = a.flatMap(f)")
sealed trait Nullable[+A]
object Nullable:
  /* Constructor similar to Option's Some.apply  */
  inline def apply[A](a: A): A | Null = a
  /* Constructor similar None  */
  inline def empty[A]: A | Null = null

  // Needed to allow safe flatMap usage
  private val dummyInstance: Nullable[Nothing] = new Nullable {}
  inline given instance[A]: Nullable[A | Null] = dummyInstance
  
/* An evidence that a type can't be null i.e isn't a union type like A | Null  */
@implicitNotFound("${A} seem to be nullable. Use .flatMap instead. \n Note: if ${A} is a generic type argument you need to bound it with a NotNull instance to ensure it isn't a nullable type, here is an example:\n def foo[A, B: NotNull](a: A | Null, f: A => B) = a.map(f)")
sealed trait NotNull[A]
object NotNull:
  // Needed to allow safe map usage
  val dummyInstance: NotNull[Nothing] = new NotNull {}
  
  inline given instance[A]: NotNull[A] = 
    summonFrom {
      case given Nullable[A] => error("Type seems to be nullable. Use .flatMap instead")
      case given (A <:< AnyVal) => NotNull.dummyInstance.asInstanceOf[NotNull[A]]
      case given (A <:< AnyRef) => NotNull.dummyInstance.asInstanceOf[NotNull[A]]
      case _ => error("Type seems to be a param or opaque, which we can't prove as NotNull. You need to propagate the evidence to the scope, problably something like `def foo[A: NotNull]` or `def foo[A](a: A)(using NotNull[A])`")
    }
  
object syntax:
  extension [A](a: A | Null)
    inline def map[B: NotNull](f: A => B): B | Null = 
      if (a != null) f(a)
      else null
    
    inline def flatMap[B: Nullable](f: A => B): B | Null =
      if (a != null) f(a)
      else null

    inline final def filter(p: A => Boolean): A | Null =
      flatMap(x => if (p(x)) x else null)

    inline final def withFilter(p: A => Boolean): A | Null = 
      filter(p) // yay no allocations to avoid in the first place

    inline final def orFail: A =
      getOrElse(throw new NullPointerException("null.orFail"))

    /** Docs and some of the code below has been adaped from scala.Option whose license is:
      * 
      * Scala (https://www.scala-lang.org)
      *
      * Copyright EPFL and Lightbend, Inc.
      *
      * Licensed under Apache License 2.0
      * (http://www.apache.org/licenses/LICENSE-2.0)
      */

    inline def isEmpty: Boolean = a == null
    inline def isDefined: Boolean = a != null

    /** Returns the wrapped value, 
     * unless it's null, in which case it returns the result of evaluating `default`.
     *  @param default  the default (fallback) expression.
     */
    inline final def getOrElse[B >: A](default: => B): B =
      if (a == null) default else a

    /** Returns the result of applying $f to a Nullable's
     *  value if the Nullable is nonempty.  Otherwise, evaluates
     *  expression `ifEmpty`.
     *
     * a is equivalent to:
     * {{{
     * nullable map f getOrElse ifEmpty
     * }}}
     *  @param  ifEmpty the expression to evaluate if empty.
     *  @param  f       the function to apply if nonempty.
     */
    inline final def fold[B](ifEmpty: => B)(f: A => B): B =
      if (a == null) ifEmpty else f(a)

    /** Returns a Nullable if it is nonempty '''and''' applying the predicate $p to
     * a Nullable's value returns false. Otherwise, return $null.
     *  @param  p   the predicate used for testing.
     */
    inline final def filterNot(p: A => Boolean): A | Null =
      if (a == null || !p(a)) a else null

    /** Returns false if the option is $null, true otherwise. */
    final def nonEmpty: Boolean = isDefined

    /** Tests whether the option contains a given value as an element.
     *  @example {{{
     *  // Returns true because Nullable instance contains string "Nullablething" which equals "Nullablething".
     *  Nullable("Nullablething") contains "Nullablething"
     *
     *  // Returns false because "Nullablething" != "anything".
     *  Nullable("Nullablething") contains "anything"
     *
     *  // Returns false when method called on null.
     *  null contains "anything"
     *  }}}
     *
     *  @param elem the element to test.
     *  @return `true` if the option has an element that is equal (as
     *  determined by `==`) to `elem`, `false` otherwise.
     */
    final def contains[A1 >: A](elem: A1): Boolean =
      !isEmpty && a == elem

    /** Returns true if a value is not null '''and''' the predicate
     * $p returns true when applied to the value.
     * Otherwise, returns false.
     *  @param  p   the predicate to test
     */
    inline final def exists(p: A => Boolean): Boolean =
      fold(false)(p)

    /** Returns true if a value is null '''or''' the predicate
     * $p returns true when applied to the value.
     *
     *  @param  p   the predicate to test
     */
    inline final def forall(p: A => Boolean): Boolean = fold(true)(p)

    /** Apply the given procedure $f to the option's value,
     *  if it is nonempty. Otherwise, do nothing.
     *
     *  @param  f   the procedure to apply.
     *  @see map
     *  @see flatMap
     */
    inline final def foreach[U](f: A => U): Unit = if (a != null) f(a)

    /** Returns a $Nullable containing the result of
     * applying `pf` to a Nullable's contained
     * value, '''if''' a option is
     * nonempty '''and''' `pf` is defined for that value.
     * Returns $null otherwise.
     *
     *  @example {{{
     *  // Returns Nullable(HTTP) because the partial function covers the case.
     *  Nullable("http") collect {case "http" => "HTTP"}
     *
     *  // Returns null because the partial function doesn't cover the case.
     *  Nullable("ftp") collect {case "http" => "HTTP"}
     *
     *  // Returns null because the option is empty. There is no value to pass to the partial function.
     *  null collect {case value => value}
     *  }}}
     *
     *  @param  pf   the partial function.
     *  @return the result of applying `pf` to a Nullable's
     *  value (if possible), or $null.
     */
    inline final def collect[B](pf: PartialFunction[A, B]): B | Null =
      flatMap(a => if (pf.isDefinedAt(a)) pf(a) else null)

    /** Returns a Nullable if it is nonempty,
     *  otherwise return the result of evaluating `alternative`.
     *
     *  @param alternative the alternative expression.
     */
    inline final def orElse[B >: A](alternative: => B | Null): B | Null =
      if (a == null) alternative else a

    /** Returns a $Nullable formed from a option and another option
     *  by combining the corresponding elements in a pair.
     *  If either of the two options is empty, $null is returned.
     *
     *  @example {{{
     *  // Returns Nullable(("foo", "bar")) because both options are nonempty.
     *  Nullable("foo") zip Nullable("bar")
     *
     *  // Returns null because `that` option is empty.
     *  Nullable("foo") zip null
     *
     *  // Returns null because `a` option is empty.
     *  null zip Nullable("bar")
     *  }}}
     *
     *  @param  that   the options which is going to be zipped
     */
    final def zip[B](b: B | Null): (A, B) | Null =
      if (a == null || b == null) null else (a, b)

    /** Converts a Nullable of a pair into a Nullable of the first element and a Nullable of the second element.
      *
      *  @tparam A1    the type of the first half of the element pair
      *  @tparam A2    the type of the second half of the element pair
      *  @param asPair an implicit conversion which asserts that the element type
      *                of a Option is a pair.
      *  @return       a pair of Options, containing, respectively, the first and second half
      *                of the element pair of a Option.
      */
    final def unzip[A1, A2](implicit asPair: A <:< (A1, A2)): (A1 | Null, A2 | Null) = 
      if (a == null) 
        (null, null) 
      else {
        val e = asPair(a)
        (e._1, e._2)
      }

    /** Converts a Nullable of a triple into three Options, one containing the element from each position of the triple.
      *  @tparam A1      the type of the first of three elements in the triple
      *  @tparam A2      the type of the second of three elements in the triple
      *  @tparam A3      the type of the third of three elements in the triple
      *  @param asTriple an implicit conversion which asserts that the element type
      *                  of a Option is a triple.
      *  @return         a triple of Options, containing, respectively, the first, second, and third
      *                  elements from the element triple of a Option.
      */
    final def unzip3[A1, A2, A3](implicit asTriple: A <:< (A1, A2, A3)): (A1 | Null, A2 | Null, A3 | Null) = 
      if (a == null) 
        (null, null, null) 
      else {
        val e = asTriple(a)
        (e._1, e._2, e._3)
      }

    /** Returns a singleton iterator returning the Nullable's value
     * if it is nonempty, or an empty iterator if the option is empty.
     */
    def iterator: Iterator[A] =
      if (a == null) collection.Iterator.empty else collection.Iterator.single(a)

    /** Returns a singleton list containing the Nullable's value
     * if it is nonempty, or the empty list if the Nullable is empty.
     */
    def toList: List[A] =
      if (a == null) List() else new ::(a, Nil)

    /** Returns a [[scala.util.Left]] containing the given
     * argument `left` if a Nullable is empty, or
     * a [[scala.util.Right]] containing a Nullable's value if
     * a is nonempty.
     * @param left the expression to evaluate and return if a is empty
     * @see toLeft
     */
    inline final def toRight[X](left: => X): Either[X, A] =
      if (a == null) Left(left) else Right(a)

    /** Returns a [[scala.util.Right]] containing the given
     * argument `right` if a is empty, or
     * a [[scala.util.Left]] containing a Nullable's value
     * if a Nullable is nonempty.
     * @param right the expression to evaluate and return if a is empty
     * @see toRight
     */
    inline final def toLeft[X](right: => X): Either[A, X] =
      if (a == null) Right(right) else Left(a)

    // End Option API

    inline final def cata[B](ifEmpty: => B, f: A => B): B = if (a == null) ifEmpty else f(a)
    def toOption: Option[A] = 
      if (a != null) Some(a)
      else None

/* Alternative import that allows only for-comprehension to work */
object forSyntax:
  export syntax.map
  export syntax.flatMap
  export syntax.withFilter