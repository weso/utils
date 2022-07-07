package es.weso.utils

import java.net.URI
import scala.io.Source
import scala.util.Try
import cats.effect.IO

object UriUtils {

  /** Dereferentiate an URI
    * @param uri
    * @return
    *   Contents
    */
  // TODO: Use a more functional approach
  def derefUri(uri: URI): IO[String] = {
    Try {
      val urlCon = uri.toURL.openConnection()
      urlCon.setConnectTimeout(4000)
      urlCon.setReadTimeout(2000)
      val is = urlCon.getInputStream()
      Source.fromInputStream(is).mkString
    }.fold(e => IO.raiseError(e), IO(_))

  }

}
