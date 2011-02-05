package fj.data

import ArbitraryArray.arbitraryArray
import fj.ArbitraryP.arbitraryP1
import org.scalacheck.Prop._
import fj.Implicit._
import fj.data.Array.{array, empty, join}
import fj.Equal.{arrayEqual, stringEqual}
import fj.Unit.unit

object CheckArray {
  val prop_isEmpty = forAll((a: Array[Int]) =>
    a.isEmpty != a.isNotEmpty)

  val prop_isNotEmpty = forAll((a: Array[Int]) =>
    a.length > 0 ==> a.isNotEmpty)

  val prop_toOption = forAll((a: Array[Int]) =>
    a.toOption.isNone || a.toOption.some == a.get(0))

  // crashes the type checker for unknown reason
  // val prop_toEither = property((a: Array[Int], n: P1[Int]) =>
  //   (a.toEither(n).isLeft && a.toEither(n).left.value == n._1) || (a.toEither(n).right.value == a.get(0)))

  val prop_mapId = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(a.map((x: String) => x), a))

  val prop_mapCompose = forAll((a: Array[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    arrayEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  val prop_filter1 = forAll((a: Array[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)))

  val prop_filter2 = forAll((a: Array[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).length <= a.length)

  val prop_foreach = forAll((a: Array[Int]) => {
    var i = 0
    a.foreach({
      (x: Int) => i = i + x
      unit
    })

    var j = 0

    for(x <- 0 until a.length)
      j = j + a.get(x)

    i == j
  })

  val prop_foldRight = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.foldRight((a: String, b: Array[String]) => array[String](scala.Array(a): _*).append(b), empty[String]), a))

  val prop_foldLeft = forAll((a: Array[String], s: String) =>
    arrayEqual(stringEqual).eq(
      a.foldLeft(((a: Array[String], b: String) => array[String](scala.Array(b): _*).append(a)), empty[String]),
      a.reverse.foldRight((a: String, b: Array[String]) => array[String](scala.Array(a): _*).append(b), empty[String])))

  val prop_bindLeftIdentity = forAll((a: Array[String], s: String) => {
    def f(s: String) = array[String](scala.Array(s.reverse): _*)
    arrayEqual(stringEqual).eq(
      array[String](scala.Array(s): _*).bind(f(_: String)),
      f(s))})

  val prop_bindRightIdentity = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.bind((x: String) => array[String](scala.Array(x): _*)),
      a))

  val prop_bindAssociativity = forAll((a: Array[String]) => {
    def f(s: String) = array[String](scala.Array(s.reverse): _*)
    def g(s: String) = array[String](scala.Array(s.toUpperCase): _*)
    arrayEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  val prop_sequence = forAll((a: Array[String], b: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  val prop_reverseIdentity = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.reverse.reverse,
      a))

  val prop_reverse = forAll((a: Array[String], n: Int) =>
    (n >= 0 && n < a.length) ==>
    (a.reverse.get(n) == a.get(a.length - 1 - n)))

  val prop_appendLeftIdentity = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(a.append(empty[String]), a))

  val prop_appendRightIdentity = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(a, a.append(empty[String])))

  val prop_appendAssociativity = forAll((a: Array[String], b: Array[String], c: Array[String]) =>
    arrayEqual(stringEqual).eq(a.append(b).append(c), a.append(b.append(c))))

  val prop_appendLength = forAll((a: Array[String], b: Array[String]) =>
    a.append(b).length == a.length + b.length)

  val prop_array = forAll((a: scala.Array[String], n: Int) =>
    (n >= 0 && n < a.length) ==>
    (array[String](a: _*).length == a.length && array[String](a: _*).get(n) == a(n)))

  val prop_join = forAll((a: Array[Array[String]]) =>
    arrayEqual(stringEqual).eq(
      a.foldRight((a: Array[String], b: Array[String]) => a.append(b), empty[String]),
      join(a)))

  val prop_forall = forAll((a: Array[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  val prop_exists = forAll((a: Array[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  val tests = scala.List(
      ("prop_isEmpty", prop_isEmpty),
      ("prop_isNotEmpty", prop_isNotEmpty),
      ("prop_toOption", prop_toOption),
//      ("prop_toEither", prop_toEither),
      ("prop_mapId", prop_mapId),
      ("prop_mapCompose", prop_mapCompose),
      ("prop_filter1", prop_filter1),
      ("prop_filter2", prop_filter2),
      ("prop_foreach", prop_foreach),
      ("prop_foldRight", prop_foldRight),
      ("prop_foldLeft", prop_foldLeft),
      ("prop_bindLeftIdentity", prop_bindLeftIdentity),
      ("prop_bindRightIdentity", prop_bindRightIdentity),
      ("prop_bindAssociativity", prop_bindAssociativity),
      ("prop_sequence", prop_sequence),
      ("prop_reverseIdentity", prop_reverseIdentity),
      ("prop_reverse", prop_reverse),
      ("prop_appendLeftIdentity", prop_appendLeftIdentity),
      ("prop_appendRightIdentity", prop_appendRightIdentity),
      ("prop_appendAssociativity", prop_appendAssociativity),
      ("prop_appendLength", prop_appendLength),
      ("prop_array", prop_array),
      ("prop_join", prop_join),
      ("prop_forall", prop_forall), 
      ("prop_exists", prop_exists)
  ).map { case (n, p) => ("Array." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)  
}
