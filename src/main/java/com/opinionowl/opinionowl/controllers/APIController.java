package com.opinionowl.opinionowl.controllers;
import com.fasterxml.jackson.databind.ObjectMapper; // You might need to import this class

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Version 1 of the API layer for Opinion Owl
 */
@RestController
@RequestMapping("/api/v1")
@NoArgsConstructor
public class APIController {

    @Autowired
    SurveyRepository surveyRepo;


    @Autowired
    private UserRepository userRepository;


    /**
     * <p>Api call to handle the survey answers by a user.</p>
     * <br />
     * <strong>Api route: api/v1/postSurveyResponses</strong>
     * @param response HttpServletResponse server side response
     * @throws IOException
     */
    @PostMapping("/postSurveyResponses")
    public void postSurveyResponses(HttpServletResponse response) throws IOException {
        // handle save of survey data
        // redirect to home
        response.sendRedirect("/");
    }

    /**
     * <p>API Call to post a generated survey by the user. A survey generated JSON is required from the client</p>
     * <br />
     * <strong>Example of a JSON:</strong>
     * <pre>
     * json = {
     *     title: "title",
     *     textQuestions: ["question 1", "question 2"],
     *     radioQuestions: {
     *         "question 1": ["radio 1", "radio 2"],
     *         "question 2": ["radio 1", "radio 2", "radio 3"]
     *     },
     *     numericRanges: {
     *         "question 1": [1, 11],
     *         "question 2": [1, 5]
     *     }
     * }
     * </pre>
     * @param request HttpServletRequest request from the client
     * @return 200 if api was a success
     * @throws IOException
     */
    @PostMapping("/createSurvey")
    public int createSurvey(HttpServletRequest request) throws IOException {
        System.out.println("createSurvey() API");
        // read the json sent by the client
        BufferedReader reader = request.getReader();
        // create a string format of the json from the reader
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        String jsonData = jsonBuilder.toString();
        System.out.println("JSONDATA: " + jsonData);
        // Parse the JSON data using Jackson ObjectMapper

        //create the objects as java objects
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> surveyData = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, Object>>() {});
        // Extract specific data from the parsed JSON
        String title = (String) surveyData.get("title");
        List<String> textQuestions = (List<String>) surveyData.get("textQuestions");
        HashMap<String, List<String>> radioQuestions = (HashMap<String, List<String>>) surveyData.get("radioQuestions");
        HashMap<String, List<Integer>> numericRanges = (HashMap<String, List<Integer>>) surveyData.get("numericRanges");

        AppUser user = new AppUser("username", "password");
        userRepository.save(user);
        Survey survey = new Survey(user, title);
        user.addSurvey(survey);
        // add all the question types to the survey
        for (String questionTitle : textQuestions) {
            survey.addQuestion(new LongAnswerQuestion(survey, questionTitle, 50));
        }

        for (String questionTitle : radioQuestions.keySet()) {
            String[] radioQuestionsArr = new String[radioQuestions.get(questionTitle).size()];
            survey.addQuestion(new RadioChoiceQuestion(survey, questionTitle, radioQuestions.get(questionTitle).toArray(radioQuestionsArr)));
        }

        for (String questionTitle : numericRanges.keySet()) {
            List<Integer> ranges = numericRanges.get(questionTitle);
            survey.addQuestion(new RangeQuestion(survey, questionTitle, ranges.get(0), ranges.get(1), 1));
        }
        surveyRepo.save(survey);
        System.out.println("survey generated\n\n" + survey);
        return 200;
    }
}
