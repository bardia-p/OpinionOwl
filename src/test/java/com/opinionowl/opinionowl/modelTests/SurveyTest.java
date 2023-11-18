package com.opinionowl.opinionowl.modelTests;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Survey Class.
 */
@SpringBootTest
public class SurveyTest {
    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to test if a survey has been closed and if responses can be added after closing.
     */
    @Test
    public void testClosedSurvey() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 10);
        survey.addQuestion(q1);
        q1.setId(Long.valueOf(2001));


        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "response1");
        r1.addAnswer(q1.getId(), "response2");
        survey.addResponse(r1);

        survey.setClosed(true); // close the survey
        assertTrue(survey.isClosed());

        Map<String, Integer> expectedR = Map.of("response1", 1, "response2", 1);
        Map<String, Integer> actualR = survey.getResponsesForQuestion(q1.getId());

        survey.removeResponse(r1.getId()); // removing a response from a closed survey
        assertEquals(expectedR, actualR);

        // Check if questions were added to the survey after the survey was closed
        Response r2 = new Response(survey);
        r2.addAnswer(q1.getId(), "hello");
        r2.addAnswer(q1.getId(), "okay");
        survey.addResponse(r2);

        List<String> res2 = Arrays.asList("hello", "okay");
        assertFalse(actualR.equals(res2));
    }

    /**
     * Method to test if questions have been added to the survey.
     */
    @Test
    public void testAddedQuestions() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        assertTrue(survey.getQuestions().isEmpty());

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 3);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test1", new String[]{"a", "b", "c"} );
        RangeQuestion q3 = new RangeQuestion(survey, "test3", 1, 10, 1);
        survey.addQuestion(q1);
        survey.addQuestion(q2);
        survey.addQuestion(q3);
        survey.setClosed(true);
        assertEquals(3, survey.getQuestions().size());

        List<Question> questions = survey.getQuestions();
        for(Question q : questions) {
            if (q.getType() == QuestionType.LONG_ANSWER){
                LongAnswerQuestion laq = (LongAnswerQuestion) q;
                assertEquals(3, laq.getCharLimit());
            }
            else if (q.getType() == QuestionType.RADIO_CHOICE) {
                RadioChoiceQuestion rcq = (RadioChoiceQuestion) q;
                assertArrayEquals(new String[]{"a", "b", "c"}, rcq.getChoices());
            }
            else {
                RangeQuestion rq = (RangeQuestion) q;
                assertEquals(1, rq.getLower());
                assertEquals(10, rq.getUpper());
            }
        }
    }

    /**
     * Test whether the questions have been added to the survey.
     */
    @Test
    public void testAddedResponses() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 3);
        survey.addQuestion(q1);
        q1.setId(Long.valueOf(2002));

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "hai");
        r1.addAnswer(q1.getId(), "a");
        survey.addResponse(r1);
        survey.setClosed(true);
        assertEquals(1, survey.getResponses().size());

        Map<String, Integer> expectedR = Map.of("hai", 1, "a", 1);
        assertTrue(survey.getResponsesForQuestion(q1.getId()).equals(expectedR));
    }

    /**
     * Method that tests if answers to a question are added to a survey.
     */
    @Test
    public void testAddedAnswer() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion laq = new LongAnswerQuestion(survey, "test1", 2);
        survey.addQuestion(laq);
        laq.setId(Long.valueOf(2003));

        Response r1 = new Response(survey);
        r1.addAnswer(laq.getId(), "test1");
        r1.addAnswer(laq.getId(), "test2");
        survey.addResponse(r1);

        // Check that responses have been added
        assertEquals(2, survey.getResponsesForQuestion(laq.getId()).size());
    }

    /**
     * Method to test responses to long answer questions in the survey.
     */
    @Test
    public void testLongAnswerResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion laq = new LongAnswerQuestion(survey, "test1", 20);
        survey.addQuestion(laq);
        laq.setId(Long.valueOf(2004));

        Response r1 = new Response(survey);
        r1.addAnswer(laq.getId(), "test response");
        r1.addAnswer(laq.getId(), "SYSC 4806 project");
        survey.addResponse(r1);

        Map<String, Integer> responses = survey.getResponsesForQuestion(laq.getId());
        for(String res : responses.keySet()) {
            assertTrue(res.length() <= laq.getCharLimit());
        }
    }

    /**
     * Method to test responses to radio choice questions in the survey.
     */
    @Test
    public void testRadioChoiceResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        RadioChoiceQuestion rcq = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        survey.addQuestion(rcq);
        rcq.setId(Long.valueOf(2005));

        Response r1 = new Response(survey);
        r1.addAnswer(rcq.getId(), "a");
        r1.addAnswer(rcq.getId(), "d");
        survey.addResponse(r1);

        Map<String, Integer> expectedR = Map.of("a", 1, "d", 1, "c", 0);
        assertTrue(survey.getResponsesForQuestion(rcq.getId()).equals(expectedR));
    }

    /**
     * Method to test responses to range questions in the survey.
     */
    @Test
    public void testRangeResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        RangeQuestion rq = new RangeQuestion(survey, "test3", 1, 10, 1);
        survey.addQuestion(rq);
        rq.setId(Long.valueOf(2006));

        Response r1 = new Response(survey);
        r1.addAnswer(rq.getId(), "2");
        r1.addAnswer(rq.getId(), "3");
        r1.addAnswer(rq.getId(), "10");
        survey.addResponse(r1);

        Map<String, Integer> responses = survey.getResponsesForQuestion(rq.getId());
        for(String res : responses.keySet()) {
            assertTrue(Integer.parseInt(res) >= rq.getLower() && Integer.parseInt(res) <= rq.getUpper());
        }
    }

    /**
     * Persistence test for the survey.
     */
    @Test
    public void testPersist() {
        AppUser u1 = new AppUser("SurveyTest", "123");
        Survey survey = new Survey(u1, "SURVEY_TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        RangeQuestion q3 = new RangeQuestion(survey, "test3", 1, 10, 1);

        survey.addQuestion(q1);
        survey.addQuestion(q2);
        survey.addQuestion(q3);

        surveyRepository.save(survey);

        Response r1 = new Response(survey);

        r1.addAnswer(q1.getId(), "hi");
        r1.addAnswer(q2.getId(), "b");

        survey.addResponse(r1);

        surveyRepository.save(survey);

        survey.setClosed(true);

        Response r2 = new Response(survey);

        r2.addAnswer(q1.getId(), "yo");
        r2.addAnswer(q2.getId(), "a");

        survey.addResponse(r2);

        surveyRepository.save(survey);

        List<Survey> results = surveyRepository.findAll();
        Survey retrievedSurvey = null;
        for (Survey r : results) {
            if (r.getTitle().equals(survey.getTitle())) {
                retrievedSurvey = r;
                break;
            }
        }
        assertNotNull(retrievedSurvey);
        assertEquals(retrievedSurvey.getQuestions().size(), survey.getQuestions().size());
        assertEquals(retrievedSurvey.getResponses().size(), survey.getResponses().size());
    }
}