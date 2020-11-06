package es.weso.utils
import java.io._
import java.nio.file.Paths
import cats.data.EitherT
import cats.effect._
import scala.io._
import java.nio.file.Path
// import util._

object FileUtils {

  def getFilesFromFolderWithExt(
    path: String,
    ext: String,
    ignoreFiles: List[String]): IO[List[File]] = IO {
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

  def getFileFromFolderWithSameExt(
                                 file: File,
                                 oldExt: String,
                                 newExt: String): IO[File] = IO {
   val newName = file.getAbsolutePath.reverse.replaceFirst(oldExt.reverse, newExt.reverse).reverse
   new File(newName)
/*   Try {
      new File(newName)
    }.fold(exc =>
      Left(s"Error accessing file with name $newName: ${exc.getMessage}"),
      Right(_)) */
  }


  def getFileFromFolderWithExt(
    path: String,
    name: String,
    ext: String): IO[File] = IO {
    new File(path + "/" + name + "." + ext)
  }

  def splitExtension(str: String): (String, String) = {
    val splits = str.split('.')
    (splits.init.mkString("."), splits.last)
  }

  /**
   * Ensures to close a file.
   * Follows the [[https://wiki.scala-lang.org/display/SYGN/Loan Loan pattern]]
   */
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }


  /**
   * Gets the contents of a file
   *
   * @param file file
   *
   */
  def getContents(file: File): EitherT[IO, String, CharSequence] = {
    try {
      using(Source.fromFile(file)("UTF-8")) { source =>
        EitherT.pure[IO,String](source.getLines.mkString("\n"))
      }
    } catch {
      case e: FileNotFoundException =>
        EitherT.leftT[IO,CharSequence](s"Error reading file ${file.getAbsolutePath}: ${e.getMessage}")
      case e: IOException =>
        EitherT.leftT[IO,CharSequence](s"IO Exception reading file ${file.getAbsolutePath}: ${e.getMessage}")
      case e: Exception =>
        EitherT.leftT[IO,CharSequence](s"Exception reading file ${file.getAbsolutePath}: ${e.getMessage}")
    }
  }

  /**
   * Gets the contents of a file
   *
   * @param fileName name of the file
   *
   */
  def getContents(path: Path): IO[CharSequence] = {
    val decoder: Pipe[IO,Byte,String] = fs2.text.utf8Decode
    Stream.resource(Blocker[IO]).flatMap(blocker =>
      fs2.io.file.readAll[IO](path, blocker,4096).through(decoder)
    ).compile.string
  }

  /*
  def getContents(fileName: String): EitherT[IO, String, CharSequence] = {
    try {
      using(Source.fromFile(fileName)("UTF-8")) { source =>
        EitherT.pure[IO,String](source.getLines.mkString("\n"))
      }
    } catch {
      case e: FileNotFoundException =>
       EitherT.leftT[IO,CharSequence](s"Error reading file ${fileName}: ${e.getMessage}")
      case e: IOException =>
       EitherT.leftT[IO,CharSequence](s"IO Exception reading file ${fileName}: ${e.getMessage}")
      case e: Exception =>
       EitherT.leftT[IO,CharSequence](s"Exception reading file ${fileName}: ${e.getMessage}")
    }
  } */

  def getStream(fileName: String): Either[String, InputStreamReader] = {
    try {
      using(Source.fromFile(fileName)("UTF-8")) { source =>
        {
          Right(source.reader())
        }
      }
    } catch {
      case e: FileNotFoundException =>
        Left(s"Error reading file ${fileName}: ${e.getMessage}")
      case e: IOException =>
        Left(s"IO Exception reading file ${fileName}: ${e.getMessage}")
      case e: Exception =>
        Left(s"Exception reading file ${fileName}: ${e.getMessage}")
    }
  }
  /**
   * Write contents to a file
   *
   * @param name name of the file
   * @param contents contents to write to the file
   */
  def writeFile(name: String, contents: String): Unit = {
    import java.nio.file._
    val path = Paths.get(name)
    Files.write(path, contents.getBytes)
    ()
  }

  /**
   * Format a char sequence including the line numbers
   * @param cs
   * @return String with the line numbers of the char sequence
   */
  def formatLines(cs: CharSequence): String = {
    cs.toString.linesIterator.zipWithIndex.map(p => (p._2 + 1).toString + " " + p._1).mkString("\n")
  }

  lazy val currentFolderURL: String =
    Paths.get(".").normalize.toUri.toURL.toExternalForm
//    ""

}
