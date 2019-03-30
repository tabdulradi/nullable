package com.abdulradi.nullable

import org.scalatest._

class NullableSpec extends FunSuite with Matchers {
  def assertEquals[A](a1: A, a2: A) = assert(a1 == a2)

  test("NullableZipNullable"){ 
    assertEquals(Nullable("foo") zip Nullable("bar"), Nullable(("foo", "bar")))
  }
  test("NullableZipNullable.empty"){   
    assertEquals(Nullable("foo") zip Nullable.empty[String], Nullable.empty)
  }
  test("Nullable.emptyZipNullable"){ 
    assertEquals(Nullable.empty zip Nullable("bar"), Nullable.empty)
  }
  test("Nullable.emptyZipNullable.empty"){ 
    assertEquals(Nullable.empty zip Nullable.empty, Nullable.empty)
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
}
