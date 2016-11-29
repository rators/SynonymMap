package synonym

import rtcollection.map._

/**
  * The synonym map class that acts as a dictionary/thesaurus
  */
class SynonymMap extends Dictionary with Thesaurus {
  val entryMap: HashMap[String, Entry] = new HashMap()

  override def addWord(word: String, meaning: String): SynonymMap.this.type = {
    entryMap += word -> Entry(meaning, None)
    this
  }

  override def lookup(word: String): Option[String] = {
    entryMap.get(word) match {
      case None => None
      case Some(entry) => Some(entry.meaning)
    }
  }

  override def synonymize(word: String, synonym: String): SynonymMap.this.type = {
    if (!bothDefined(word, synonym)) throw new NoSuchElementException(s"Both words must already exists in order to synonymize them")

    val wordOpt: Option[Entry] = entryMap.get(word)
    val synonymOpt: Option[Entry] = entryMap.get(synonym)

    (wordOpt, synonymOpt) match {
      case (Some(wordEntry: Entry), Some(synonymEntry: Entry)) =>
        setSynonymReps((word, wordEntry), (synonym, synonymEntry))
        this
      case _ => throw new RuntimeException()
    }

  }

  override def getSynonyms(word: String): Set[String] = {
    entryMap.get(word).get.synonymRep match {
      case None => Set.empty
      case Some(synonymRep) =>
        entryMap
          .keySet
          .filter((key) => entryMap.get(key).get.synonymRep.contains(synonymRep))
          .toSet
    }
  }

  /**
    * Sets the synonym reps for two given entries.
    *
    * @param word
    * A word pair (word -> associated entry.
    * @param synonym
    * A word pair (word -> associated entry.
    */
  private def setSynonymReps(word: (String, Entry), synonym: (String, Entry)): Unit = {
    (word._2.synonymRep, synonym._2.synonymRep) match {
      case (None, None) => // if both are undefined then initialize the rep for either, then set equal
        synonym._2.synonymRep = Some(SynonymRep(Value(synonym._1)))
        word._2.synonymRep = synonym._2.synonymRep
      case (wordSynRep: Some[SynonymRep], None) => // If one and only one word has a predefined rep then set the undefined rep to the defined one
        synonym._2.synonymRep = wordSynRep
      case (None, synonymRep: Some[SynonymRep]) => // same as above.
        word._2.synonymRep = synonymRep
      case (Some(wordRep), Some(synRep)) => wordRep.value = synRep.value // if both are defined then set their representative string values equal to each other.
    }
  }

  //convenience method to determine if two strings already exist.
  private def bothDefined(a: String, b: String) = {
    entryMap.contains(a) && entryMap.contains(b)
  }
}


