This project answers simple questions such as `How old is <person>`, and `What is the birth name of <person>?` by submiting SPARQL queries to the live RESTful api of DbPedia - a structured data version of Wikipedia. 

To run the example tests, run QuestionTest in IntelliJ IDEA. Alternatively, from the project's root `gradlew clean test --info` may be used from the command line.

Java language version & target is Java 8.

Dependencies are managed with Gradle:
- Apache Jena, for submitting SPARQL queries and giving access to returned data
- Joda-Time to help cleanly go from date of birth to someone's age in years
- JUnit, for testing