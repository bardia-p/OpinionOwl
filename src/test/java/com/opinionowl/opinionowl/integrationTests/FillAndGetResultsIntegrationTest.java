package com.opinionowl.opinionowl.integrationTests;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.ResponseRepository;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.json.*;

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
    private UserRepository appUserRepo;

    @Autowired
    private SurveyRepository surveyRepo;

    @Autowired
    private ResponseRepository responseRepo;

    /**
     * Method to test the get survey results. mapping. It will create a survey, fill it, and confirm the result exists.
     * @throws Exception - throws Exception
     */
    @Test
    public void testCreateAndPostSurveyResponse() throws Exception {
        String postUserData = "{\"username\":\"TestGetOfCreateAndPostSurveyResponseUser\",\"password\":\"testpassword\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        AppUser testUser = this.appUserRepo.findByUsername("TestGetOfCreateAndPostSurveyResponseUser").orElse(null);
        assertNotNull(testUser);
        Cookie cookie = new Cookie("username", "TestGetOfCreateAndPostSurveyResponseUser");

        String postDataSurvey = "{\"radioQuestions\":{\"Test2\":[\"some radio choice\",\"radio choice 2\"]},\"numericRanges\":{\"Test3\":[0,25]},\"title\":\"TestGetOfCreateAndPostSurveyResponseTitle\",\"textQuestions\":[\"Test1\"]}";
        // create a survey
        this.testController.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postDataSurvey))
                .andExpect(status().isOk());

        Survey createdSurvey = this.surveyRepo.findAll().stream().filter(s -> s.getTitle().equals("TestGetOfCreateAndPostSurveyResponseTitle")).findFirst().orElse(null);
        assertNotNull(createdSurvey);
        assertEquals(3, createdSurvey.getQuestions().size());

        JSONObject surveyResponse = new JSONObject();
        for (Question q : createdSurvey.getQuestions()) {
            if (q.getType() == QuestionType.RADIO_CHOICE) {
                RadioChoiceQuestion rC = (RadioChoiceQuestion) q;
                surveyResponse.put(Long.toString(rC.getId()), rC.getChoices()[0]);
            } else if (q.getType() == QuestionType.RANGE) {
                RangeQuestion rQ = (RangeQuestion) q;
                surveyResponse.put(Long.toString(rQ.getId()), 10);
            } else if (q.getType() == QuestionType.LONG_ANSWER) {
                LongAnswerQuestion lQ = (LongAnswerQuestion) q;
                surveyResponse.put(Long.toString(lQ.getId()), "Some Text Answer");
            }
        }

        // post a response to the survey created. We can guarantee there is only one survey, so we grab the id=1
        this.testController.perform(post("/api/v1/postSurveyResponses/" + createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(surveyResponse.toString()))
                .andExpect(status().isOk());

        for (Response res : this.responseRepo.findAll()) {
            assertNotNull(res);
        }

        this.testController.perform(get("/api/v1/getSurveyResults/" + createdSurvey.getId())
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("24")));

    }
}
