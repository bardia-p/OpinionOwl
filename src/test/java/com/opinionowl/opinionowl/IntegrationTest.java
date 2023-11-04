package com.opinionowl.opinionowl;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class IntegrationTest {
    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Test method for the Survey class.
     */
    @Test
    public void testDatabaseIntegration() {
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

        List<AppUser> results = userRepository.findAll();
        for (AppUser u : results){
            System.out.println(u.toString());
        }
    }
}