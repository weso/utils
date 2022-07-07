package es.weso.utils.test
import io.circe.Json
import munit._

class JsonMatchersTest extends FunSuite with JsonMatchers {

  test("Simple Json test. Should check two json values") {
    val json1 =
      """|{ "key1": 24 }
           |""".stripMargin
    val expected1 =
      """|{ "key1": 24 }
           |""".stripMargin
    assertJsonStrEquals(json1, expected1)
  }

  test(s"Should check two different json values dont match") {
    val json1 =
      """|{ "key1": 24 }
           |""".stripMargin
    val expected1 =
      """|{ "key1": 23 }
           |""".stripMargin
    assertJsonStrEquals(json1, expected1)
  }

  test(s"Should check a Json with another Json values dont match") {
    val json = Json.fromFields(
      List(
        ("key1", Json.fromString("value1")),
        ("key2", Json.fromString("value2"))
      )
    )
    val expected: Json = Json.fromFields(
      List(
        ("key2", Json.fromString("value2")),
        ("key1", Json.fromString("value1"))
      )
    )
    assertJsonEquals(json, expected)
  }

}
