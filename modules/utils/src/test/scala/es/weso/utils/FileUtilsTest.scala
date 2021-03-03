package es.weso.utils

import org.scalatest._
import matchers.should._
import funspec._
import es.weso.utils.FileUtils._
import cats.effect.IO
import java.nio.file.Paths

class FileUtilsTest extends AnyFunSpec with Matchers {

  describe("FileUtils") {

   describe("getContents") {
       it(s"Should be able to get contents") {
        val r: IO[String] = for {
            str <- getContents(Paths.get("modules/utils/src/test/resources/exampleFolder/testFile.txt"))
        } yield str.toString

        r.unsafeRunSync() should be("Hello World!")
      }

      it(s"Should fail if file doesn't exist") {
        val r: IO[String] = for {
            str <- getContents(Paths.get("nonExistent/testFile.txt"))
        } yield str.toString

        an [GetContentsException] should be thrownBy (r.unsafeRunSync())
      }


 }
}

}