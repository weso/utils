package es.weso.utils.json
import io.circe._
import cats._
import cats.implicits._
import io.circe.syntax._
import io.circe.parser._

trait JsonTest {

 def decodeJsonEncodeEquals[A: Encoder: Decoder: Show](str: String): Either[String,Unit] = {
    for {
      json   <- parse(str).leftMap(e => s"decodeJsonEncodeEquals\nError decoding $str: $e")
      value <- json.as[A].leftMap(e => s"decodeJsonEncodeEquals\nError obtainning encoding value from decoded Json\nError: $e\nJson decoded:\n${json.show}\nString: $str")
      jsonEncoded = value.asJson
      check <- if (json.equals(jsonEncoded)) Right(())
      else
        Left(s"decodeJsonEncodeEquals: Jsons and different: \nJson1:\n${json.spaces2}\nEncoded:\n${jsonEncoded.spaces2}\nValue:${value.show}")
    } yield check
  } 
}