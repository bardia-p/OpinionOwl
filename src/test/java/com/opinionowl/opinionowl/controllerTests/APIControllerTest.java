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
     * @throws Exception, exception
     */
    @Test
    public void testCreateSurvey() throws Exception {
        String postData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"This is a test\",\"textQuestions\":[\"Test1\"]}";

        this.testController.perform(post("/api/v1/createSurvey")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        for (Survey survey : surveyRepository.findAll()) {
            assertNotNull(survey);
            assertEquals("This is a test", survey.getTitle());
        }
    }

    /**
     * Method to test the Register User post mapping. It simply verifies that a new survey was posted to the repository.
     *
     * @throws Exception, exception
     */
    @Test
    public void testRegisterUser() throws Exception {
        String postData = "{\"username\":\"maxcurkovic\",\"password\":\"sysc4806\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser retrievedUser = null;
        for (AppUser u : userRepository.findAll()) {
            if (u.getUsername().equals("maxcurkovic")) {
                retrievedUser = u;
                break;
            }
        }
        assertNotNull(retrievedUser);
        assertEquals("maxcurkovic", retrievedUser.getUsername());
    }

    /**
     * Method that tests the loginUser POST mapping. It tests that a user can successfully be logged in.
     * @throws Exception, exception
     */
    @Test
    public void testLoginUser() throws Exception {
        String postData = "{\"username\":\"maxcurkovic\",\"password\":\"sysc4806\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        Cookie cookie = new Cookie("userId", "1");
        // Create a survey using the POST request.
        this.testController.perform(post("/api/v1/loginUser")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser loggedInUser = null;
        for (AppUser user : userRepository.findAll()) {
            if (user.getUsername().equals("maxcurkovic") && user.getPassword().equals("sysc4806")) {
                loggedInUser = user;
                break;
            }
        }
        assertEquals(parseLong(cookie.getValue()), loggedInUser != null ? loggedInUser.getId() : null);
    }
}





