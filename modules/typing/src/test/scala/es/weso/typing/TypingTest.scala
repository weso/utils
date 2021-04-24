package es.weso.typing

import munit._

class TypingTest extends FunSuite {

  case class K(s: String)
  case class V(s: String)
  case class Er(s: String)
  case class Ev(e: String)

  test("get evidences of empty map = None") {
      val m: Typing[K, V, Er, Ev] = Typing.empty
    assertEquals(m.getEvidences(K("x"), V("v")), None)
  }

  test("get evidences of single map(x -> (v, e1)) = e1") {
      val m: Typing[K, V, Er, Ev] = Typing.empty.
        addType(K("x"), V("v"), List(Ev("e1")))
    assertEquals(m.getEvidences(K("x"), V("v")), Some(List(Ev("e1"))))
  }

  test("get evidences of (x,v) in (x -> (v, e1), y -> (w,e2)) = List(e1)") {
      val m: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
        .addType(K("y"), V("w"), List(Ev("e2")))

   assertEquals(m.getEvidences(K("x"), V("v")), Some(List(Ev("e1"))))
  }

  test("get evidences of (y,w) in (x -> (v, e1), y -> (w,e2)) = List(e2)") {
      val m: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
        .addType(K("y"), V("w"), List(Ev("e2")))
    assertEquals(m.getEvidences(K("y"), V("w")), Some(List(Ev("e2"))))
  }

  test("get evidences of (y,v) in (x -> (v, e1), y -> (w,e2)) = None") {
      val m: Typing[K, V, Er, Ev] =
        Typing
          .empty
          .addType(K("x"), V("v"), List(Ev("e1")))
          .addType(K("y"), V("w"), List(Ev("e2")))
   assertEquals(m.getEvidences(K("y"), V("v")), None)
  }

  test("Can combine 2 empty typings") {
      val t1: Typing[K, V, Er, Ev] = Typing.empty
      val t2: Typing[K, V, Er, Ev] = Typing.empty
    assertEquals(t1.combineTyping(t2), t1)
  }

  test("Can combine empty with (x -> (v,e1)) and return (x -> (v,e1))") {
      val t1: Typing[K, V, Er, Ev] = Typing.empty
      val t2: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
      assertEquals(t1.combineTyping(t2), t2)
  }
  
  test("Can combine (x -> (v,e1)) with empty and return (x -> (v,e1))") {
      val t1: Typing[K, V, Er, Ev] = Typing.empty
      val t2: Typing[K, V, Er, Ev] = Typing.empty
        .addType(K("x"), V("v"), List(Ev("e1")))
      assertEquals(t2.combineTyping(t1), t2)
  }

  test("Should add not evidence when there is no failed value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.addNotEvidence(K("x"), V("b"), Er("E1"))
      assertEquals(t2.getOkValues(K("x")), Set(V("a")))
      assertEquals(t2.getFailedValues(K("x")), Set(V("b")))
  }
  
  test(s"Should add not evidence when there is failed value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.addNotEvidence(K("x"), V("b"), Er("E1"))
      assertEquals(t2.getOkValues(K("x")), Set(V("a")))
      assertEquals(t2.getFailedValues(K("x")), Set(V("b")))
  }

  test(s"Should add not evidence when there is a positive value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.addNotEvidence(K("x"), V("a"), Er("E1"))
      assertEquals(t2.getOkValues(K("x")).toList, List())
      assertEquals(t2.getFailedValues(K("x")), Set((V("a"))))
  }

  test(s"Should remove values") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty.addType(K("x"), V("a"), List(Ev("e1")))
      val t2 = t1.removeValue(K("x"),V("a"))  
      assertEquals(t2.getOkValues(K("x")).toList, List())
  }

  test(s"Should remove values 2") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("x"), V("b"), List(Ev("e2")))
      val t2 = t1.removeValue(K("x"),V("a"))  
      assertEquals(t2.getOkValues(K("x")).toList, List(V("b")))
  }

  test(s"Should remove no values if it doesn't exist") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("x"), V("b"), List(Ev("e2")))
      val t2 = t1.removeValue(K("y"),V("a"))  
      assertEquals(t2.getOkValues(K("x")).toList, List(V("a"),V("b")))
      assertEquals(t2.getOkValues(K("y")).toList, List())
  }

  test(s"Should remove no values if it doesn't have that value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("y"), V("b"), List(Ev("e2")))
      val t2 = t1.removeValue(K("x"),V("b"))  
      assertEquals(t2.getOkValues(K("x")).toList, List(V("a")))
      assertEquals(t2.getOkValues(K("y")).toList, List(V("b")))
  } 
  
  test("Should remove no values if it doesn't have that value") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("x"), V("b"), List(Ev("e2")))
        .addType(K("y"), V("a"), List(Ev("e3")))
      val t2 = t1.removeValuesWith(v => v.s == "a")  
      assertEquals(t2.getOkValues(K("x")).toList, List(V("b")))
      assertEquals(t2.getOkValues(K("y")).toList, List())
  } 

  test(s"Should negate values") {
      val t1: Typing[K, V, Er, Ev] =
        Typing.empty
        .addType(K("x"), V("a"), List(Ev("e1")))
        .addType(K("x"), V("b"), List(Ev("e2")))
        .addType(K("y"), V("a"), List(Ev("e3")))
      val t2 = t1.negateValuesWith(v => v.s == "a", Er("e"))  
      assertEquals(t2.getOkValues(K("x")).toList, List(V("b")))
      assertEquals(t2.getOkValues(K("y")).toList, List())
      assertEquals(t2.getFailedValues(K("x")).toList, List(V("a")))
  }  

}
