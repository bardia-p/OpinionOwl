package com.opinionowl.opinionowl.integrationTests;
import com.opinionowl.opinionowl.repos.SurveyRepository;
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

    /**
     * Test method to close a survey.
     */
    @Test
    public void testCloseSurvey() throws Exception {
        Cookie cookie = new Cookie("userId", "1");
        Long surveyId = 1L;

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
}
