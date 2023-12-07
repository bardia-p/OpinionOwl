package com.opinionowl.opinionowl.integrationTests;

import com.opinionowl.opinionowl.models.AppUser;
import com.opinionowl.opinionowl.models.Survey;
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
    private UserRepository userRepo;

    /**
     * Test method to close a survey.
     * @throws Exception, exception
     */
    @Test
    public void testCloseSurvey() throws Exception {
        Cookie cookie = new Cookie("username", "manageSurveyCloseSurveyTest");

        // Creating a user
        String postUserData = "{\"username\":\"manageSurveyCloseSurveyTest\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        AppUser user = this.userRepo.findByUsername("manageSurveyCloseSurveyTest").orElse(null);
        assertNotNull(user);

        // Create a survey using the POST request.
        String postSurveyData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"manageSurveyCloseSurveySurveyTest\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                .andExpect(status().isOk());

        Survey createdSurvey = this.surveyRepository.findAll().stream().filter(s -> s.getTitle().equals("manageSurveyCloseSurveySurveyTest")).findFirst().orElse(null);
        assertNotNull(createdSurvey);

        // Navigate to "/manageSurvey" page and expect that it contains the newly created survey in the list
        this.mockMvc.perform(get("/manageSurvey?username=manageSurveyCloseSurveyTest")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("manageSurveyCloseSurveySurveyTest")));

        // Close the survey
        this.mockMvc.perform(post("/api/v1/closeSurvey/{id}", createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                .andExpect(status().isOk());

        // Check that the survey is closed
        assertTrue(Objects.requireNonNull(this.surveyRepository.findById(createdSurvey.getId()).orElse(null)).isClosed());
    }

    /**
     * Test method to edit a survey with new data.
     * @throws Exception, exception
     */
    @Test
    public void testEditSurvey() throws Exception {
        Cookie cookie = new Cookie("username", "testManageSurveyEditSurveyUser");

        // Creating a user
        String postUserData = "{\"username\":\"testManageSurveyEditSurveyUser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        AppUser user = this.userRepo.findByUsername("testManageSurveyEditSurveyUser").orElse(null);
        assertNotNull(user);

        // Create a survey using the POST request.
        String postSurveyData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"testManageSurveyEditSurveySurvey\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                .andExpect(status().isOk());

        Survey createdSurvey = this.surveyRepository.findAll().stream().filter(s -> s.getTitle().equals("testManageSurveyEditSurveySurvey")).findFirst().orElse(null);
        assertNotNull(createdSurvey);

        // Navigate to "/manageSurvey" page and expect that it contains the newly created survey in the list
        this.mockMvc.perform(get("/manageSurvey?username=testManageSurveyEditSurveyUser")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("testManageSurveyEditSurveySurvey")));

        // Get the current survey results
        this.mockMvc.perform(get("/api/v1/getSurveyResults/{id}", createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                .andExpect(status().isOk());

        // Navigate to "/editSurvey" page to edit newly created survey
        this.mockMvc.perform(get("/editSurvey?surveyId=" + createdSurvey.getId())
                        .cookie(cookie))
                .andExpect(status().isOk());

        // Update the survey with new data
        String updatedSurveyData = "{\"radioQuestions\":{\"Test2\":[\"b\",\"a\"]},\"numericRanges\":{\"Test3\":[0,9]},\"title\":\"testManageSurveyEditSurveySurveyUpdate\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/updateSurvey/{id}", createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(updatedSurveyData))
                .andExpect(status().isOk());

        Survey updatedSurvey = this.surveyRepository.findAll().stream().filter(s -> s.getTitle().equals("testManageSurveyEditSurveySurveyUpdate")).findFirst().orElse(null);
        assertNotNull(updatedSurvey);
    }
}
