package es.weso.utils
import munit._
import es.weso.utils.internal.CollectionCompat._

class SetUtilsTest extends FunSuite {

  def shouldCalculatePSet[A: Ordering](s: Set[A], expected: LazyList[(Set[A], Set[A])]): Unit = {

    test(s"Should calculate pSet($s) and return $expected") {
      assertEquals(SetUtils.pSet(s), expected)
    }
  }

  shouldCalculatePSet(
    Set(1, 2),
    LazyList(
      (Set(1, 2), Set[Int]()),
      (Set(2), Set(1)),
      (Set(1), Set(2)),
      (Set[Int](), Set(1, 2))
    )
  )

  shouldCalculatePSet(
    Set(1, 2, 3),
    LazyList(
      (Set(1, 2, 3), Set[Int]()),
      (Set(2, 3), Set(1)),
      (Set(1, 3), Set(2)),
      (Set(3), Set(1, 2)),
      (Set(1, 2), Set(3)),
      (Set(2), Set(1, 3)),
      (Set(1), Set(2, 3)),
      (Set[Int](), Set(1, 2, 3))
    )
  )

  shouldCalculatePSet(Set[Int](), LazyList((Set[Int](), Set[Int]())))

  shouldCalculatePSet(
    Set(1),
    LazyList(
      (Set(1), Set[Int]()),
      (Set[Int](), Set(1))
    )
  )

  def shouldCalculatePartition[A](s: Set[A], n: Int, expected: LazyList[List[Set[A]]]): Unit = {
    test(s"Should calculate partition($s,$n) and return $expected") {
      assertEquals(SetUtils.partition(s, n), expected)
    }
  }

  shouldCalculatePartition(
    Set(1, 2),
    1,
    LazyList(
      List(Set(1, 2))
    )
  )

  shouldCalculatePartition(
    Set(1, 2),
    2,
    LazyList(List(Set(1, 2), Set[Int]()), List(Set(2), Set(1)), List(Set(1), Set(2)), List(Set[Int](), Set(1, 2)))
  )

  shouldCalculatePartition(
    Set(1, 2),
    3,
    LazyList(
      List(Set(1, 2), Set[Int](), Set[Int]()),
      List(Set(2), Set(1), Set[Int]()),
      List(Set(2), Set[Int](), Set(1)),
      List(Set(1), Set(2), Set[Int]()),
      List(Set(1), Set[Int](), Set(2)),
      List(Set[Int](), Set(1, 2), Set[Int]()),
      List(Set[Int](), Set(2), Set(1)),
      List(Set[Int](), Set(1), Set(2)),
      List(Set[Int](), Set[Int](), Set(1, 2))
    )
  )

  test(s"Should raise error with wrong argument") {
    intercept[Exception](SetUtils.partition(Set(1, 2), 0))
    intercept[Exception](SetUtils.partition(Set(1, 2), -1))
  }
}
