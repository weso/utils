package es.weso.typing

import cats._
import cats.implicits._
import TypingResult._
import munit._

class TypingResultTest extends FunSuite {

  case class Er(s: String)
  case class Ev(e: String)

  
    test("get evidences of empty") {
      val m: TypingResult[Er, Ev] = Monoid[TypingResult[Er,Ev]].empty
      assertEquals(m.getEvidences, Some(List()))
    }
    test("add simple evidence") {
      val m: TypingResult[Er, Ev] = Monoid[TypingResult[Er,Ev]].empty
      val r = m.addEvidence(Ev("e1"))
      assertEquals(r.getEvidences, Some(List(Ev("e1"))))
    }
    test("add two evidences") {
      val m: TypingResult[Er, Ev] = Monoid[TypingResult[Er,Ev]].empty
      val r = m.addEvidence(Ev("e1")).addEvidence(Ev("e2"))
      assertEquals(r.getEvidences, Some(List(Ev("e1"),Ev("e2"))))
    }
    test("add no evidence to two evidences") {
      val m: TypingResult[Er, Ev] = Monoid[TypingResult[Er,Ev]].empty
      val r = m.addEvidence(Ev("e1")).addEvidence(Ev("e2")).addNotEvidence(Er("er1"))
      assertEquals(r.getEvidences, None)
      assertEquals(r.getErrors, Some(List(Er("er1"))))
    }
    test("can combine two valid results ") {
      val m1 = Monoid[TypingResult[Er,Ev]].empty.addEvidence(Ev("e1")).addEvidence(Ev("e2"))
      val m2 = Monoid[TypingResult[Er,Ev]].empty.addEvidence(Ev("e2")).addEvidence(Ev("e3"))
      val r = m1.combine(m2)
      assertEquals(r.getErrors, None)
      r.getEvidences.fold(
        fail(s"No value for $r evidences"))(
        ls => assertEquals(ls,List(Ev("e1"),Ev("e2"),Ev("e2"),Ev("e3"))))
    }
    test("can combine one valid result and one failed result") {
      val m1 = Monoid[TypingResult[Er,Ev]].empty.addEvidence(Ev("e1")).addEvidence(Ev("e2"))
      val m2 = Monoid[TypingResult[Er,Ev]].empty.addEvidence(Ev("e2")).addNotEvidence(Er("er3"))
      val r = m1.combine(m2)
      r.getErrors.fold(fail(s"No value for $r"))(ls => assertEquals(ls, List(Er("er3"))))
      assertEquals(r.getEvidences, None)

      val r1 = m2.combine(m1)
      r1.getErrors.fold(fail(s"No value for $r1"))(ls => assertEquals(ls, List(Er("er3"))))
      assertEquals(r1.getEvidences, None)
    }


}
