/**
  * Created by rotorres on 11/29/2016.
  */
package rtcollection.map

import rcollection.map.KVPair

import scala.collection.mutable

class HashMap[K, V] extends Iterable[KVPair[K, V]] {
  var currentPopulation = 0

  var innerArray: Array[mutable.ListBuffer[KVPair[K, V]]] = new Array(HashMap.INIT_ARR_SIZE)
  var insertions: mutable.ListBuffer[KVPair[K, V]] = mutable.ListBuffer()

  def +=(pair: KVPair[K, V]): Unit = {
    tryReload()
    insertIncrement(pair)
  }

  def +=(pair: (K, V)): Unit = {
    this.+=(KVPair(pair._1, pair._2))
  }

  def -=(key: K): Option[KVPair[K, V]] = {
    val index = hashIndex(key)
    val targetBucket = innerArray(index)
    val pulledPair = if (targetBucket != null) targetBucket.find(keyedWith(key)) else None
    pulledPair match {
      case Some(foundPair) =>
        targetBucket -= foundPair
        insertions -= foundPair
      case _ =>
    }
    pulledPair
  }

  def keyedWith(key: K): KVPair[K, V] => Boolean = pair => pair.key.equals(key)

  def get(key: K): Option[V] = {
    val index: Int = hashIndex(key)
    val result = innerArray(index).find(_.key == key)

    if (result.isDefined) Some(result.get.value) else None
  }

  private def hashIndex(key: K): Int = {
    Math.abs(key.hashCode()) % innerArray.length
  }

  private def atReload: Boolean = {
    val status: Float = currentPopulation / innerArray.length.toFloat

    status >= HashMap.LOAD_FACTOR
  }

  private def tryReload() = if (atReload) reload()

  private def reload(): Unit = {
    val size = innerArray.length * 2
    val newArray = new Array[mutable.ListBuffer[KVPair[K, V]]](size)

    innerArray = newArray

    val reHash: (KVPair[K, V]) => Unit = insertHash(newArray, _)

    insertions.foreach(reHash)
    innerArray = newArray
  }

  private def insertIncrement(pair: KVPair[K, V]): Unit = {
    insertHash(innerArray, pair)
    currentPopulation += 1
    insertions += pair
  }

  private def insertHash(array: Array[mutable.ListBuffer[KVPair[K, V]]], pair: KVPair[K, V]): Unit = {
    val index = hashIndex(pair.key)
    if (array(index) == null) array(index) = mutable.ListBuffer()
    val exists = array(index).exists(_.key.equals(pair.key))
    /*
     * TODO  Could be made more efficient by having mutable values for the pair nodes
     * TODO  that would allow for object re-use rather than dangling pointers picked up by GC later on
     */
    if (exists) {
      array(index) -= array(index).find {
        (target) => target.key.equals(pair.key)
      }.get
    }
    array(index) += pair
  }

  override def iterator: Iterator[KVPair[K, V]] = MapIterator(insertions)

  def getOrElseUpdate(key: K, op: => V): V = {
    val index = hashIndex(key)
    val targetBucket = innerArray(index)
    val exists = if (targetBucket != null) targetBucket.find(_.key.equals(key)) else None

    val result = exists match {
      case Some(existingValue) => existingValue.value
      case None =>
        val update = KVPair(key, op)
        +=(update)
        update.value
    }

    result
  }

  def contains(key: K): Boolean = {
    val index = hashIndex(key)
    val targetBucket = innerArray(index)
    val exists = if (targetBucket != null) targetBucket.exists(_.key.equals(key)) else false
    exists
  }

  def updateEither(key: K, update: Option[V] => V): V = {
    val index = hashIndex(key)
    val targetBucket = innerArray(index)
    val targetPair = if (targetBucket != null) targetBucket.find(_.key.equals(key)) else None

    targetPair match {
      case Some(kvPair) => {
        val updatePair = KVPair(kvPair.key, update(Some(kvPair.value)))
        targetBucket.update(targetBucket.indexOf(kvPair), updatePair)
        insertions -= kvPair
        insertions += updatePair
      }
      case None => +=(KVPair(key, update(None)))
    }

    get(key).get
  }

  def values = insertions.map(_.value)

  def keySet = insertions.map(_.key)
}

case class MapIterator[K, V](insertions: Iterable[KVPair[K, V]]) extends Iterator[KVPair[K, V]] {
  val keyIterator = insertions.iterator

  override def hasNext: Boolean = keyIterator.hasNext

  override def next(): KVPair[K, V] = keyIterator.next()
}

/**
  * Constants object for HashMap.
  */
object HashMap {
  val INIT_ARR_SIZE = 16
  val LOAD_FACTOR = 0.75f
}