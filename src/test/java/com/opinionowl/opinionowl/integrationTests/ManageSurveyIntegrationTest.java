package com.opinionowl.opinionowl.integrationTests;
import com.opinionowl.opinionowl.models.AppUser;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ManageSurveyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Test method to close a survey.
     * @throws Exception, exception
     */
    @Test
    public void testCloseSurvey() throws Exception {
        //Cookie cookie = new Cookie("userId", "1");
        Long surveyId = 1L;

        String postData = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser loggedInUser = null;
        for (AppUser user : userRepository.findAll()) {
            if (user.getUsername().equals("testuser") && user.getPassword().equals("testpassword")) {
                loggedInUser = user;
                break;
            }
        }
        assert loggedInUser != null;
        Cookie cookie = new Cookie("userId", loggedInUser.getId().toString());

        this.mockMvc.perform(post("/api/v1/loginUser")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        // Creating a user
        String postUserData = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                        .andExpect(status().isOk());

        // Create a survey using the POST request.
        String postSurveyData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"Form Title\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/createSurvey")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                            .andExpect(status().isOk());

        // Navigate to "/manageSurvey" page and expect that it contains the newly created survey in the list
        this.mockMvc.perform(get("/manageSurvey?userId=1")
                        .cookie(cookie))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Form Title")));

        // Close the survey
        this.mockMvc.perform(post("/api/v1/closeSurvey/{id}", surveyId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                        .andExpect(status().isOk());

        // Check that the survey is closed
        assertTrue(Objects.requireNonNull(surveyRepository.findById(surveyId).orElse(null)).isClosed());
    }

    /**
     * Test method to edit a survey with new data.
     * @throws Exception, exception
     */
    @Test
    public void testEditSurvey() throws Exception {
        Long surveyId = 1L;
        String postData = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser loggedInUser = null;
        for (AppUser user : userRepository.findAll()) {
            if (user.getUsername().equals("testuser") && user.getPassword().equals("testpassword")) {
                loggedInUser = user;
                break;
            }
        }
        assert loggedInUser != null;
        Cookie cookie = new Cookie("userId", loggedInUser.getId().toString());

        this.mockMvc.perform(post("/api/v1/loginUser")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        String postSurveyData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"Form Title\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                        .andExpect(status().isOk());

        this.mockMvc.perform(get("/manageSurvey?userId=1")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Form Title")));

        // Get the current survey results
        this.mockMvc.perform(get("/api/v1/getSurveyResults/{id}", surveyId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                        .andExpect(status().isOk());

        // Navigate to "/editSurvey" page to edit newly created survey
        this.mockMvc.perform(get("/editSurvey?surveyId=1")
                        .cookie(cookie))
                .andExpect(status().isOk());

        // Update the survey with new data
        String updatedSurveyData = "{\"radioQuestions\":{\"Test2\":[\"b\",\"a\"]},\"numericRanges\":{\"Test3\":[0,9]},\"title\":\"Survey\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/updateSurvey/{id}", surveyId)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(updatedSurveyData))
                        .andExpect(status().isOk());
    }
}
