package es.weso.typing

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should._

class TypingTest extends AnyFunSpec with Matchers {

  case class K(s: String)
  case class V(s: String)
  case class Er(s: String)
  case class Ev(e: String)

  describe("Typing - getEvidences") {
    it("get evidences of empty map = None") {
      val m: Typing[K, V, Er, Ev] = Typing.empty
      m.getEvidences(K("x"), V("v")) should be(None)
    }

    it("get evidences of single map(x -> (v, e1)) = e1") {
      val m: Typing[K, V, Er, Ev] = Typing.empty.
        addType(K("x"), V("v"), List(Ev("e1")))
      m.getEvidences(K("x"), V("v")) should be(Some(List(Ev("e1"))))
    }

    it("get evidences of (x,v) in (x -> (v, e1), y -> (w,e2)) = List(e1)") {
      val m: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
        .addType(K("y"), V("w"), List(Ev("e2")))

      m.getEvidences(K("x"), V("v")) should be(Some(List(Ev("e1"))))
    }

    it("get evidences of (y,w) in (x -> (v, e1), y -> (w,e2)) = List(e2)") {
      val m: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
        .addType(K("y"), V("w"), List(Ev("e2")))
      m.getEvidences(K("y"), V("w")) should be(Some(List(Ev("e2"))))
    }

    it("get evidences of (y,v) in (x -> (v, e1), y -> (w,e2)) = None") {
      val m: Typing[K, V, Er, Ev] =
        Typing
          .empty
          .addType(K("x"), V("v"), List(Ev("e1")))
          .addType(K("y"), V("w"), List(Ev("e2")))
      m.getEvidences(K("y"), V("v")) should be(None)
    }
  }

  describe("Typing - combineTypings") {
    it("Can combine 2 empty typings") {
      val t1: Typing[K, V, Er, Ev] = Typing.empty
      val t2: Typing[K, V, Er, Ev] = Typing.empty
      t1.combineTyping(t2) should be(t1)
    }

    it("Can combine empty with (x -> (v,e1)) and return (x -> (v,e1))") {
      val t1: Typing[K, V, Er, Ev] = Typing.empty
      val t2: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
      t1.combineTyping(t2) should be(t2)
    }
    it("Can combine (x -> (v,e1)) with empty and return (x -> (v,e1))") {
      val t1: Typing[K, V, Er, Ev] = Typing.empty
      val t2: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
      t2.combineTyping(t1) should be(t2)
    }
  }

  describe("Add not evidences") {
    it(s"Should add not evidence when there is no failed value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.addNotEvidence(K("x"), V("b"), Er("E1"))
      t2.getOkValues(K("x")) should contain theSameElementsAs List(V("a"))
      t2.getFailedValues(K("x")) should contain theSameElementsAs List(V("b"))
    }
    it(s"Should add not evidence when there is failed value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.addNotEvidence(K("x"), V("b"), Er("E1"))
      t2.getOkValues(K("x")) should contain theSameElementsAs List(V("a"))
      t2.getFailedValues(K("x")) should contain theSameElementsAs List(V("b"))
    }

    it(s"Should add not evidence when there is a positive value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.addNotEvidence(K("x"), V("a"), Er("E1"))
      t2.getOkValues(K("x")) should contain theSameElementsAs List()
      t2.getFailedValues(K("x")) should contain theSameElementsAs List((V("a")))
    }
  }

  describe(s"Remove value") {
    it(s"Should remove values") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.removeValue(K("x"),V("a"))  
      t2.getOkValues(K("x")) should contain theSameElementsAs List()
    }

    it(s"Should remove values 2") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("x"), V("b"), List(Ev("e2")))
      val t2 = t1.removeValue(K("x"),V("a"))  
      t2.getOkValues(K("x")) should contain theSameElementsAs List(V("b"))
    }

    it(s"Should remove no values if it doesn't exist") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("x"), V("b"), List(Ev("e2")))
      val t2 = t1.removeValue(K("y"),V("a"))  
      t2.getOkValues(K("x")) should contain theSameElementsAs List(V("a"),V("b"))
      t2.getOkValues(K("y")) should contain theSameElementsAs List()
    }

    it(s"Should remove no values if it doesn't have that value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("y"), V("b"), List(Ev("e2")))
      val t2 = t1.removeValue(K("x"),V("b"))  
      t2.getOkValues(K("x")) should contain theSameElementsAs List(V("a"))
      t2.getOkValues(K("y")) should contain theSameElementsAs List(V("b"))
    }
  }
}
