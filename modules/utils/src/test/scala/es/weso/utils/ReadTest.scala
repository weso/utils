package es.weso.utils

import munit._
import Read._
import cats._
import cats.implicits._

class ReadTest extends FunSuite {

  test("read Int") {
    assertEquals("23".read[Int], 23.asRight)

  }

  test("read Int error") {
    assertEquals("Foo".read[Int].isLeft, true)
  }

  test("read Int error") {
    assertEquals(Read[Int].unsafeRead("23"), 23)
  }

}
