package com.opinionowl.opinionowl.controllerTests;
import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.models.AppUser;
import com.opinionowl.opinionowl.repos.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the post mappings in API Controller.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class APIControllerTest {
    @Autowired
    private MockMvc testController;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method to test the Create Survey post mapping. It simply verifies that a new survey was posted to the repository.
     *
     * @throws Exception
     */
    @Test
    public void testCreateSurvey() throws Exception {
        String postData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"This is a small test\",\"textQuestions\":[\"Test1\"]}";

        AppUser user = new AppUser("SimpleUser", "password");
        userRepository.save(user);

        Cookie userCookie = new Cookie("username", user.getUsername());
        this.testController.perform(post("/api/v1/createSurvey")
                        .cookie(userCookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        List<Survey> surveyList = surveyRepository.findAll();
        assertTrue(surveyList.size() != 0);
        Survey retrievedSurvey = null;
        for (Survey survey : surveyList) {
            if (survey.getTitle().equals("This is a small test")){
                retrievedSurvey = survey;
                break;
            }
        }

        assertNotNull(retrievedSurvey);
    }

    /**
     * Method to test the Register User post mapping. It simply verifies that a new survey was posted to the repository.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterUser() throws Exception {
        String postData = "{\"username\":\"maxcurkovic\",\"password\":\"sysc4806\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser retrievedUser = userRepository.findByUsername("maxcurkovic").orElse(null);
        assertNotNull(retrievedUser);
    }

    /**
     * Method that tests the loginUser POST mapping. It tests that a user can successfully be logged in.
     * @throws Exception
     */
    @Test
    public void testLoginUser() throws Exception {
        String postData = "{\"username\":\"maxcurkovic\",\"password\":\"sysc4806\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        Cookie cookie = new Cookie("username", "maxcurkovic");
        // Create a survey using the POST request.
        this.testController.perform(post("/api/v1/loginUser")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser loggedInUser = userRepository.findByUsername("maxcurkovic").orElse(null);
        assertNotNull(loggedInUser);
        assertEquals(cookie.getValue(), loggedInUser.getUsername());
    }
}





