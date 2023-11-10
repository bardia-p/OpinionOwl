package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppUserTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAddSurvey(){
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);

        assertEquals(1, u1.getListSurveys().size());
        List<Survey> expected = Arrays.asList(survey);

        assertEquals(expected, u1.getListSurveys());
    }

    @Test
    public void testRemoveSurvey(){
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        userRepository.save(u1);
        surveyRepository.save(survey);
        u1.addSurvey(survey);
        u1.removeSurvey(survey.getId());
        assertEquals(0, u1.getListSurveys().size());
    }

}
