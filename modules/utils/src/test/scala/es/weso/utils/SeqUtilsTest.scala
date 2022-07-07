package es.weso.utils
import SeqUtils._
import munit._

class SeqUtilsTest extends FunSuite {

  test("Should check three elements") {
    val xs: List[(Char, Set[Int])] =
      List(('a', Set(1, 2)), ('b', Set(0)), ('c', Set(3, 4)))
    val expected: List[List[(Char, Int)]] =
      List(
        List(('a', 1), ('b', 0), ('c', 3)),
        List(('a', 1), ('b', 0), ('c', 4)),
        List(('a', 2), ('b', 0), ('c', 3)),
        List(('a', 2), ('b', 0), ('c', 4))
      )
    assertEquals(transpose(xs), expected)
  }
  test("Should check three elements that fail") {
    val xs: List[(Char, Set[Int])] =
      List(('a', Set[Int]()), ('b', Set[Int]()), ('c', Set[Int]()))
    val expected: List[List[(Char, Int)]] = List()
    assertEquals(transpose(xs), expected)
  }
  test("should intersperse 1,2,3 with comma") {
    val xs       = List("1", "2", "3")
    val c        = ","
    val expected = List("1", ",", "2", ",", "3")
    assertEquals(intersperse(c, xs), expected)
  }
  test("should intersperse empty list with comma") {
    val xs       = List[String]()
    val c        = ","
    val expected = List[String]()
    assertEquals(intersperse(c, xs), expected)
  }
  test("should intersperse singleton list with comma") {
    val xs       = List("1")
    val c        = ","
    val expected = List("1")
    assertEquals(intersperse(c, xs), expected)
  }
  test(s"should zipN [[1,2],[4],[5,6]]") {
    val xs = List(List("1", "2"), List("4"), List("5", "6"))
    val expected =
      List(List("1", "4", "5"), List("1", "4", "6"), List("2", "4", "5"), List("2", "4", "6"))
    assertEquals(zipN(xs), expected)
  }
  test(s"should zipN [[1,2],[4],[5,6,7]]") {
    val xs = List(List("1", "2"), List("4"), List("5", "6", "7"))
    val expected =
      List(
        List("1", "4", "5"),
        List("1", "4", "6"),
        List("1", "4", "7"),
        List("2", "4", "5"),
        List("2", "4", "6"),
        List("2", "4", "7")
      )
    assertEquals(zipN(xs), expected)
  }
  test(s"should zipN [[]]") {
    val xs = List(List())
    val expected =
      List(List())
    assertEquals(zipN(xs), expected)
  }
  test(s"should zipN [[],[],[],[]]") {
    val xs = List(List(), List(), List(), List())
    val expected =
      List(List())
    assertEquals(zipN(xs), expected)
  }
  test(s"should zipN [[1],[],[5,6],[]]") {
    val xs = List(List("1"), List(), List("5", "6"), List())
    val expected =
      List(List("1", "5"), List("1", "6"))
    assertEquals(zipN(xs), expected)
  }
  test(s"should zipN [[1,2],[],[5,6]]") {
    val xs = List(List("1", "2"), List(), List("5", "6"))
    val expected =
      List(List("1", "5"), List("1", "6"), List("2", "5"), List("2", "6"))
    assertEquals(zipN(xs), expected)
  }
}
