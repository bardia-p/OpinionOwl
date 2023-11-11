package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the AppUser Class
 */
@SpringBootTest
public class AppUserTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to test if a survey can be added to an appUser
     */
    @Test
    public void testAddSurvey(){
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);

        assertEquals(1, u1.getListSurveys().size());
        List<Survey> expected = Arrays.asList(survey);

        assertEquals(expected, u1.getListSurveys());
    }

    /**
     * Method to test if the user remains the same before and after a survey is saved
     */
    @Test
    public void testPersist(){
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);
        surveyRepository.save(survey);

        List<AppUser> users = userRepository.findAll();
        assertEquals(1, users.size());

        AppUser user = users.get(0);

        assertEquals(1,user.getListSurveys().size());
        assertEquals(u1.getListSurveys().get(0).getTitle(), user.getListSurveys().get(0).getTitle());
        assertEquals(u1.getListSurveys().get(0).isClosed(), user.getListSurveys().get(0).isClosed());

    }

}
