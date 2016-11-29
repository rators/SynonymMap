package rtcollection.map

/**
  * The entry convenience class for representing an entry in the synonym map.
  *
  * @param meaning
  * The meaning of the word.
  * @param synonymRep
  * The synonym representative of this entry.
  */
case class Entry(meaning: String, var synonymRep: Option[SynonymRep])

/**
  * The value pointer.
  *
  * @param s
  * The string representative.
  */
case class Value(s: String)

/**
  * The synonym rep value pointer object.
  *
  * @param value
  * The value string pointer for this synonym representation.
  */
case class SynonymRep(var value: Value)