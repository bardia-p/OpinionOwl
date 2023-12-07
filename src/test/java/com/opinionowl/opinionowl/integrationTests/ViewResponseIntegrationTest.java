package com.opinionowl.opinionowl.integrationTests;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.ResponseRepository;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.Cookie;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewResponseIntegrationTest {

    @Autowired
    private MockMvc testController;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepo;

    /**
     * test the creation and posting of survey, then verifying we got some data for that user
     * @throws Exception - throws Exception
     */
    @Test
    public void testCreateAndPostSurveyResponseAndViewResponses() throws Exception {
        String postUserData = "{\"username\":\"TestCreateAndPostSurveyResponseAndViewResponsesUser\",\"password\":\"testpassword\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        Cookie cookie = new Cookie("username", "TestCreateAndPostSurveyResponseAndViewResponsesUser");

        AppUser user = this.userRepo.findByUsername("TestCreateAndPostSurveyResponseAndViewResponsesUser").orElse(null);
        assertNotNull(user);

        String postDataSurvey = "{\"radioQuestions\":{\"Test2\":[\"some radio choice\",\"radio choice 2\"]},\"numericRanges\":{\"Test3\":[0,25]},\"title\":\"TestCreateAndPostSurveyResponseAndViewResponsesSurvey\",\"textQuestions\":[\"Test1\"]}";
        // create a survey
        this.testController.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postDataSurvey))
                .andExpect(status().isOk());

        Survey createdSurvey = this.surveyRepository.findAll().stream().filter(s -> s.getTitle().equals("TestCreateAndPostSurveyResponseAndViewResponsesSurvey")).findFirst().orElse(null);
        assertNotNull(createdSurvey);

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

        // post a response to the survey created.
        this.testController.perform(post("/api/v1/postSurveyResponses/" + createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(surveyResponse.toString()))
                .andExpect(status().isOk());

        // post a response to the survey created.
        this.testController.perform(post("/api/v1/postSurveyResponses/" + createdSurvey.getId())
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(surveyResponse.toString()))
                .andExpect(status().isOk());

        for (Response res : this.responseRepository.findAll()) {
            assertNotNull(res);
        }

        this.testController.perform(get("/api/v1/savedResponses/{username}", user.getUsername())
                .cookie(cookie)).andExpect(status().isOk()).andExpect(content().string(containsString("answers")));
    }
}
