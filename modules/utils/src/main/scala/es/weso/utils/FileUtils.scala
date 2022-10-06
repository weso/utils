package es.weso.utils
import java.io._
import java.nio.file.Paths
import cats.effect._
import scala.io._
import java.nio.file.Path
import fs2._
import fs2.io.file.Files
import java.nio.file.NoSuchFileException
import scala.util.control.NoStackTrace
import cats.implicits._

object FileUtils {

  def getFilesFromFolderWithExt(
      path: String,
      ext: String,
      ignoreFiles: List[String]
  ): IO[List[File]] = IO {
    val d = new File(path)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter { file =>
        if (file.isFile) {
          val (name, extension) = splitExtension(file.getName)
          extension == ext && !ignoreFiles.contains(name)
        } else false
      }.toList
    } else {
      List[File]()
    }
  }

  def getFileFromFolderWithSameExt(file: File, oldExt: String, newExt: String): IO[File] = IO {
    val newName = file.getAbsolutePath.reverse.replaceFirst(oldExt.reverse, newExt.reverse).reverse
    new File(newName)
    /*   Try {
      new File(newName)
    }.fold(exc =>
      Left(s"Error accessing file with name $newName: ${exc.getMessage}"),
      Right(_)) */
  }

  def getFileFromFolderWithExt(path: String, name: String, ext: String): IO[File] = IO {
    new File(path + "/" + name + "." + ext)
  }

  def splitExtension(str: String): (String, String) = {
    val splits = str.split('.')
    (splits.init.mkString("."), splits.last)
  }

  /** Gets the contents of a file
    *
    * @param fileName
    *   name of the file
    */
  def getContents(path: Path): IO[String] = {
    val decoder: Pipe[IO, Byte, String] = text.utf8Decode
    Files[IO]
      .readAll(path, 4096)
      .through(decoder)
      .compile
      .string
      .handleErrorWith(e => IO.raiseError(GetContentsException(path)))
  }

  case class GetContentsException(path: Path)
      extends NoSuchFileException(
        s"""|Error obtaining contents from file ${path.toFile().getAbsolutePath()}""".stripMargin
      )
      with NoStackTrace

  def getStream(fileName: String): Either[String, InputStreamReader] =
    try {
      val source = Source.fromFile(fileName)("UTF-8")
      source.reader().asRight
    } catch {
      case e: FileNotFoundException =>
        Left(s"Error reading file ${fileName}: ${e.getMessage}")
      case e: IOException =>
        Left(s"IO Exception reading file ${fileName}: ${e.getMessage}")
      case e: Exception =>
        Left(s"Exception reading file ${fileName}: ${e.getMessage}")
    }

  /** Write contents to a file
    *
    * @param name
    *   name of the file
    * @param contents
    *   contents to write to the file
    */
  def writeFile(name: String, contents: String): IO[Unit] = {
    val path = Paths.get(name)
    Stream
      .emits(contents)
      .covary[IO]
      .chunkN(4096)
      .map(_.toVector.mkString)
      .through(text.utf8Encode)
      .through(Files[IO].writeAll(path))
      .compile
      .drain
  }

  /** Format a char sequence including the line numbers
    * @param cs
    * @return
    *   String with the line numbers of the char sequence
    */
  def formatLines(cs: CharSequence): String =
    cs.toString.linesIterator.zipWithIndex.map(p => (p._2 + 1).toString + " " + p._1).mkString("\n")

  lazy val currentFolderURL: String =
    Paths.get(".").normalize.toUri.toURL.toExternalForm
//    ""

}
