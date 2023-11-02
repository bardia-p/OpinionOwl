package com.opinionowl.opinionowl.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class APIController {

    //    @Autowired
    //    SurveyRepository sRepo;

    @GetMapping("/getAllSurveys")
    public List<Integer> getAllSurveys() {
        // change Integer to a list of Survey Entities
        List<Integer> surveys = new ArrayList<>();
        surveys.add(1);
        surveys.add(2);
        return surveys;
    }

    @PostMapping("/postSurveyResponses")
    public void postSurveyResponses(HttpServletResponse response) throws IOException {
        // change Integer to a list of Survey Entities
        // handle save of data
        // redirect to home
        response.sendRedirect("/");
    }

    @PostMapping("/createSurvey")
    public void createSurvey(HttpServletResponse response) throws IOException {
        // change Integer to a list of Survey Entities
        // handle save of data
        // redirect to home
        response.sendRedirect("/");
    }
}
