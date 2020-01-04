# SearchEngine

Implementation of a basic search engine in Java along with a GUI built with JavaFX that indexes documents and provides appropriate search results. 
Porter's algorithm is used for stemming (reducing words to their base form).

- Libraries: JSoup (Java HTML Parser), OpenNLP (PorterStemmer)
- JDK: jdk1.8.0_231
- Runtime: jre1.8.0_231

For the purpose of the project, 200 sample documents were fetched based on the following search engine queries:
- When are the 2020 NBA finals?
- Who Won The Fourth Democratic Debate?
- What are the best movies of 2019?
- Who are the richest people in the world?
- How Much Battery Life Does the iPhone 11 Pro Have?
- Where Is UFC 245 Being Held?
- Where Is the 2022 World Cup?
- How Much Money Has the Joker Movie Made?
- Who is hosting the 2020 Olympics?
- How Tall Is One World Trade Center?

These documents act as the corpus (collection of texts) which is indexed by the program, but more documents can be added as desired (to the *html* folder) in order to serve better results for queries different from the ones listed above.
