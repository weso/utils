package es.weso.utils.internal
import scala.{collection => c}

private[weso] object CollectionCompat {
    type LazyList[A] = scala.collection.immutable.LazyList[A]
    val LazyList = scala.collection.immutable.LazyList
    val CollectionConverters = scala.jdk.CollectionConverters

    def mapValues[K, A, B](map: Map[K, A])(f: A => B): Map[K, B] =
      map.view.mapValues(f).toMap

      
    implicit class IterableOps[T](private val iterable: c.Iterable[T]) extends AnyVal {
        def toLazyList: LazyList[T] = iterable.to(LazyList)
    }

}