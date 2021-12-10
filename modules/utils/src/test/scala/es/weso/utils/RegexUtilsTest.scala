package es.weso.utils

import munit._ 

class RegexUtilsTest extends FunSuite {
  shouldMatch("[a-z]", Some("i"), "A") // Case insensitive
  shouldNotMatch("[a-z]", None, "A") // Do not match
  shouldMatch("[a-z].[a-z]", Some("s"), "a\nb") // Dot matches a newline
  shouldNotMatch("[a-z].[a-z]", None, "a\nb") // Dot matches a newline
  shouldMatch("[a-z].[a-z]", Some("si"), "A\nB") // Multiple patterns
  shouldMatch("[a-z].[a-z]", Some("isi"), "A\nB") // Duplicate flags
  shouldMatch("\\d{2}", None, "34") // Match
  shouldNotMatch("\\[a-z]", None, "A") // Error while processing regex


  def shouldMatch(regex: String, flags: Option[String], str: String): Unit = {
    test(s"should match /$regex/${flags.getOrElse("")} with $str") {
      RegEx(regex, flags).matches(str) match {
        case Right(true) => () // info(s"$str matches /$regex/${flags.getOrElse("")}")
        case _ => fail(s"Execution of regex on $str was expected to match: /$regex/${flags.getOrElse("")}")
      }
    }
  }

  def shouldNotMatch(regex: String, flags: Option[String], str: String): Unit = {
    test(s"should not match /$regex/${flags.getOrElse("")} with $str") {
      RegEx(regex, flags).matches(str) match {
        case Right(false) => () // info(s"$str doesn't match /$regex/${flags.getOrElse("")}")
        case _ => fail(s"Execution of regex on $str was expected not to match: /$regex/${flags.getOrElse("")}")
      }
    }
  }

  def ErrorWhileMatch(regex: String, flags: Option[String], str: String): Unit = {
    test(s"should fail when trying to match /$regex/${flags.getOrElse("")} with $str") {
      RegEx(regex, flags).matches(str) match {
        case Left(msg) => () // info(s"Error $msg trying to match $str with /$regex/${flags.getOrElse("")}")
        case _ => fail(s"Execution of regex on $str was expected to fail: /$regex/${flags.getOrElse("")}")
      }
    }
  }
}