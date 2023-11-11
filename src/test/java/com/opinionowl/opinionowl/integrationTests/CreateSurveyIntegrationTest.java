package com.opinionowl.opinionowl.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateSurveyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    /**
     * Test method for POSTing a survey and doing a GET after
     */
    @Test
    public void testCreateAndRetrieveSurvey() throws Exception {
        String postData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"Form Title\",\"textQuestions\":[\"Test1\"]}";

        // Create a survey using the POST request.
        this.mockMvc.perform(post("/api/v1/createSurvey")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                        .andExpect(status().isOk());

        // Expect the survey to show up on the home page.
        this.mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Form Title")));

    }
}