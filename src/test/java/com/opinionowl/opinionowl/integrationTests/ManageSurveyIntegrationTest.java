package com.opinionowl.opinionowl.integrationTests;
import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

    /**
     * Test method to close a survey.
     * @throws Exception, exception
     */
    @Test
    public void testCloseSurvey() throws Exception {
        Cookie cookie = new Cookie("username", "closesurveyuser");

        // Creating a user
        String postUserData = "{\"username\":\"closesurveyuser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        // Create a survey using the POST request.
        String postSurveyData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"Test Close Survey\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/createSurvey")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                            .andExpect(status().isOk());

        List<Survey> surveyList = surveyRepository.findAll();
        Survey newSurvey = null;
        for (Survey s : surveyList){
            if (s.getTitle().equals("Test Close Survey")){
                newSurvey = s;
                break;
            }
        }
        assertNotNull(newSurvey);

        // Navigate to "/manageSurvey" page and expect that it contains the newly created survey in the list
        this.mockMvc.perform(get("/manageSurvey?username=closesurveyuser")
                        .cookie(cookie))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Test Close Survey")));

        // Close the survey
        this.mockMvc.perform(post("/api/v1/closeSurvey/{id}", newSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                        .andExpect(status().isOk());

        // Check that the survey is closed
        assertTrue(surveyRepository.findById(newSurvey.getId()).orElse(null).isClosed());
        }

    /**
     * Test method to edit a survey with new data.
     * @throws Exception, exception
     */
    @Test
    public void testEditSurvey() throws Exception {
        Cookie cookie = new Cookie("username", "editsurveyuser");

        // Creating a user
        String postUserData = "{\"username\":\"editsurveyuser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        // Create a survey using the POST request.
        String postSurveyData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"Test Edit Survey\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                .andExpect(status().isOk());

        List<Survey> surveyList = surveyRepository.findAll();
        Survey newSurvey = null;
        for (Survey s : surveyList){
            if (s.getTitle().equals("Test Edit Survey")){
                newSurvey = s;
                break;
            }
        }
        assertNotNull(newSurvey);

        // Navigate to "/manageSurvey" page and expect that it contains the newly created survey in the list
        this.mockMvc.perform(get("/manageSurvey?username=editsurveyuser")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test Edit Survey")));

        // Get the current survey results
        this.mockMvc.perform(get("/api/v1/getSurveyResults/{id}", newSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postSurveyData))
                        .andExpect(status().isOk());

        // Navigate to "/editSurvey" page to edit newly created survey
        this.mockMvc.perform(get("/editSurvey?surveyId=1")
                        .cookie(cookie))
                .andExpect(status().isOk());

        // Update the survey with new data
        String updatedSurveyData = "{\"radioQuestions\":{\"Test2\":[\"b\",\"a\"]},\"numericRanges\":{\"Test3\":[0,9]},\"title\":\"Survey\",\"textQuestions\":[\"Test1\"]}";
        this.mockMvc.perform(post("/api/v1/updateSurvey/{id}", newSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(updatedSurveyData))
                        .andExpect(status().isOk());
    }
}
