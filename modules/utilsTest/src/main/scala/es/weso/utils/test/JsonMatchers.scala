package es.weso.utils.test
import io.circe._
import io.circe.parser._
import cats.implicits._

trait JsonMatchers {

  def assertJsonStrEquals(str1: String, str2: String): Either[String, Unit] = for {
    json1 <- parse(str1).leftMap(e => s"Error parsing json1: ${e}\nString: ${str1}")
    json2 <- parse(str2).leftMap(e => s"Error parsing json2: ${e}\nString: ${str1}")
    cmp <-
      if (json1 == json2) Right(())
      else
        Left(
          s"Decoded jsons are different\nJson1:${json1.spaces2}\nJson2:${json2.spaces2}\nStr1:${str1}\nStr2:${str2} "
        )
  } yield cmp

  def assertJsonEquals(json1: Json, json2: Json): Either[String, Unit] = for {
    cmp <-
      if (json1 == json2) Right(())
      else Left(s"Decoded jsons are different\nJson1:${json1.spaces2}\nJson2:${json2.spaces2}")
  } yield cmp

}

/** This line allows to import JsonMatchers without requiring to extend the trait
  */
object JsonMatchers extends JsonMatchers
