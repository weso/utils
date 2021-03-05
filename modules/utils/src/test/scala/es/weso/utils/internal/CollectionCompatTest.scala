package es.weso.utils.internal
import org.scalatest.funspec._
import org.scalatest.matchers.should._
import CollectionCompat._

class CollectionCompatTest extends AnyFunSpec with Matchers {
  
    describe("CollectionCompat") {
        it("Should create a collection") {
            val s: List[Int] = List(1,2,3,4)
            val ls = s.toLazyList
            ls.length should(be(4))
        }
    }


    describe("Map filterKeys") {
        it("Should filter keys") {
            val m: Map[Int,String] = Map(1 -> "a", 2 -> "b", 3 -> "c" )
            filterKeys(m)(_ % 2 == 0) should contain theSameElementsAs List( (2,"b"))
        }
    }

    describe("Updated with") {
        it("Update with") {
            val m: Map[Int,String] = Map(1 -> "a", 2 -> "b", 3 -> "c" )
            def f(v: Option[String]): Option[String] = v match {
                case None => None
                case Some("b") => Some("d")
                case Some(other) => Some(other)
            }
            updatedWith(m)(2)(f) should contain theSameElementsAs List((1,"a"),(2,"d"),(3,"c"))
        }
    }

}


