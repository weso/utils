package es.weso.utils
// Read type class
// History: I had written a read type class inspired by Haskell some time ago
// I later found this nice project:https://github.com/ChristopherDavenport/read
// To avoid adding more dependencies, I merged that project with my previous type class

trait Read[A] {

  def read(x: String): Either[Throwable, A]

  final def unsafeRead(s: String): A =
    read(s).fold(e => throw e, identity)

}

object Read {

  def apply[A](implicit ev: Read[A]): Read[A] = ev

  def readNonFatal[A](f: String => A): Read[A] = new Read[A] {
    override def read(s: String): Either[Throwable, A] =
      catchNonFatal(f(s))
  }

  private def catchNonFatal[A](a: => A): Either[Throwable, A] = {
    import scala.util.control.NonFatal
    try {
      Right(a)
    } catch {
      case NonFatal(e) => Left(e)
    }
  }

  // Example instance
  implicit val readInt: Read[Int]               = readNonFatal(_.toInt)
  implicit val readLong: Read[Long]             = readNonFatal(_.toLong)
  implicit val readShort: Read[Short]           = readNonFatal(_.toShort)
  implicit val readDouble: Read[Double]         = readNonFatal(_.toDouble)
  implicit val readString: Read[String]         = readNonFatal(identity)
  implicit val readBigDecimal: Read[BigDecimal] = readNonFatal(BigDecimal(_))
  implicit val readBigInt: Read[BigInt]         = readNonFatal(BigInt(_))
  implicit val readBoolean: Read[Boolean]       = readNonFatal(_.toBoolean)

  // Syntax
  implicit class ReadStringOps(s: String) {
    def read[A: Read]: Either[Throwable, A] =
      Read[A].read(s)
  }

}
