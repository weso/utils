package es.weso.utils.internal

private[weso] object CollectionCompat {
    type LazyList[A] = scala.collection.immutable.LazyList[A]
    val LazyList = scala.collection.immutable.LazyList
}