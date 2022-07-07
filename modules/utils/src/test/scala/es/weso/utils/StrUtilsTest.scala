package es.weso.utils

import munit._
import StrUtils._

class StrUtilsTest extends FunSuite {

  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\", "\\")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\tpepe", "\tpepe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\bpepe", "\bpepe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\npepe", "\npepe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\rpepe", "\rpepe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\fpepe", "\fpepe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\'pepe", "\'pepe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "pepe\\u0031", "pepe1")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\u0031pepe\\u0031", "1pepe1")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\u0032\\u00ac\\u0031", "2¬1")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "pe\\-pe", "pe-pe")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\\\", "\\")
  shouldConvert("unescapeStringLiteral", unescapeStringLiteral, "\\U0001D4B8", "\uD835\uDCB8")

  shouldConvert("escapeStringLiteral", escapeStringLiteral, "\tpepe", "\\tpepe")
  shouldConvert("escapeStringLiteral", escapeStringLiteral, "pepe1", "pepe1")
  shouldConvert("escapeStringLiteral", escapeStringLiteral, "1pepe1", "1pepe1")
  shouldConvert("escapeStringLiteral", escapeStringLiteral, "2\u00ac1", "2\u00ac1")

  shouldConvert("unescapePattern", unescapePattern, "\\u0061", "a")
  shouldConvert("unescapePattern", unescapePattern, "\\\\u0061", "\\\\u0061")

  shouldConvert(
    "unescapeIRI",
    unescapeIRI,
    "\\thttp://www.w3.org/1999/02/22-rdf-syntax-ns",
    "\thttp://www.w3.org/1999/02/22-rdf-syntax-ns"
  )

  shouldConvert("unescapeCode", unescapeCode, "\\\"Hello\\r\\n\\tW\"orld\\n", "\"Hello\r\n\tW\"orld\n")

  shouldConvert("escapePattern", escapePattern, "\tpepe", "\tpepe")
  shouldConvert("escapePattern", escapePattern, "pepe1", "pepe1")
  shouldConvert("escapePattern", escapePattern, "1pepe1", "1pepe1")
  shouldConvert("escapePattern", escapePattern, "2\u00ac1", "2\u00ac1")
  shouldConvert("escapePattern", escapePattern, "2\\^1", "2\\^1")
  // shouldUnescape("\\U0001D4B8","\uD835\uDCB8")

  shouldConvert("escapeDot", escapeDot, "pepe", "pepe")
  shouldConvert("escapeDot", escapeDot, "pepe1", "pepe1")
  shouldConvert("escapeDot", escapeDot, "1pepe1", "1pepe1")
  shouldConvert("escapeDot", escapeDot, "2\"1", "2&#34;1")
  shouldConvert("escapeDot", escapeDot, "2¥1", "2&#165;1")

  def shouldConvert(name: String, cnv: String => String, str: String, expected: String): Unit = {
    test(s"$name should convert $str and obtain $expected") {
      assertEquals(cnv(str), expected)
    }
  }
}
