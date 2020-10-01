import org.apache.jena.query.*;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Question {

    private static final String HOW_OLD_QUESTION = "How old is";
    private static final String BIRTHDATE_QUESTION = "What is the birth name of";
    private static final float DAYS_IN_YEAR = 365.25f;

    public static String ask(String question) throws UnknownQuestionException {

        // Assume question wording remains the same, so can simply check for .contains and later use substring
        if (question.contains(HOW_OLD_QUESTION)) {
            return askAgeQuestion(question);
        } else if (question.contains(BIRTHDATE_QUESTION)) {
            return askBirthdateQuestion(question);
        }

        throw new UnknownQuestionException("Cannot currently parse questions like " + question);
    }

    private static String askAgeQuestion(String question) {
        String searchTerm = question.substring(HOW_OLD_QUESTION.length() + 1)
                .replaceAll(" ", "_");

        String queryString = "prefix dbr: <http://dbpedia.org/resource/>\n" +
                "prefix dbp: <http://dbpedia.org/property/>\n" +
                "prefix dbo: <http://dbpedia.org/ontology/>\n" +
                "SELECT ?dob\n" +
                "FROM <http://dbpedia.org>\n" +
                "WHERE {\n" +
                "dbr:" + searchTerm + " dbo:birthDate ?dob .\n" +
                "} LIMIT 1";

        // force syntaxARQ as default Jena syntax standard doesn't support a used syntax feature
        Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql",  query)) {
            ResultSet resultSet = queryExecution.execSelect();

            QuerySolution next = resultSet.next();

            String dob = next.getLiteral("dob").getString(); // e.g. 1966-10-09

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            DateTime dobDateTime = formatter.parseDateTime(dob);

            Duration timeDifference = new Duration(dobDateTime , new DateTime());
            int years = (int) (timeDifference.getStandardDays() / DAYS_IN_YEAR);

            return Integer.toString(years);
        }
    }

    private static String askBirthdateQuestion(String question) {
        String searchTerm = question.substring(BIRTHDATE_QUESTION.length() + 1)
                .replaceAll("\\?", "") // don't care about question mark at end
                .trim() // don't care about whitespace before/after, e.g. "...Blair ?"
                .replaceAll(" ", "_");

        String queryString = "prefix dbr: <http://dbpedia.org/resource/>\n" +
                "prefix dbp: <http://dbpedia.org/property/>\n" +
                "prefix dbo: <http://dbpedia.org/ontology/>\n" +
                "SELECT ?name\n" +
                "FROM <http://dbpedia.org>\n" +
                "WHERE {\n" +
                "dbr:" + searchTerm +" ?p ?name .\n" +
                "FILTER (?p IN (dbp:birthname, dbp:birthName, dbo:birthName,dbo:birthname) )\n" +
                "} LIMIT 1";

        // force syntaxARQ as default Jena syntax standard doesn't support a used syntax feature
        Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql",  query)) {
            ResultSet resultSet = queryExecution.execSelect();

            QuerySolution next = resultSet.next();

            return next.getLiteral("name").getString(); // e.g. "Anthony Charles Lynton Blair"
        }
    }
}