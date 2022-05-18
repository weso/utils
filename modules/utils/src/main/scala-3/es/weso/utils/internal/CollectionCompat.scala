package es.weso.utils.internal
import scala.{collection => c}

private[weso] object CollectionCompat {
  type LazyList[A] = scala.collection.immutable.LazyList[A]
  val LazyList             = scala.collection.immutable.LazyList
  val CollectionConverters = scala.jdk.CollectionConverters

  def mapValues[K, A, B](map: Map[K, A])(f: A => B): Map[K, B] =
    map.view.mapValues(f).toMap

  def filterKeys[K, A, B](map: Map[K, A])(cond: K => Boolean): Map[K, A] =
    map.view.filterKeys(cond).toMap

  def updatedWith[K, V](map: Map[K, V])(key: K)(remappingFunction: Option[V] => Option[V]): Map[K, V] = {
    map.updatedWith(key)(remappingFunction)
  }

  implicit class IterableOps[T](private val iterable: c.Iterable[T]) extends AnyVal {
    def toLazyList: LazyList[T] = iterable.to(LazyList)
  }

}
