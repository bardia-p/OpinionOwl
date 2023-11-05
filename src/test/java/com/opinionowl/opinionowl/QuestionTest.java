package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Question Class
 */
@SpringBootTest
public class QuestionTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

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

        assertEquals(1, survey.getQuestions().size()); // question was added to the survey
        assertSame(laq.getType(), QuestionType.LONG_ANSWER);
        assertEquals(5, laq.getCharLimit());
        assertTrue(survey.getQuestions().contains(laq));
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

        assertEquals(1, survey.getQuestions().size()); // question was added to the survey
        assertSame(rcq.getType(), QuestionType.RADIO_CHOICE);
        assertTrue(survey.getQuestions().contains(rcq));
        assertArrayEquals(new String[]{"a", "c", "d"}, rcq.getChoices());
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

        assertEquals(1, survey.getQuestions().size()); // question was added to the survey
        assertSame(rq.getType(), QuestionType.RANGE);
        assertTrue(survey.getQuestions().contains(rq));
        assertEquals(0, rq.getLower());
        assertEquals(8, rq.getUpper());
        assertEquals(2, rq.getIncrement());
    }

    /**
     * Persistence test for questions in the survey.
     */
    @Test
    public void testPersist() {
        AppUser u1 = new AppUser("Test", "123");
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
        surveyRepository.save(survey);
        survey.setClosed(true);

        List<Question> results = questionRepository.findAll();
        for (Question q : results){
            System.out.println(q.toString());
        }
    }
}
