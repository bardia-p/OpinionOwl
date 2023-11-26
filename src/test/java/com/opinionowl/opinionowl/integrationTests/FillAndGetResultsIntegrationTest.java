package com.opinionowl.opinionowl.integrationTests;

import com.opinionowl.opinionowl.models.Response;
import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FillAndGetResultsIntegrationTest {
    @Autowired
    private MockMvc testController;

    @Autowired
    private SurveyRepository surveyRepository;

    /**
     * Method to test the get survey results. mapping. It will create a survey, fill it, and confirm the result exists.
     * @throws Exception
     */
    @Test
    public void testCreateAndPostSurveyResponse() throws Exception {
        String postUserData = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        Cookie cookie = new Cookie("userId", "1");

        String postDataResponse = "{\"1\": \"some text answer\", \"2\" : \"some radio choice\", \"3\" : \"24\"}";
        String postDataSurvey = "{\"radioQuestions\":{\"Test2\":[\"some radio choice\",\"radio choice 2\"]},\"numericRanges\":{\"Test3\":[0,25]},\"title\":\"This is a result test\",\"textQuestions\":[\"Test1\"]}";
        // create a survey
        this.testController.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postDataSurvey))
                .andExpect(status().isOk());

        Survey createdSurvey = null;
        for (Survey survey : this.surveyRepository.findAll()) {
            if (survey.getTitle().equals("This is a result test")){
                createdSurvey = survey;
                break;
            }
        }
        assertNotNull(createdSurvey);
        assertEquals(createdSurvey.getTitle(), "This is a result test");

        // post a response to the survey created. We can guarantee there is only one survey, so we grab the id=1
        this.testController.perform(post("/api/v1/postSurveyResponses/" + createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postDataResponse))
                .andExpect(status().isOk());

        // Query the results of the survey.
        this.testController.perform(get("/api/v1/getSurveyResults/" + createdSurvey.getId())
                                    .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("24")));
    }
}
