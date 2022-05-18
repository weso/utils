package es.weso.utils

import java.util.regex.Pattern

case class RegEx(pattern: String, maybeFlags: Option[String]) {
  val cleanPattern = cleanBackslashes(pattern)

  def cleanBackslashes(str: String): String = {
    // str.replaceAllLiterally("\\\\d", "\\d")
    str.replace("\\\\d", "\\d")
  }

  def intFlags(flags: String): Int = {
    flags.foldLeft(0)((flagAcc,c) => c match {
      case 'i' => flagAcc | Pattern.CASE_INSENSITIVE
      case 'd' => flagAcc | Pattern.UNIX_LINES
      case 'm' => flagAcc | Pattern.MULTILINE
      case 's' => flagAcc | Pattern.DOTALL
      case 'u' => flagAcc | Pattern.UNICODE_CASE
      case 'x' => flagAcc | Pattern.COMMENTS
      case 'U' => flagAcc | Pattern.UNICODE_CHARACTER_CLASS
    })
  }

  def matches(str: String): Either[String, Boolean] = {
    // println(s"Pattern: $pattern\ncleanPattern: $cleanPattern")
    // println(s"str: $str")
    // println(s"re: $cleanPattern: chars: ${cleanPattern.map(c => c.toInt).mkString(",")}")
    try {
      val pattern = maybeFlags match {
        case Some(value) => Pattern.compile(cleanPattern, intFlags(value))
        case None => Pattern.compile(cleanPattern)
      }
      // println(s"pattern: $pattern")
      Right(pattern.matcher(str).find())
    } catch {
      case e: Exception =>
        Left(s"Error: $e, matching $str with /$cleanPattern/${maybeFlags.getOrElse("")}")
    }
  }
}

/*object RegexUtils {

  def makeRegex(pattern: String,
                flags: Option[String]): Either[String,RegEx] = {
    Right(RegEx(pattern,flags))
  }

  def regexMatch(pattern: RegEx, str: String): Either[String,Boolean] = {
    pattern.matches(str)
  }

}*/ 