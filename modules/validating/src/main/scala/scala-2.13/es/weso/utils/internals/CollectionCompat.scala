package es.weso.utils.internals

private[utils] object CollectionCompat {
    type LazyList[A] = scala.collection.immutable.LazyList[A]
    val LazyList = scala.collection.immutable.LazyList
}