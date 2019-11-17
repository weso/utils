package es.weso.utils.internals

private[utils] object CollectionCompat {
  type LazyList[A] = Stream[A]
  val LazyList = Stream

}