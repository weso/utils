package es.weso.utils.internal
import munit._
import CollectionCompat._

class CollectionCompatTest extends FunSuite {
  
    test("CollectionCompat. Should create a collection") {
            val s: List[Int] = List(1,2,3,4)
            val ls = s.toLazyList
            assertEquals(ls.length, 4)
    }


    test("Map filterKeys, should filter keys") {
            val m: Map[Int,String] = Map(1 -> "a", 2 -> "b", 3 -> "c" )
            assertEquals(filterKeys(m)(_ % 2 == 0).toList, List( (2,"b")))
    }

    test("Updated with. Update with") {
            val m: Map[Int,String] = Map(1 -> "a", 2 -> "b", 3 -> "c" )
            def f(v: Option[String]): Option[String] = v match {
                case None => None
                case Some("b") => Some("d")
                case Some(other) => Some(other)
            }
            assertEquals(updatedWith(m)(2)(f).toList, List((1,"a"),(2,"d"),(3,"c")))
    }

}


