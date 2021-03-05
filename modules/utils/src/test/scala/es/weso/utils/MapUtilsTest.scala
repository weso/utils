package es.weso.utils

import es.weso.utils.MapUtils._
import org.scalatest.funspec._
import org.scalatest.matchers.should._

class MapUtilsTest extends AnyFunSpec with Matchers {

  describe("MapUtils mapMap") {
    it(s"Should convert map of map") {
      val mm: Map[String,Map[String,Int]] = Map(
        "foo" -> Map("a" -> 2, "b" -> 3),
        "bar" -> Map("c" -> 3)
      )
      val mm1: Map[String,Map[String,Int]] = Map(
        "foo1" -> Map("a1" -> 2, "b1" -> 3),
        "bar1" -> Map("c1" -> 3)
      )
      def add1(x: String): String = x ++ "1"
      val r: Map[String,Map[String,Int]] = cnvMapMap(mm, add1, add1, identity[Int])
      r should be(mm1)
    }
  }

  describe("MapUtils combineMaps") {
    it(s"Should combine map") {
      val mm: Map[String,Int] = Map(
        "a" -> 1,
        "b" -> 2,
        "c" -> 3,
      )
      val mm1: Map[String,Int] = Map(
        "a" -> 4,
        "b" -> 5,
        "c" -> 6,
      )
      val mm2: Map[String,Int] = Map(
        "d" -> 7,
      )

      val comb: Map[String, Int] = combineMaps(List(mm, mm1, mm2))

      comb should be(
        Map(
          "a" -> 5,
          "b" -> 7,
          "c" -> 9,
          "d" -> 7,
        )
      )
    }
  }

}