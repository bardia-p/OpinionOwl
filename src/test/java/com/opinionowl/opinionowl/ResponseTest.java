package com.opinionowl.opinionowl;
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

    @Autowired
    private SurveyRepository surveyRepository;

    /**
     * Method to test that the correct response is returned.
     */
    @Test
    public void testResponse() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 10);
        survey.addQuestion(q1);

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "response1");
        r1.addAnswer(q1.getId(), "response2");
        survey.addResponse(r1);

        List<String> expectedR = Arrays.asList("response1", "response2");
        assertTrue(survey.getResponsesForQuestion(q1.getId()).containsAll(expectedR));
    }


    /**
     * Persistence test for the responses in the survey.
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

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "hi");
        r1.addAnswer(q2.getId(), "b");
        survey.addResponse(r1);

        responseRepository.save(r1);
        survey.setClosed(true);

        Response r2 = new Response(survey);

        r2.addAnswer(q1.getId(), "yo");
        r2.addAnswer(q2.getId(), "a");

        surveyRepository.save(survey);
        responseRepository.save(r2);

        List<Response> results = responseRepository.findAll();
        List<Answer> expectedR = new ArrayList<>();
        expectedR.add(new Answer(r1, q1.getId(), "hi"));
        expectedR.add(new Answer(r1, q1.getId(), "b"));
        for (Response r : results){
            System.out.println(r.toString());
            if(r.getAnswers().containsAll(expectedR)) {
                System.out.println(r.getAnswers());
            }
        }
    }
}