import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuestionTest {

    @Test
    public void ageQuestionTest() throws UnknownQuestionException { // fail if question form unknown
        // n.b. Question.ask() method signature can only return String, so expect a string response here (contrary to spec)
        assertEquals("53", Question.ask("How old is David Cameron"));
        assertEquals("67", Question.ask("How old is Tony Blair"));
    }

    @Test
    public void birthdateQuestionTest() throws UnknownQuestionException { // fail if question form unknown
        assertEquals("Anthony Charles Lynton Blair", Question.ask("What is the birth name of Tony Blair ?"));
    }
}
