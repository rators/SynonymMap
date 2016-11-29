package rtcollection.map

import scala.collection._

/**
  * The dictionary trait.
  */
trait Dictionary {

  /**
    * Add a word to the dictionary.
    * @param word
    *             The word being added.
    * @param meaning
    *                The meaning of the word.
    * @return
    *         This instance.
    */
  def addWord(word: String, meaning: String): this.type

  /**
    * Looks up a word in this dictionary.
    * @param word
    *             The word being queried.
    * @return
    *         Some meaning value if it exists, else none.
    */
  def lookup(word: String): Option[String]
}

/**
  * The thesaurus trait.
  */
trait Thesaurus {

  /**
    * Synonymizes two words. Synonym relations are equivalence relations.
    * @param word
    *             A word that exists in the dictionary.
    * @param synonym
    *                A word that exists in the dictionary.
    * @return
    *         This instance.
    */
  def synonymize(word: String, synonym: String): this.type

  /**
    * Gets all synonyms related to a word.
    * @param word
    *             The word whose synonyms are being fetched.
    * @return
    *         The set of synonyms for this word.
    */
  def getSynonyms(word: String): Set[String]
}