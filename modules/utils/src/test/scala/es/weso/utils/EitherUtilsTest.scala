package es.weso.utils
import EitherUtils._
import org.scalatest._

class EitherUtilsTest extends FunSpec with Matchers {

 describe(s"EitherUtils sequence") {
     it(s"Calculates a sequence") {
         val les: List[Either[String,Int]] = List(Right(1), Right(2))
         val els: Either[String,List[Int]] = sequence(les)
         els.fold(e => fail(s"Should have a value"), v => v.length should(be (2)))
     }
 }
}