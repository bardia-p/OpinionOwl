package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Response Class
 */
@SpringBootTest
public class ResponseTest {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to test responses to long answer questions in the survey
     */
    @Test
    public void testLongAnswerResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion laq = new LongAnswerQuestion(survey, "test1", 20);
        survey.addQuestion(laq);

        Response r1 = new Response(survey);
        r1.addAnswer(laq.getId(), "test response");
        r1.addAnswer(laq.getId(), "SYSC 4806 project");
        survey.addResponse(r1);

        List<String> responses = survey.getResponsesForQuestion(laq.getId());
        for(String res : responses) {
            assertTrue(res.length() <= laq.getCharLimit());
        }
    }

    /**
     * Method to test responses to radio choice questions in the survey
     */
    @Test
    public void testRadioChoiceResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        RadioChoiceQuestion rcq = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        survey.addQuestion(rcq);

        Response r1 = new Response(survey);
        r1.addAnswer(rcq.getId(), "a");
        r1.addAnswer(rcq.getId(), "d");
        survey.addResponse(r1);

        List<String> expected = Arrays.asList("a", "d");
        assertTrue(survey.getResponsesForQuestion(rcq.getId()).containsAll(expected));
    }

    @Test
    public void testRangeResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        RangeQuestion rq = new RangeQuestion(survey, "test3", 1, 10, 1);
        survey.addQuestion(rq);

        Response r1 = new Response(survey);
        r1.addAnswer(rq.getId(), "2");
        r1.addAnswer(rq.getId(), "3");
        r1.addAnswer(rq.getId(), "10");
        survey.addResponse(r1);

        List<String> responses = survey.getResponsesForQuestion(rq.getId());
        for(String res : responses) {
            assertTrue(Integer.parseInt(res) >= rq.getLower() && Integer.parseInt(res) <= rq.getUpper());
        }
    }

    /**
     * Persistence test for the survey rsponses.
     */
    @Test
    public void testPersist() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        survey.addQuestion(q1);

        surveyRepository.save(survey);

        Response r1 = new Response(survey);

        r1.addAnswer(q1.getId(), "hi");
        r1.addAnswer(q2.getId(), "b");
        survey.addResponse(r1);

        responseRepository.save(r1);
        surveyRepository.save(survey);
        survey.setClosed(true);

        Response r2 = new Response(survey);

        r2.addAnswer(q1.getId(), "yo");
        r2.addAnswer(q2.getId(), "a");

        responseRepository.save(r2);

        List<Response> results = responseRepository.findAll();
        for (Response r : results){
            System.out.println(r.toString());
        }
    }
}