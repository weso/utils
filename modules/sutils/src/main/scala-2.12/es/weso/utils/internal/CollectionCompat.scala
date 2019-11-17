package es.weso.utils.internal

private[weso] object CollectionCompat {
  type LazyList[A] = Stream[A]
  val LazyList = Stream

}