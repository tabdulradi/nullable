# Nullable

Enriches union types `A | Null` with an interface similar scala.Option making it usable inside for comprehensions

## Usage
Add the following to your build.sbt
```
scalacOptions += "-Yexplicit-nulls"
libraryDependencies += "com.abdulradi" %% "nullable-core" % "0.3.0"
```
0.3.0 is published for Scala 3 RC2 only ( If you are still in RC1, please use 0.2.0 instead)

## Features
### Plays nice with for comprehensions

``` scala
import com.abdulradi.nullable.syntax.*
val maybeA: Int | Null = 42
val maybeB: String | Null = "foo"

// Compiles fine
for 
  a <- maybeA 
yield 5

// Also compiles, no null pointer exceptions
for 
  a <- maybeA 
  b <- null
yield ()

// Compiles, and evaluates to null (because if condition doesn't match)
for 
  a <- maybeA 
  if (a < 0)
yield a
```

### Familiar Option-like experience

``` scala
maybeA.map(_ => 5)
maybeA.flatMap(_ => null)
maybeA.flatMap(_ => maybeB)
maybeA.fold(0)(_ + 1)
maybeA.getOrElse(0)
maybeA.contains(0)
maybeA.exists(_ == 0)
maybeA.toRight("Value was null")
```

### Convert from/to Option

``` scala
val optA = Some(42)
maybeA.toOption == optA
maybeA == optA.orNull
```

### Prevents auto flattening
While `Option[Option[A]]` is a valid type, the equivalent `A | Null | Null` is indistinguishable from `A | Null`. So, the library ensures that map and flatMap are used correctly used at compile time. 

The following examples **don't compile**

``` scala
for a <- maybeA yield maybeB // Shows a compile time error suggesting to use flatMap instead
maybeA.flatMap(_ => 5) // Shows a compile time error suggesting message to use map instead
maybeA.map(_ => null) // Shows a compile time error suggesting to use flatMap instead
maybeA.map(_ => maybeB) // Shows a compile time error suggesting to use flatMap instead
```

#### Working with Generics

The following doesn't compile, as we can't prove `A` won't be `Null`

``` scala
def useFlatMapWithNullableInScope[A](f: Int => A): A | Null = 
  maybeA.flatMap(f)
```

Solution: Propagate a Nullable instance and let the library check at usage site

``` scala
def useFlatMapWithNullableInScope[A: Nullable](f: Int => A): A | Null = 
  maybeA.flatMap(f)

def useMapWithNotNullInScope[A: NotNull](f: Int => A): A | Null = 
  maybeA.map(f)
```

### Lightweight version
If you only care about for-comprehension features, but not the rest of Option-like methods, we also offer a lightweight syntax
```scala
import com.abdulradi.nullable.forSyntax.*

for 
  a <- maybeA
  b <- maybeB
  res = a + b
  if (res % 2) == 0
yield res
```
The rest of the methods like fold, getOrElse, etc won't be in scope.


## License & Acknowledgements

Since this library mimics the scala.Option behavior, it made sense to copy and adapt the documentation of it's methods and sometimes the implementation too. Those bits are copyrighted by EPFL and Lightbend under Apache License 2.0, which is the same license as this library.
