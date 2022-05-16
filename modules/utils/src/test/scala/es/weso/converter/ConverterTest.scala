package es.weso.converter

import munit._

import scala.util.Try

class ConverterTest extends FunSuite with Converter {

  def cnvStr(str: String): Result[Int] = {
    Try(Integer.parseInt(str)).fold(exc => err(exc.getMessage), ok(_))
  }

  def cnvList(ls: List[String]): Result[List[Int]] = {
    sequence(ls.map(cnvStr(_)))
  }

  def checkPositive(n: Int): Result[Int] = {
    if (n >= 0) ok(n)
    else err(s"Negative value ${n}")
  }

  def cnvListPositive(ls: List[String]): Result[List[Int]] = {
    val xs: Result[List[Int]]                  = cnvList(ls)
    def next(ls: List[Int]): Result[List[Int]] = sequence(ls.map(n => checkPositive(n)))
    xs andThen (next)
  }

  shouldBeEqualTo("cnvStr(23)", cnvStr("23"), 23)
  shouldBeEqualTo("cnvStr(0)", cnvStr("0"), 0)
  shouldBeEqualTo("cnvList(0,1)", cnvList(List("0", "1")), List(0, 1))
  shouldBeEqualTo("cnvPositive(2,3)", cnvListPositive(List("2", "3")), List(2, 3))
  shouldFail("cnvListPositive(2,-1)", cnvListPositive(List("2", "-1")))

  def shouldBeEqualTo[A](name: String, r: Result[A], expected: A): Unit =
    test(name) {
      r.fold(
        ls => {
          fail(s"Errors: ${ls.toList.mkString(",")}")
        },
        n => assertEquals(n, expected)
      )
    }

  def shouldFail[A](name: String, r: Result[A]): Unit = {
    test(name) { assertEquals(r.isValid, false) }
  }
}
