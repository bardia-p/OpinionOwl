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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateSurveyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository appUserRepo;

    @Autowired
    private SurveyRepository surveyRepo;

    /**
     *
     */
    @Test
    public void testCreateAndRetrieveSurvey() throws Exception {
        String postUserData = "{\"username\":\"TestCreateAndRetrieveSurveyUser\",\"password\":\"testpassword\"}";
        this.mockMvc.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserData))
                .andExpect(status().isOk());

        AppUser testUser = this.appUserRepo.findByUsername("TestCreateAndRetrieveSurveyUser").orElse(null);
        assertNotNull(testUser);

        Cookie cookie = new Cookie("username", "TestCreateAndRetrieveSurveyUser");
        String postData = "{\"radioQuestions\":{\"Test2\":[\"a\",\"b\"]},\"numericRanges\":{\"Test3\":[0,11]},\"title\":\"TestCreateAndRetrieveSurvey\",\"textQuestions\":[\"Test1\"]}";

        // Create a survey using the POST request.
        this.mockMvc.perform(post("/api/v1/createSurvey")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        Survey testSurvey = this.surveyRepo.findAll().stream().filter(s -> s.getTitle().equals("TestCreateAndRetrieveSurvey")).findFirst().orElse(null);
        assertNotNull(testSurvey);
        assertEquals(3, testSurvey.getQuestions().size());

    }
}