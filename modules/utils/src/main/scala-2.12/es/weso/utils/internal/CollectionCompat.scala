package es.weso.utils.internal
import scala.{collection => c}

private[weso] object CollectionCompat {

  type LazyList[A] = Stream[A]
  val LazyList = Stream

  def mapValues[K, A, B](map: collection.Map[K, A])(f: A => B): Map[K, B] =
    map.mapValues(f).toMap


  def filterKeys[K, A, B](map: collection.immutable.Map[K, A])(cond: K => Boolean): Map[K, A] =
    map.filterKeys(cond)


  def updatedWith[K,V](map: collection.immutable.Map[K,V])
                      (key: K)
                      (remappingFunction: Option[V] => Option[V]): collection.immutable.Map[K,V] = {
    val previousValue = map.get(key)
    val nextValue = remappingFunction(previousValue)
    (previousValue, nextValue) match {
      case (None, None) => map
      case (Some(_), None) => removed(map)(key)
      case (_, Some(v)) => map.updated(key, v)
    }                        
  }

  def removed[K,V](map: collection.immutable.Map[K,V])(key: K) = {
    val zero: collection.immutable.Map[K,V] = collection.immutable.Map()
    def cmb(m1: collection.immutable.Map[K,V], pair: (K,V)): collection.immutable.Map[K,V] = {
      val (k,v) = pair
      if (k == key) {
        m1
      } else {
        m1 + (k -> v)
      }
    }
    map.view.foldLeft(zero)(cmb).toMap
  }
    
  val CollectionConverters = scala.collection.JavaConverters

  implicit class IterableOps[T](private val iterable: c.Iterable[T]) extends AnyVal {
    def toLazyList: LazyList[T] = iterable.to[LazyList]
  }



}