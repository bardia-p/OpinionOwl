package com.opinionowl.opinionowl.controllerTests;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@AutoConfigureMockMvc
public class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * <p>Handle tests for Get mapping the home page</p>
     * <br />
     * <strong>Expects:</strong> the <u>index</u> HTML page
     * @throws Exception, exception
     */
    @Test
    public void testHomePageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testHomePageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/', expecting to retrieve an HTML page");
        String content = this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }
        System.out.println("Expects title: OpinionOwl, Actual: " + title);
        // assert the title equals to the home page title
        assert(title.equals("OpinionOwl"));
        System.out.println("---------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the create survey page</p>
     * <br />
     * <strong>Expects:</strong> the <u>createSurvey</u> HTML page
     * @throws Exception, exception
     */
    @Test
    public void testCreateSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testCreateSurveyPageMapping()");
        System.out.println();
        AppUser u = new AppUser("test1", "test1");
        userRepository.save(u);
        Cookie cookie = new Cookie("username", u.getUsername().toString());
        System.out.println("Mocking get page '/createSurvey', expecting to retrieve an HTML page");
        String content = this.mockMvc.perform(get("/createSurvey")
                        .cookie(cookie))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("text/html;charset=UTF-8"))
                        .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Create Survey, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Create Survey"));
        System.out.println("------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the answer survey page using a valid survey id</p>
     * <br />
     * <strong>Expects:</strong> the <u>answerSurvey</u> HTML page
     * @throws Exception, exception
     */
    @Test
    public void testValidAnswerSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testValidAnswerSurveyPageMapping() with a valid survey retrieving");
        System.out.println();
        System.out.println("Mocking get page '/answerSurvey', expecting to retrieve an HTML page");
        System.out.println("Creating a survey for the page to get. ID: 1L");
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

        Long surveyId = null;

        List<Survey> surveyList = surveyRepository.findAll();
        for (Survey s : surveyList){
            if (s.getTitle().equals("TEST_SURVEY")){
                surveyId = s.getId();
                break;
            }
        }

        Cookie cookie = new Cookie("username", u1.getUsername().toString());

        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        RadioChoiceQuestion q2 = new RadioChoiceQuestion(survey, "test2", new String[]{"a", "c", "d"});
        RangeQuestion q3 = new RangeQuestion(survey, "test3", 1, 10, 1);

        survey.addQuestion(q1);
        survey.addQuestion(q2);
        survey.addQuestion(q3);
        System.out.println("Survey successfully created");
        surveyRepository.save(survey);

        System.out.println("Performing get of /answerSurvey with query ?surveyId=1. Final mapping = /answerSurvey?surveyId=1");
        String content = this.mockMvc.perform(get("/answerSurvey")
                        .cookie(cookie)
                        .param("surveyId", String.valueOf(surveyId)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Answer Survey, Actual: " + title);
        // assert the title equals to the Answer Survey page title
        assert(title.equals("OpinionOwl | Answer Survey"));
        System.out.println("------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the answer survey page using an invalid survey id</p>
     * <br />
     * <strong>Expects:</strong> the <u>index</u> HTML page
     * @throws Exception, exception
     */
    @Test
    public void testInvalidAnswerSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testInvalidAnswerSurveyPageMapping() with an Invalid survey retrieving");
        System.out.println();
        System.out.println("Mocking get page '/answerSurvey', expecting to retrieve an HTML page");
        long surveyId = 2000;
        System.out.println("Performing get of /answerSurvey with query ?surveyId=3. Final mapping = /answerSurvey?surveyId=3");
        String content = this.mockMvc.perform(get("/answerSurvey")
                        .param("surveyId", String.valueOf(surveyId)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: , Actual: " + title);
        // assert the title equals to the redirect page title
        assertEquals(title, "");
        System.out.println("------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the register user page</p>
     * <br />
     * <strong>Expects:</strong> the <u>registerUser</u> HTML page
     * @throws Exception, exception
     */
    @Test
    public void testRegisterUserPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testRegisterUserPageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/RegisterUser', expecting to retrieve an HTML page");
        String content = this.mockMvc.perform(get("/registerUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Create Survey, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Register User"));
        System.out.println("------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the Login user page</p>
     * <br />
     * <strong>Expects:</strong> the <u>loginUser</u> HTML page
     * @throws Exception, exception
     */
    @Test
    public void testLoginUserPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testLoginUserPageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/loginUser', expecting to retrieve an HTML page");
        String content = this.mockMvc.perform(get("/loginUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Login, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Login"));
        System.out.println("------------------------------");
    }

    /**
     * Handle tests for Get mapping the Edit Survey page
     * @throws Exception, exception
     */
    @Test
    public void testEditSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testEditSurveyPageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/editSurvey', expecting to retrieve an HTML page");
        long surveyId = 2000;

        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);
        Cookie cookie = new Cookie("userId", u1.getId().toString());

        String content = this.mockMvc.perform(get("/editSurvey")
                    .param("surveyId", String.valueOf(surveyId)).cookie(cookie))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Edit Survey, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Edit Survey"));
        System.out.println("------------------------------");
    }

    /**
     * Handle tests for Get mapping the Manage Survey page
     * @throws Exception, exception
     */
    @Test
    public void testManageSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testManageSurveyPageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/manageSurvey', expecting to retrieve an HTML page");

        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);
        Cookie cookie = new Cookie("userId", u1.getId().toString());

        String content = this.mockMvc.perform(get("/manageSurvey")
                .param("userId", String.valueOf(u1.getId()))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Manage Survey, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Manage Survey"));
        System.out.println("------------------------------");
    }

    /**
     * Handle tests for Get mapping the View Response page
     * @throws Exception, exception
     */
    @Test
    public void testViewResponsePageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testViewResponsePageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/viewResponse', expecting to retrieve an HTML page");

        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);
        Long surveyId = null;

        List<Survey> surveyList = surveyRepository.findAll();
        for (Survey s : surveyList){
            if (s.getTitle().equals("TEST_SURVEY")){
                surveyId = s.getId();
                break;
            }
        }
        Cookie cookie = new Cookie("userId", u1.getId().toString());
        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        survey.addQuestion(q1);
        surveyRepository.save(survey);

        String content = this.mockMvc.perform(get("/viewResponse")
                        .param("surveyId", String.valueOf(surveyId))
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Responses, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Responses"));
        System.out.println("------------------------------");
    }

    /**
     * Handle tests for Get mapping the Saved Responses page
     * @throws Exception, exception
     */
    @Test
    public void testSavedResponsesPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testSavedResponsesPageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/savedResponses', expecting to retrieve an HTML page");

        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

        Cookie cookie = new Cookie("userId", u1.getId().toString());
        LongAnswerQuestion q1 = new LongAnswerQuestion(survey, "test1", 2);
        survey.addQuestion(q1);
        Response r1 = new Response(survey);
        Response r2 = new Response(survey);
        r1.addAnswer(q1.getId(),"response1");
        r2.addAnswer(q1.getId(),"response2");
        survey.addResponse(r1);
        survey.addResponse(r2);
        surveyRepository.save(survey);

        String content = this.mockMvc.perform(get("/savedResponses")
                        .param("userId", String.valueOf(u1.getId()))
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        // extract the title
        System.out.println("Parsing the title of the page");
        Pattern pattern = Pattern.compile("<title>(.*?)</title>");
        Matcher matcher = pattern.matcher(content);

        // Find the title using the regex pattern
        String title = "";
        if (matcher.find()) {
            title = matcher.group(1);
        }

        System.out.println("Expects title: OpinionOwl | Saved Responses, Actual: " + title);
        // assert the title equals to the create survey page title
        assert(title.equals("OpinionOwl | Saved Responses"));
        System.out.println("------------------------------");
    }
}
