package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Answer Class.
 */
@SpringBootTest
public class AnswerTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    /**
     * Method to test that the correct answers are returned.
     */
    @Test
    public void testAnswer() {
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        survey.addQuestion(q1);

        Response r1 = new Response(survey);
        r1.addAnswer(q1.getId(), "answer1");
        r1.addAnswer(q1.getId(), "answer2");

        List<Answer> expectedAnswers = new ArrayList<>();
        expectedAnswers.add(new Answer(r1, q1.getId(), "answer1"));
        expectedAnswers.add(new Answer(r1, q1.getId(), "answer2"));
        assertEquals(expectedAnswers.toString(), r1.getAnswers().toString());
    }


    /**
     * Persistence test for the answers in the survey.
     */
    @Test
    public void testPersist() {
        AppUser u1 = new AppUser("AnswerTest", "123");
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
        survey.setClosed(true);
        surveyRepository.save(survey);

        Answer a1 = new Answer(r1, q1.getId(), "hi");
        Answer a2 = new Answer(r1, q1.getId(), "b");
        answerRepository.save(a1);
        answerRepository.save(a2);

        List<Answer> expectedR = new ArrayList<>();
        expectedR.add(new Answer(r1, q1.getId(), "hi"));
        expectedR.add(new Answer(r1, q1.getId(), "b"));

        List<Answer> results = answerRepository.findAll();
        for (Answer a : results){
            System.out.println(a.toString());
            if(a.getResponse().getAnswers().containsAll(expectedR)) {
                System.out.println(a.getResponse().getAnswers());
            }
        }
    }
}
