package com.opinionowl.opinionowl.controllers;
import com.fasterxml.jackson.databind.ObjectMapper; // You might need to import this class

import com.opinionowl.opinionowl.models.LongAnswerQuestion;
import com.opinionowl.opinionowl.models.RadioChoiceQuestion;
import com.opinionowl.opinionowl.models.RangeQuestion;
import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class APIController {

    //    @Autowired
    //    SurveyRepository sRepo;
    @Autowired
    SurveyRepository surveyRepo;

    @PostMapping("/postSurveyResponses")
    public void postSurveyResponses(HttpServletResponse response) throws IOException {
        // change Integer to a list of Survey Entities
        // handle save of data
        // redirect to home
        response.sendRedirect("/");
    }

    @GetMapping("/getSurveyData")
    public Survey getSurveyData(@RequestParam(value = "surveyId") Long surveyId) throws IOException {
        Optional<Survey> surveyO = surveyRepo.findById(surveyId);
        if (surveyO.isPresent()) {
            Survey survey = surveyO.get();
            System.out.println("Survey found:\n");
            System.out.println(survey);
            return survey;
        } else {
            System.out.println("ERROR: Survey could not be found");
            System.exit(1);
        }
        return null;
    }

    @PostMapping("/createSurvey")
    public int createSurvey(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        Survey survey = new Survey(title);
        // add all the question types to the survey
        for (String questionTitle : textQuestions) {
            survey.addQuestion(new LongAnswerQuestion(questionTitle, 50));
        }

        for (String questionTitle : radioQuestions.keySet()) {
            String[] radioQuestionsArr = new String[radioQuestions.get(questionTitle).size()];
            survey.addQuestion(new RadioChoiceQuestion(questionTitle, radioQuestions.get(questionTitle).toArray(radioQuestionsArr)));
        }

        for (String questionTitle : numericRanges.keySet()) {
            List<Integer> ranges = numericRanges.get(questionTitle);
            survey.addQuestion(new RangeQuestion(questionTitle, ranges.get(0), ranges.get(1), 1));
        }
        surveyRepo.save(survey);
        System.out.println("survey generated\n\n" + survey);
        return 200;
    }
}
