package com.opinionowl.opinionowl;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class SurveyTest {
    @Autowired
    private SurveyRepository surveyRepository;

    @Test
    public void testPersist() {
        Survey survey = new Survey();
        LongAnswerQuestion q1 = new LongAnswerQuestion("test1", 2);
        MultiChoiceQuestion q2 = new MultiChoiceQuestion("test2", new String[]{"a", "c", "d"});
        RangeQuestion q3 = new RangeQuestion("test3", 1.0F, 10.0F);

        survey.addQuestion(q1);
        survey.addQuestion(q2);
        survey.addQuestion(q3);

        surveyRepository.save(survey);

        Response r1 = new Response(new HashMap<Long, String>(){{
            put(Long.valueOf(1), "hi");
            put(Long.valueOf(2), "a");
        }});

        survey.addResponse(r1);

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
