package es.weso.utils.internal

private[utils] object CollectionCompat {
    type LazyList[A] = scala.collection.immutable.LazyList[A]
    val LazyList = scala.collection.immutable.LazyList
}