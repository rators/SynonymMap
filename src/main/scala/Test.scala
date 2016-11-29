import synonym.SynonymMap

/**
  * Test method for the data structure. Ran out of time implementing the Hash Map and could not make a driver
  * main class, my apologiies.
  */
object Test extends App {
  val reader = scala.io.StdIn

  val synonymMap = new SynonymMap()

  //Test transitivity
  synonymMap
    .addWord("Hello", "to say goodbye in english")
    .addWord("Konichiwa", "to say goodbye in japanese")
    .addWord("Knee How", "to say goodbye in chinese")

  synonymMap
    .synonymize("Hello", "Konichiwa")
    .synonymize("Konichiwa", "Knee How")


  //Test transitivity and synonym representative union

  synonymMap
    .addWord("animal", "a thing")
    .addWord("creature", "a creature")
    .addWord("beast", "a beast")

  synonymMap.synonymize("animal", "beast")
    .synonymize("beast", "creature")

  assert(
    synonymMap.getSynonyms("animal") == synonymMap.getSynonyms("creature") &&
      synonymMap.getSynonyms("animal") == synonymMap.getSynonyms("beast")
  )

  synonymMap
    .addWord("dog", "a dog")
    .addWord("mammal", "a mammal")
    .synonymize("mammal", "dog")

  assert(
    synonymMap.getSynonyms("mammal") == synonymMap.getSynonyms("dog")
  )

  // Unite two representative sets
  synonymMap
    .synonymize("mammal", "beast")

  assert(synonymMap.getSynonyms("animal") != synonymMap.getSynonyms("dog"))
}
