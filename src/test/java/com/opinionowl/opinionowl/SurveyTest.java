package com.opinionowl.opinionowl;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SurveyTest {
    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testPersist() {
        User u1 = new User("max", "123");
        userRepository.save(u1);
        Survey survey = new Survey("TEST_SURVEY");
        u1.addSurvey(survey);
        LongAnswerQuestion q1 = new LongAnswerQuestion("test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion("test2", new String[]{"a", "c", "d"});
        RangeQuestion q3 = new RangeQuestion("test3", 1, 10);

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

        surveyRepository.save(survey);

        List<Survey> results = surveyRepository.findAll();
        for (Survey r : results){
            System.out.println(r.toString());

            for (Question q : r.getQuestions()){
                if (q.getType() == QuestionType.LONG_ANSWER){
                    LongAnswerQuestion laq = (LongAnswerQuestion) q;
                    System.out.println(laq.getCharLimit());
                }
            }
        }
    }
}
