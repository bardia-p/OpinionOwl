package com.opinionowl.opinionowl;
import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    /**
     * Method to test the Create Survey post mapping. It simply verifies that a new survey was posted to the repository.
     * @throws Exception
     */
    @Test
    public void testCreateSurvey() throws Exception{
        String postData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"This is a test\",\"textQuestions\":[\"Test1\"]}";

        this.testController.perform(post("/api/v1/createSurvey")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        for (Survey survey : surveyRepository.findAll()) {
            System.out.println(survey.toString());
        }
    }
}

