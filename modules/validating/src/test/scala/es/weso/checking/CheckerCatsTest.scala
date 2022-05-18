package es.weso.checking
import java.util.concurrent.atomic.AtomicInteger
import es.weso.utils.internal.CollectionCompat._
// import cats.implicits._
import cats.effect.IO
import munit._

class CheckerCatsTest extends CatsEffectSuite {

  import CheckerCatsStr._

  def runValue_(c: Check[Int]): IO[Either[Err, Int]]                          = runValue(c)(c0)(e0)
  def runLog_(c: Check[Int]): IO[Log]                                         = runLog(c)(c0)(e0)
  def runValueFlag(c: Check[(Int, Boolean)]): IO[Either[Err, (Int, Boolean)]] = runValue(c)(c0)(e0)

  test("Should be able to return a value") {
    val c: Check[Int] = ok(2)
    assertIO(runValue_(c), Right(2))
  }

  test("Should be able to return an error") {
    val msg           = "Error"
    val e: Check[Int] = err(msg)
    assertIO(runValue_(e), Left(msg))
  }

  test("Should be able to do an or...") {
    val c: Check[Int]  = ok(2)
    val c3: Check[Int] = ok(3)
    val e: Check[Int]  = err("Err")
    val e1: Check[Int] = err("Err1")
    assertIO(runValue_(orElse(c, e)), Right(2))
    assertIO(runValue_(orElse(e, c)), Right(2))
    assertIO(runValue_(orElse(c, c3)), Right(2))
    assertIO(runValue_(orElse(e, e1)), Left("Err1"))
  }
  test("Should be able to do checkSome...") {
    val c1: Check[Int] = ok(1)
    val c2: Check[Int] = ok(2)
    val e: Check[Int]  = err("Err")
    val e1: Check[Int] = err("Err1")
    assertIO(runValue_(checkSome(List(c1, e), "No one")), Right(1))
    assertIO(runValue_(checkSome(List(e, c2, e), "No one")), Right(2))
    assertIO(runValue_(checkSome(List(e, e1), "No one")), Left("No one"))
    assertIO(runValue_(checkSome(List(c1, c2), "No one")), Right(1))
  }
  /*      it("Should be able to run local") {
        def addEnv(name: String, value: Int): Env => Env =
          _.updated(name, value)

        val getX: Check[Option[Int]] = for {
          env <- getEnv
        } yield (env.get("x"))
        runValue(getX)(c0)(e0) should ===(Right(None))
        runValue(local(addEnv("x", 1))(getX))(c0)(e0) should ===(Right(Some(1)))

        val c: Check[Option[Int]] = local(addEnv("x", 1))(getX) >> getX
        runValue(c)(c0)(e0) should ===(Right(None))
      }
      it("Should be able to collect a single log") {
        val x1: Check[Int] = for {
          _ <- logStr("L1")
        } yield 1
        val log = runLog_(x1)
        log should ===(List("L1"))
      }
      it("Should be able to collect two logs") {
        val x1: Check[Int] = for {
          _ <- logStr("L1")
        } yield 1
        val x2: Check[Int] = for {
          _ <- logStr("L2")
        } yield 1
        val log = runLog_(x1 >> x2)
        log should ===(List("L1", "L2"))
      }
      it("Should be able to collect two logs with checkSome") {
        val x: Check[Int] = logStr("L1") >> ok(1)
        val e: Check[Int] = logStr("E") >> err("Err")
        runLog_(checkSome(List(x, e), "NoOne")) should ===(List("L1"))
        runLog_(checkSome(List(e, x), "NoOne")) should ===(List("E", "L1"))
        runLog_(checkSome(List(e, e), "NoOne")) should ===(List("E", "E"))
      }
      it("Should be able to execute cond for some successful computation") {
        lazy val x1: Check[Int] = logStr("x1") >> ok(1)
        lazy val x2: Check[Int] = logStr("x2") >> ok(2)
        lazy val e: Check[Int] = logStr("E") >> err("Err")
        lazy val c1 = cond(x1, (_: Int) => x2, _ => e)
        runValue_(c1) should ===(Right(2))
        runLog_(c1) should ===(List("x1", "x2"))
      }
      it("Should be able to execute cond for some fail computation") {
        lazy val x1: Check[Int] = logStr("x1") >> ok(1)
        lazy val x2: Check[Int] = logStr("x2") >> ok(2)
        lazy val e: Check[Int] = logStr("E") >> err("Err")
        lazy val c1 = cond(x1, (_: Int) => e, _ => x2)
        runValue_(c1) should ===(Left("Err"))
        runLog_(c1) should ===(List("x1", "E"))
      }
/*      it("Should be able to update info") {
        def add(x: Int): CheckInfo => CheckInfo = x :: _
        def c : Check[Int] = logStr("X") >> updateInfo(add(1)) >> ok(2)
        runCheck(c)(c0)(e0) should ===(List("X"),(Right(2),List(1)))
      } */
 } */

  {
    val counter = new AtomicInteger(0)
    def comp(x: Int): Check[(Int, Boolean)] = {
      counter.getAndIncrement;
      // println(s"Comp($x), steps: $counter")
      if (x % 2 == 0) {
        ok((x, true))
      } else {
        ok((x, false))
      }
    }
    shouldCheckSomeFlag(
      "checkSomeFlag(LazyList(2,4), (0,false)) = (2, true)|1",
      LazyList(2, 4),
      comp,
      ok((0, false)),
      (2, true),
      1
    )

// TODO: We commented the following tests because they fail...check if the use of AtomicInteger has a conflict with IOs
//  shouldCheckSomeFlag("checkSomeFlag(LazyList1,4), (0,false)) = (4, true)|2",LazyList(1, 4),comp,ok((0,false)),(4,true),2)

// shouldCheckSomeFlag("checkSomeFlag(LazyList(1,3,5), (0,false)) = (0, false)|3",LazyList(1, 3, 5),comp,ok((0,false)),(0,false),3)
// shouldCheckSomeFlag("checkSomeFlag(LazyList(2,4,...), (0,false)) = (2, true)|1",LazyList.from(2,2),comp,ok((0,false)),(2,true),1)
// shouldCheckSomeFlag("checkSomeFlag(LazyList(), (0,false)) = (0, false)|0",LazyList(),comp,ok((0,false)),(0,false),0)

    def shouldCheckSomeFlag(
        msg: String,
        ls: => LazyList[Int],
        check: Int => Check[(Int, Boolean)],
        last: => Check[(Int, Boolean)],
        expected: (Int, Boolean),
        stepsExpected: Int
    ): Unit = {
      test(msg) {
        counter.set(0)
        assertIO(runValueFlag(checkSomeFlag(ls, check, last)), Right(expected))
        assertEquals(counter.get, stepsExpected)
      }
    }
  }

  /*{
 val counter = new AtomicInteger(0)
 def comp(x: Int): Check[(Int,Boolean)] = {
      counter.getAndIncrement;
      // println(s"Comp($x), steps: $counter")
      if (x % 2 == 0) {
        ok((x, true))
      } else {
        ok((x, false))
      }
    }

 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(List(2,4), 0)) = (6, true)|2",LazyList(2, 4),comp,0,(6,true),2)
 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(List(2,4,1), 0)) = (7, false)|3",LazyList(2, 4, 1),comp,0,(7,false),3)
 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(List(2,4,1,2), 0)) = (7, false)|3",LazyList(2, 4, 1,2),comp,0,(7,false),3)
 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(List(2,4,6,2), 0)) = (14, true)|4",LazyList(2, 4, 6, 2),comp,0,(14,true),4)
 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(List(1,4), 0) = (1, false)|1",LazyList(1, 4),comp,0,(1,false),1)
 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(List(1,3,5), 0) = (1, false)|1",LazyList(1, 3, 5),comp,0,(1,false),1)
 shouldCheckAllFailAtFirstFlag("checkAllFailAtFirstFlag(LazyList(), 0) = (0, true)|0",LazyList(),comp,0,(0,true),0)

 def shouldCheckAllFailAtFirstFlag(msg: String,
                           ls: => LazyList[Int],
                           check: Int => Check[(Int,Boolean)],
                           last: => Int,
                           expected: (Int,Boolean),
                           stepsExpected: Int): Unit = {
      test(msg) {
        counter.set(0)
        assertIO(runValueFlag(checkAllFailFAtFirstFlag(ls, check, last)), Right(expected))
        assertEquals(counter.get, stepsExpected)
      }
  }
} */
  /*
{
  val counter = new AtomicInteger(0)
  def comp(x: Int): Check[(Int,Boolean)] = {
      counter.getAndIncrement;
      // println(s"Comp($x), steps: $counter")
      if (x % 2 == 0) {
        ok((x, true))
      } else {
        ok((x, false))
      }
    }

 shouldCheckAllFlag("checkAllFlag(List(2,4), 0)) = (6, true)|2",LazyList(2, 4),comp,0,(6,true),2)
 shouldCheckAllFlag("checkAllFlag(List(2,4,1), 0)) = (7, false)|3",LazyList(2, 4, 1),comp,0,(7,false),3)
 shouldCheckAllFlag("checkAllFlag(List(2,4,1,2), 0)) = (7, false)|4",LazyList(2, 4, 1,2),comp,0,(9,false),4)
 shouldCheckAllFlag("checkAllFlag(List(2,4,6,2), 0)) = (14, true)|4",LazyList(2, 4, 6, 2),comp,0,(14,true),4)
 shouldCheckAllFlag("checkAllFlag(List(1,4), 0) = (1, false)|2",LazyList(1, 4),comp,0,(5,false),2)
 shouldCheckAllFlag("checkAllFlag(List(1,3,5), 0) = (1, false)|3",LazyList(1, 3, 5),comp,0,(9,false),3)
 shouldCheckAllFlag("checkAllFlag(LazyList(), 0) = (0, true)|0",LazyList(),comp,0,(0,true),0)

 def shouldCheckAllFlag(msg: String,
                           ls: => LazyList[Int],
                           check: Int => Check[(Int,Boolean)],
                           last: => Int,
                           expected: (Int,Boolean),
                           stepsExpected: Int): Unit = {
  test(msg) {
   counter.set(0)
   assertIO(runValueFlag(checkAllFlag(ls, check, last)), Right(expected))
   assertEquals(counter.get, stepsExpected)
  }

  }
 } */

  /* {
   val counter = new AtomicInteger(0)
   def comp(x: Int): Check[(Int,Boolean)] = {
      counter.getAndIncrement;
      // println(s"Comp($x), steps: $counter")
      if (x % 2 == 0) {
        ok((x, true))
      } else {
        ok((x, false))
      }
    }


  shouldCheckSequenceFlag("checkSequenceFlag(List(2,4), 0)) = (6, true)",List(comp(2), comp(4)),0,(6,true))
  shouldCheckSequenceFlag("checkSequenceFlag(List(2,1), 0)) = (3, false)",List(comp(2), comp(1)),0,(3,false))
  shouldCheckSequenceFlag("checkSequenceFlag(List(), 0)) = (0, true)",List(),0,(0,true))
  shouldCheckSequenceFlag("checkSequenceFlag(List(1), 0)) = (1, false)",List(comp(1)),0,(1,false))
  shouldCheckSequenceFlag("checkSequenceFlag(List(2), 0)) = (1, false)",List(comp(2)),0,(2,true))
  shouldCheckSequenceFlag("checkSequenceFlag(List(2,2,2), 0)) = (6, true)",List(comp(2), comp(2), comp(2)),0,(6,true))

  def shouldCheckSequenceFlag(msg: String,
                           ls: => List[Check[(Int,Boolean)]],
                           last: => Int,
                           expected: (Int,Boolean)): Unit = {
    test(msg) {
      assertIO(runValueFlag(checkSequenceFlag(ls, last)), Right(expected))
    }
  }

} */

}
