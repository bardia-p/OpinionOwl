package com.opinionowl.opinionowl.controllers;
import com.fasterxml.jackson.databind.ObjectMapper; // You might need to import this class

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class APIController {

    //    @Autowired
    //    SurveyRepository sRepo;

    @PostMapping("/postSurveyResponses")
    public void postSurveyResponses(HttpServletResponse response) throws IOException {
        // change Integer to a list of Survey Entities
        // handle save of data
        // redirect to home
        response.sendRedirect("/");
    }

    @PostMapping("/createSurvey")
    public void createSurvey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change Integer to a list of Survey Entities
        // handle save of data
        // redirect to home
        // Read the JSON data from the request
        System.out.println("Hit create Survey");
        BufferedReader reader = request.getReader();
        System.out.println(reader);
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        String jsonData = jsonBuilder.toString();

        ObjectMapper objectMapper = new ObjectMapper();
//        SurveyData surveyData = objectMapper.readValue(jsonData, SurveyData.class);


        response.sendRedirect("/");
    }
}
