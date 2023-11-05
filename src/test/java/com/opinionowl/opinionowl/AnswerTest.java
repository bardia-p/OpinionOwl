package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AnswerTest {
    @Autowired
    private ResponseRepository answerRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method that tests if answers to a question are added to a survey
     */
    @Test
    public void testAddedAnswer() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        LongAnswerQuestion laq = new LongAnswerQuestion(survey, "test1", 2);
        survey.addQuestion(laq);

        Response r1 = new Response(survey);
        r1.addAnswer(laq.getId(), "test1");
        r1.addAnswer(laq.getId(), "test2");
        survey.addResponse(r1);

        // Check that responses have been added
        assertEquals(2, survey.getResponsesForQuestion(laq.getId()).size());
    }

    @Test
    public void testAnswer() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        survey.addQuestion(q1);
        survey.addQuestion(q2);

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "answer1");
        survey.addResponse(r1);
        assertEquals("answer1", survey.getResponsesForQuestion(q1.getId()).get(0));
    }


    /**
     * Persistence test for the survey responses.
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

        surveyRepository.save(survey);
        survey.setClosed(true);

        Response r2 = new Response(survey);

        r2.addAnswer(q1.getId(), "yo");
        r2.addAnswer(q2.getId(), "a");

        answerRepository.save(r2);

        List<Response> results = answerRepository.findAll();
        for (Response a : results){
            System.out.println(a.toString());
        }
    }
}
