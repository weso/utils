package es.weso.utils

import es.weso.utils.EitherUtils._
import munit._


class EitherUtilsTest extends FunSuite {

test(s"Calculates a sequence") {
      val les: List[Either[String, Int]] = List(Right(1), Right(2))
      val els: Either[String, List[Int]] = sequence(les)
      els.fold(e => fail(s"Should have a value"), v => assertEquals(v.length, 2))
}

test(s"EitherUtils sequence with message") {
 val msg = "I'm an error"
 val les: List[Either[String, Int]] = List(Right(1), Right(2), Left(msg))
 val els: Either[String, List[Int]] = sequence(les)
 els.fold(s => assertEquals(s, msg), _ => fail(s"Should have failed"))
}

test(s"Take single with error. Should take from empty list") {
 val msg = "failed"
 val e: Either[String, Int] = takeSingle(List[Int](), msg)
 e.fold(s => assertEquals(s, msg), _ => fail(s"Should have failed"))
}

test(s"Take single with error, Should take from a list with more than one element") {
 val list: List[Int] = List(1,2)
 val msg = "failed"
 val e: Either[String, Int] = takeSingle(list, msg)
 e.fold(s => assertEquals(s, msg), _ => fail(s"Should have failed"))
}

test(s"Take single with no error, should take from a list") {
 val list: List[Int] = List(1)
 val msg = "failed"
 val e: Either[String, Int] = takeSingle(list, msg)
 e.fold(s => fail("Should have a value"), v => assertEquals(v, 1))
}

}