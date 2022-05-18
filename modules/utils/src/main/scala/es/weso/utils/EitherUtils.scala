package es.weso.utils

import cats.implicits._

import scala.util.{Either, Left, Right}

/** This utility methods are not intended to replace cats, but as IntelliJ raises some ugly false errors in the code
  * when using cats this functions encapsulate the errors in here so your code does not look ugly.
  */
object EitherUtils {

  /** Reduces from a list of either objects to a single either where the left represents first error found and right a
    * list of the right values.
    *
    * @param of
    *   is the initial list of either objects from where the reduction will be performed.
    * @tparam A
    *   is the type of the Right value.
    * @tparam E
    *   is the type of the Left value.
    * @return
    *   a unique Either object with a list of right value types and the left type value if present.
    */
  def sequence[A, E](of: List[Either[E, A]]): Either[E, List[A]] = {
    type ES[V] = Either[E, V]
    of.sequence[ES, A]
  }

  /** From a list of values and a message will create an Either object where the Right part will be the very first and
    * unique element from the list of values. If the list is empty or contains more than one value the Either will be
    * Left will the message.
    *
    * @param from
    *   is the list from where the Either will be created.
    * @param message
    *   to set in as Left if the list is empty or has more than one element.
    * @tparam A
    *   is the type of the elements received in the list of values.
    * @return
    *   the Either object, if the list has one and only one element will return a Right containing that element. Else a
    *   Left with the message.
    */
  def takeSingle[A](from: List[A], message: String): Either[String, A] =
    if (from.length == 1) Right(from.head)
    else Left(message)

}
