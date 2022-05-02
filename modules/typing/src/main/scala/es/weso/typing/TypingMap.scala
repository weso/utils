package es.weso.typing
import cats._, data._
import cats.implicits._
import TypingResult._
import es.weso.utils.internal.CollectionCompat.{updatedWith => updWith, filterKeys => filterKs}
import scala.collection._

case class TypingMap[Key, Value, Err, Evidence](
    m: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]]
) extends Typing[Key, Value, Err, Evidence] {

  override def allOk: Boolean = {
    m.keys.map(key => getFailedValues(key).isEmpty).forall(identity)
  }

  override def getFailed: List[Key] = {
    m.keys.filter(key => !getFailedValues(key).isEmpty).toList
  }

  override def getKeys: Seq[Key] =
    m.keys.toSeq

  override def getValues(key: Key): immutable.Map[Value, TypingResult[Err, Evidence]] =
    m.get(key).getOrElse(immutable.Map())

  override def getOkValues(key: Key): Set[Value] = {
    getValues(key).filter(p => p._2.isOK).keySet
  }

  override def getEvidences(key: Key, value: Value): Option[List[Evidence]] = for {
    mvalue       <- m.get(key)
    typingResult <- mvalue.get(value)
    evidences    <- typingResult.getEvidences
  } yield evidences

  def getFailedValues(key: Key): Set[Value] = {
    getValues(key).filter(p => !p._2.isOK).keySet
  }

  def getResult(key: Key, value: Value): Option[TypingResult[Err, Evidence]] = {
    m.get(key) match {
      case None     => None
      case Some(mm) => mm.get(value)
    }
  }

  def firstEvidences(es: List[Evidence]): TypingResult[Err, Evidence] = {
    TypingResult(Validated.valid(es))
  }

  def firstNotEvidence(e: Err): TypingResult[Err, Evidence] = {
    TypingResult(Validated.invalid(NonEmptyList.of(e)))
  }

  override def addEvidences(key: Key, value: Value, es: List[Evidence]): Typing[Key, Value, Err, Evidence] = {
    val newTyping: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] =
      m.updated(
        key,
        if (m.get(key).isDefined) {
          val valueMap = m(key)
          valueMap.updated(
            value,
            if (valueMap.get(value).isDefined) {
              valueMap(value).addEvidences(es)
            } else {
              TypingResult(Validated.valid(es))
            }
          )
        } else (immutable.Map(value -> firstEvidences(es)))
      )
    TypingMap(newTyping)
  }

  override def addEvidence(key: Key, value: Value, e: Evidence): Typing[Key, Value, Err, Evidence] =
    addEvidences(key, value, List(e))

  override def getMap = m

  override def addNotEvidence(key: Key, value: Value, e: Err): Typing[Key, Value, Err, Evidence] = {
    val newTyping: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] = m.updated(
      key,
      if (m.get(key).isDefined) {
        val valueMap = m(key)
        valueMap.updated(
          value,
          if (valueMap.get(value).isDefined) {
            valueMap(value).addNotEvidence(e)
          } else {
            TypingResult(Validated.invalid(NonEmptyList.of(e)))
          }
        )
      } else
        (immutable.Map(value -> firstNotEvidence(e)))
    )
    TypingMap(newTyping)
  }

  // TODO
  override def combineTyping(t: Typing[Key, Value, Err, Evidence]): Typing[Key, Value, Err, Evidence] = {
    t match {
      case tm: TypingMap[Key, Value, Err, Evidence] => {
        val r: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] =
          m.combine(tm.m)
        TypingMap(r)
      }

      case _ => throw new Exception("Unsupported combination of different typing maps")
    }
  }

  override def removeValue(key: Key, value: Value): Typing[Key, Value, Err, Evidence] = {
    def mappingFn(
        maybeValue: Option[immutable.Map[Value, TypingResult[Err, Evidence]]]
    ): Option[immutable.Map[Value, TypingResult[Err, Evidence]]] =
      maybeValue match {
        case None => None
        case Some(mapVs) => {
          def f(m: Option[TypingResult[Err, Evidence]]): Option[TypingResult[Err, Evidence]] = m match {
            case None => None
            case Some(tr) =>
              if (tr.isOK) None
              else Some(tr)
          }
          Some(updWith(mapVs)(value)(f))
        }
      }
    TypingMap(updWith(m)(key)(mappingFn))
  }

  def rmValues(
      cond: Value => Boolean
  )(m: immutable.Map[Value, TypingResult[Err, Evidence]]): immutable.Map[Value, TypingResult[Err, Evidence]] = {
    filterKs(m)(v => !cond(v))
  }

  override def removeValuesWith(cond: Value => Boolean): Typing[Key, Value, Err, Evidence] = {
    val zero: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] = immutable.Map()
    def cmb(
        current: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]],
        key: Key
    ): immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] = {
      val newM: immutable.Map[Value, TypingResult[Err, Evidence]] = m.get(key) match {
        case None     => immutable.Map()
        case Some(vs) => rmValues(cond)(vs)
      }
      current + (key -> newM)
    }
    TypingMap(m.keys.foldLeft(zero)(cmb))
  }

  def negateValues(cond: Value => Boolean, err: Err)(
      m: immutable.Map[Value, TypingResult[Err, Evidence]]
  ): immutable.Map[Value, TypingResult[Err, Evidence]] = {
    val zero: immutable.Map[Value, TypingResult[Err, Evidence]] = immutable.Map()
    def cmb(
        current: immutable.Map[Value, TypingResult[Err, Evidence]],
        value: Value
    ): immutable.Map[Value, TypingResult[Err, Evidence]] = {
      val newMap: immutable.Map[Value, TypingResult[Err, Evidence]] =
        if (cond(value)) {
          m.get(value) match {
            case None => current
            case Some(tr) =>
              if (tr.isOK) {
                current + (value -> TypingResult.fromErr(err))
              } else {
                current + (value -> tr)
              }
          }
        } else {
          current + (value -> m(value))
        }
      newMap
    }
    m.keys.foldLeft(zero)(cmb)
  }

  override def negateValuesWith(cond: Value => Boolean, err: Err): Typing[Key, Value, Err, Evidence] = {
    val zero: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] = immutable.Map()
    def cmb(
        current: immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]],
        key: Key
    ): immutable.Map[Key, immutable.Map[Value, TypingResult[Err, Evidence]]] = {
      val newM: immutable.Map[Value, TypingResult[Err, Evidence]] = m.get(key) match {
        case None     => immutable.Map()
        case Some(vs) => negateValues(cond, err)(vs)
      }
      current + (key -> newM)
    }
    val tm = TypingMap(m.keys.foldLeft(zero)(cmb))
    tm
  }

}
