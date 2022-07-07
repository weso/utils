package es.weso.utils.testsuite
import cats._
import cats.implicits._
import cats.effect._

case class TestEntry(id: TestId, action: IO[TestResult]) {

  def info(msg: String, config: TestConfig): IO[Unit] =
    if (config.verbose) IO.println(msg)
    else IO.pure(())

  def runEntry(r: Ref[IO, Stats], config: TestConfig): IO[TestResult] =
    for {
      _ <- info(s"<<Running ${id.show}", config)
      pair <- Clock[IO].timed(
        GenTemporal[IO]
          .timeoutTo(
            action,
            config.maxTimePerTest,
            IO.raiseError(TimeOut(id.id, config.maxTimePerTest))
          )
          .attempt
      )
      (time, either) = pair
      result <- either.fold(
        e => {
          val res = FailedResult(id, exception = Some(e))
          r.getAndUpdate(_.addResult(this, res)) *>
            info(s">>Result failed for ${id.show}: ${res.show}", config) *>
            IO.pure(res)
        },
        res => {
          r.getAndUpdate(_.addResult(this, res)) *>
            info(s">>Result ${if (res.passed) "passed" else "failed"} for ${id.show}: ${res.show}", config) *>
            IO.pure(res)
        }
      )
    } yield result

}

object TestEntry {
  implicit val showEntry: Show[TestEntry] = new Show[TestEntry] {
    def show(e: TestEntry): String = s"name: ${e.id.show}"
  }
}
