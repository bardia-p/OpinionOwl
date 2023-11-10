package com.opinionowl.opinionowl;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerAPIGetTest {

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
     * @throws Exception
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
     * @throws Exception
     */
    @Test
    public void testCreateSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testCreateSurveyPageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/createSurvey', expecting to retrieve an HTML page");
        String content = this.mockMvc.perform(get("/createSurvey"))
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
     * <p>Handle tests for Get mapping the view response page</p>
     * <br />
     * <strong>Expects:</strong> the <u>viewResponse</u> HTML page
     * @throws Exception
     */
    @Test
    public void testViewResponsePageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testViewResponsePageMapping()");
        System.out.println();
        System.out.println("Mocking get page '/viewResponse', expecting to retrieve an HTML page");
        String content = this.mockMvc.perform(get("/viewResponse"))
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
        // assert the title equals to the view response page title
        assert(title.equals("OpinionOwl | Responses"));
        System.out.println("------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the answer survey page using a valid survey id</p>
     * <br />
     * <strong>Expects:</strong> the <u>answerSurvey</u> HTML page
     * @throws Exception
     */
    @Test
    public void testValidAnswerSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testValidAnswerSurveyPageMapping() with a valid survey retrieving");
        System.out.println();
        System.out.println("Mocking get page '/answerSurvey', expecting to retrieve an HTML page");
        System.out.println("Creating a survey for the page to get. ID: 1L");
        long surveyId = 1;
        AppUser u1 = new AppUser("Test", "123");
        Survey survey = new Survey(u1, "TEST_SURVEY");
        u1.addSurvey(survey);
        userRepository.save(u1);

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
                        .param("surveyId", String.valueOf(surveyId)))
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

        System.out.println("Expects title: OpinionOwl | Answer Survey, Actual: " + title);
        // assert the title equals to the Answer Survey page title
        assert(title.equals("OpinionOwl | Answer Survey"));
        System.out.println("------------------------------");
    }

    /**
     * <p>Handle tests for Get mapping the answer survey page using an invalid survey id</p>
     * <br />
     * <strong>Expects:</strong> the <u>index</u> HTML page
     * @throws Exception
     */
    @Test
    public void testInvalidAnswerSurveyPageMapping() throws Exception {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("TESTING: testInvalidAnswerSurveyPageMapping() with an Invalid survey retrieving");
        System.out.println();
        System.out.println("Mocking get page '/answerSurvey', expecting to retrieve an HTML page");
        long surveyId = 3;
        System.out.println("Performing get of /answerSurvey with query ?surveyId=3. Final mapping = /answerSurvey?surveyId=3");
        String content = this.mockMvc.perform(get("/answerSurvey")
                        .param("surveyId", String.valueOf(surveyId)))
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

        System.out.println("Expects title: OpinionOwl | Answer Survey, Actual: " + title);
        // assert the title equals to the redirect page title
        assert(title.equals("OpinionOwl"));
        System.out.println("------------------------------");
    }

}