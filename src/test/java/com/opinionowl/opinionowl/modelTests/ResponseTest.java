package com.opinionowl.opinionowl.modelTests;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Response Class.
 */
@SpringBootTest
public class ResponseTest {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to test that the correct response is returned.
     */
    @Test
    public void testResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 10);
        survey.addQuestion(q1);
        q1.setId(2000L);

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "response1");
        r1.addAnswer(q1.getId(), "response2");
        survey.addResponse(r1);
        Map<String, Integer> expectedR = Map.of("response1", 1, "response2", 1);
        assertEquals(survey.getResponsesForQuestion(q1.getId()), expectedR);
    }


    /**
     * Persistence test for the responses in the survey.
     */
    @Test
    public void testPersist() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "RESPONSE_TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        survey.addQuestion(q1);

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "hi");
        r1.addAnswer(q2.getId(), "b");
        survey.addResponse(r1);

        responseRepository.save(r1);

        List<Response> results = responseRepository.findAll();

        Response retrievedResponse = null;
        for (Response r : results){
            if (r.getSurvey().getTitle().equals(survey.getTitle())){
                retrievedResponse = r;
            }
        }
        assertNotNull(retrievedResponse);
        assertEquals(retrievedResponse.getAnswers().size(), r1.getAnswers().size());
    }
}