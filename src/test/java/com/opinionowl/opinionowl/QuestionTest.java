package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Question Class.
 */
@SpringBootTest
public class QuestionTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to test Long Answer Type questions added to the survey.
     */
    @Test
    public void testLongAnswerQuestion() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion laq = new LongAnswerQuestion(survey, "test1", 5);
        survey.addQuestion(laq);
        assertSame(laq.getType(), QuestionType.LONG_ANSWER);
    }

    /**
     * Method to test Radio Choice Type questions added to the survey.
     */
    @Test
    public void testRadioChoiceQuestion() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        RadioChoiceQuestion rcq = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        survey.addQuestion(rcq);
        assertSame(rcq.getType(), QuestionType.RADIO_CHOICE);
    }

    /**
     * Method to test Range Type questions added to the survey.
     */
    @Test
    public void testRangeQuestion() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        RangeQuestion rq = new RangeQuestion(survey, "test3", 0, 8, 2);
        survey.addQuestion(rq);
        assertSame(rq.getType(), QuestionType.RANGE);
    }

    /**
     * Persistence test for questions in the survey.
     */
    @Test
    public void testPersist() {
        AppUser u1 = new AppUser("QuestionTest", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        RangeQuestion q3 = new RangeQuestion(survey, "test3", 1, 10, 1);

        survey.addQuestion(q1);
        survey.addQuestion(q2);
        survey.addQuestion(q3);

        questionRepository.save(q1);
        questionRepository.save(q2);
        questionRepository.save(q3);

        List<Question> results = questionRepository.findAll();
        for (Question q : results){
            if (!q.getSurvey().getTitle().equals(survey.getTitle())){
                continue;
            }
            if(q.getType() == QuestionType.LONG_ANSWER) {
                LongAnswerQuestion laq = (LongAnswerQuestion) q;
                assertEquals(laq.getCharLimit(), q1.getCharLimit());
            }
            else if (q.getType() == QuestionType.RADIO_CHOICE) {
                RadioChoiceQuestion rcq = (RadioChoiceQuestion) q;
                assertArrayEquals(rcq.getChoices(), q2.getChoices());
            }
            else {
                RangeQuestion rq = (RangeQuestion) q;
                assertEquals(rq.getUpper(), q3.getUpper());
                assertEquals(rq.getLower(), q3.getLower());
                assertEquals(rq.getIncrement(), q3.getIncrement());
            }
        }
    }
}
