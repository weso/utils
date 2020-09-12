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

}


