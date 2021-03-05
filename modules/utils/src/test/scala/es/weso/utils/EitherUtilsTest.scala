package es.weso.utils

import es.weso.utils.EitherUtils._
import org.scalatest.funspec._
import org.scalatest.matchers.should._


class EitherUtilsTest extends AnyFunSpec with Matchers {

  describe(s"EitherUtils sequence with no message") {
    it(s"Calculates a sequence") {
      val les: List[Either[String, Int]] = List(Right(1), Right(2))
      val els: Either[String, List[Int]] = sequence(les)
      els.fold(e => fail(s"Should have a value"), v => v.length should (be(2)))
    }
  }

  describe(s"EitherUtils sequence with message") {
    it(s"Calculates a sequence") {
      val msg = "I'm an error"
      val les: List[Either[String, Int]] = List(Right(1), Right(2), Left(msg))
      val els: Either[String, List[Int]] = sequence(les)
      els.fold(s => s should( be(msg)), _ => fail(s"Should have failed"))
    }
  }

  describe(s"Take single with error") {
    it(s"Should take from empty list") {
      val msg = "failed"
      val e: Either[String, Int] = takeSingle(List[Int](), msg)
      e.fold(s => s should (be(msg)), _ => fail(s"Should have failed"))
    }
  }

  describe(s"Take single with error") {
    it(s"Should take from a list with more than one element") {
      val list: List[Int] = List(1,2)
      val msg = "failed"
      val e: Either[String, Int] = takeSingle(list, msg)
      e.fold(s => s should (be(msg)), _ => fail(s"Should have failed"))
    }
  }

  describe(s"Take single with no error") {
    it(s"Should take from a list") {
      val list: List[Int] = List(1)
      val msg = "failed"
      val e: Either[String, Int] = takeSingle(list, msg)
      e.fold(s => fail("Should have a value"), v => v should(be(1)))
    }
  }

}