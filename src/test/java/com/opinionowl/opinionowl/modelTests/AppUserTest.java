package com.opinionowl.opinionowl.modelTests;
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
        AppUser u1 = new AppUser("AppUserTest", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);

        assertEquals(1, u1.getListSurveys().size());
        List<Survey> expected = List.of(survey);

        assertEquals(expected, u1.getListSurveys());
    }

    /**
     * Method to test if a survey can be removed from an appUser
     */
    @Test
    public void testRemoveSurvey(){
        AppUser u1 = new AppUser("AppUserRemoveTest", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        userRepository.save(u1);
        surveyRepository.save(survey);
        u1.addSurvey(survey);
        u1.removeSurvey(survey.getId());
        assertEquals(0, u1.getListSurveys().size());
    }

    /**
     * Method to test if the user remains the same before and after a survey is saved
     */
    @Test
    public void testPersist(){
        AppUser u1 = new AppUser("AppUserTest", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);
        surveyRepository.save(survey);

        List<AppUser> users = userRepository.findAll();
        assert(!users.isEmpty());

        AppUser retrievedUser = userRepository.findByUsername(u1.getUsername()).orElse(null);
        assertNotNull(retrievedUser);
        assert(!retrievedUser.getListSurveys().isEmpty());
        assertEquals(u1.getListSurveys().get(0).getTitle(), retrievedUser.getListSurveys().get(0).getTitle());
        assertEquals(u1.getListSurveys().get(0).isClosed(), retrievedUser.getListSurveys().get(0).isClosed());

    }

}
