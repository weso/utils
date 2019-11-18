package es.weso.utils.internal
import scala.{collection => c}

private[weso] object CollectionCompat {
  type LazyList[A] = Stream[A]
  val LazyList = Stream

  def mapValues[K, A, B](map: collection.Map[K, A])(f: A => B): Map[K, B] =
    map.mapValues(f).toMap
    
  val CollectionConverters = scala.collection.JavaConverters

  implicit class IterableOps[T](private val iterable: c.Iterable[T]) extends AnyVal {
    def toLazyList: LazyList[T] = iterable.to[LazyList]
  }

}