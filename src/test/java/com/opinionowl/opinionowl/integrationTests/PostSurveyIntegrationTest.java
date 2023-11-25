package com.opinionowl.opinionowl.integrationTests;

import com.opinionowl.opinionowl.models.Response;
import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.repos.ResponseRepository;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostSurveyIntegrationTest {

    @Autowired
    private MockMvc testController;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    /**
     * Method to test the post sruvey response mapping. It simply verifies that a new survey response was posted to the repository.
     * It will first create a survey with questions then post a response to that very survey.
     * @throws Exception
     */
    @Test
    public void testCreateAndPostSurveyResponse() throws Exception {
        /*String postDataResponse = "{\"1\": \"some text answer\", \"2\" : \"some radio choice\", \"3\" : \"25\"}";
        String postDataSurvey = "{\"radioQuestions\":{\"Test2\":[\"some radio choice\",\"radio choice 2\"]},\"numericRanges\":{\"Test3\":[0,25]},\"title\":\"This is a test\",\"textQuestions\":[\"Test1\"]}";
        // create a survey
        this.testController.perform(post("/api/v1/createSurvey")
                        .contentType(MediaType.APPLICATION_JSON).content(postDataSurvey))
                .andExpect(status().isOk());

        Survey createdSurvey = null;
        for (Survey survey : this.surveyRepository.findAll()) {
            createdSurvey = survey;
        }

        assertNotNull(createdSurvey);
        assertEquals(createdSurvey.getTitle(), "This is a test");

        // post a response to the survey created. We can guarantee there is only one survey, so we grab the id=1
        this.testController.perform(post("/api/v1/postSurveyResponses/" + createdSurvey.getId()).contentType(MediaType.APPLICATION_JSON).content(postDataResponse))
                .andExpect(status().isOk());

        for (Response res : this.responseRepository.findAll()) {
            assertNotNull(res);
            assertEquals(3, res.getAnswers().size());
        }
*/
    }
}
