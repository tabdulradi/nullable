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

val maybeA: Int | Null = 42
val maybeB: String | Null = "foo"

object ForComphrehensionSyntax:
  import com.abdulradi.nullable.forSyntax.*

  // Compiles fine
  for 
    a <- maybeA 
  yield 5
  
  // Also compiles, no null pointer exceptions
  for 
    a <- maybeA 
    b <- null
  yield ()

  // Filter is also supported
  for 
    a <- maybeA
    b <- maybeB
    if (a % 2) == 0
  yield s"$a - $b"
  
  // Familiar Option-like experience
  maybeA.map(_ => 5)
  maybeA.flatMap(_ => null)
  maybeA.flatMap(_ => maybeB)

object RestOfOptionSyntax:
  import com.abdulradi.nullable.syntax.*
  val maybeA: Int | Null = 42
  val maybeB: String | Null = "foo"

  maybeA.fold(0)(_ + 1)
  maybeA.getOrElse(0)
  maybeA.contains(0)
  maybeA.exists(_ == 0)
  maybeA.toRight("Value was null")

  // Convert from/to Option
  val optA = Some(42)
  maybeA.toOption == optA
  maybeA == optA.orNull

  // Prevents auto flattening // Doesn't compile
  // for a <- maybeA yield maybeB // Shows a compile time error suggesting to use flatMap instead
  // maybeA.flatMap(_ => 5) // Shows a compile time error suggesting message to use map instead
  // maybeA.map(_ => null) // Shows a compile time error suggesting to use flatMap instead
  // maybeA.map(_ => maybeB) // Shows a compile time error suggesting to use flatMap instead
  // def useFlatMapWithoutNullableInScope[A](f: Int => A): A | Null = maybeA.flatMap(f)

  // Shouldn't compile .. yet it does :/ 
  def useMapWithoutNotNullInScope[A](f: Int => A): A | Null = maybeA.map(f)