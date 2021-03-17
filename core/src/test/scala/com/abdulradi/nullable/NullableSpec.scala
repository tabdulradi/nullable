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

import org.scalatest.funsuite.AnyFunSuite
import syntax.*

class NullableSpec extends AnyFunSuite:
  def assertEquals[A](a1: A, a2: A) = assert(a1 == a2)

  test("ForComprehensionMapNull"){ 
    val maybeA: Int | Null = null
    assertEquals(
      for a <- maybeA yield a + 1,
      null
    )
  }

  test("ForComprehensionFlatMap"){ 
    val maybeA: Int | Null = 4
    val maybeB: Int | Null = 6
    assertEquals(
      for 
        a <- maybeA
        b <- maybeB
      yield a + b,
      10
    )
  }

  test("ForComprehensionFlatMapNull"){ 
    val maybeA: Int | Null = 4
    val maybeB: Int | Null = null
    assertEquals(
      for 
        a <- maybeA
        b <- maybeB
      yield a + b,
      null
    )
  }

  test("ForComprehensionFilter"){ 
    val maybeA: Int | Null = 4
    val maybeB: Int | Null = 6
    assertEquals(
      for 
        a <- maybeA
        b <- maybeB
        res = a + b
        if (res % 2) == 0
      yield res,
      10
    )
  }

  test("ForComprehensionFilter2") { 
    val maybeA: Int | Null = 4 
    val maybeB: Int | Null = 6
    assertEquals(
      for 
        a <- maybeA
        b <- maybeB
        res = a + b
        if (res % 2) != 0
      yield res,
      null
    )
  }

  test("GenericsForComprehensionMap"){ 
    val maybeA: Int | Null = 4
    // val x = Nullable(4).map(_ => Nullable(5))
    def useMap[A](f: Int => A): A | Null = 
      Nullable(4).map(f)
      // for {
      //   a <- maybeA
      // } yield f(a)

    def useFlatMap[A: Nullable](f: Int => A): A | Null = 
      Nullable(4).flatMap(f)

    useMap(_ => null)
  }

  test("NullableZipNullable"){ 
    assertEquals(Nullable("foo") zip Nullable("bar"), Nullable(("foo", "bar")))
  }
  test("NullableZipNullable.empty"){   
    assertEquals(Nullable("foo") zip Nullable.empty[String], null)
  }
  test("Nullable.emptyZipNullable"){ 
    assertEquals(Nullable.empty zip Nullable("bar"), null)
  }
  test("Nullable.emptyZipNullable.empty"){ 
    assertEquals(Nullable.empty zip Nullable.empty, null)
  }

  test("NullableUnzipToNullablePair"){ 
    assertEquals(Nullable(("foo", "bar")).unzip, (Nullable("foo"), Nullable("bar")))
  }
  test("NullableUnzipToNullableNullable.empty"){ 
    assertEquals(Nullable(("foo", null)).unzip, (Nullable("foo"), Nullable(null)))
  }
  test("Nullable.emptyUnzipToNullable.emptyPair"){ 
    assertEquals(Nullable.empty[(String, String)].unzip, (Nullable.empty, Nullable.empty))
  }

  test("NullableUnzip3ToNullableTriple"){ 
    assertEquals(Nullable(("foo", "bar", "z")).unzip3, (Nullable("foo"), Nullable("bar"), Nullable("z")))
  }
  test("NullableUnzip3ToNullableNullable.empty"){ 
    assertEquals(Nullable(("foo", null, null)).unzip3, (Nullable("foo"), Nullable(null), Nullable(null)))
  }
  test("Nullable.emptyUnzip3ToNullable.emptyTriple"){ 
    assertEquals(Nullable.empty.unzip3, (Nullable.empty, Nullable.empty, Nullable.empty))
  }

