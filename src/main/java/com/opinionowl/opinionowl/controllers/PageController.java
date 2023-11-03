package com.opinionowl.opinionowl.controllers;

import com.opinionowl.opinionowl.models.Survey;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    SurveyRepository surveyRepo;

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Survey> surveys = surveyRepo.findAll();
        model.addAttribute("surveys", surveys);
        return "index";
    }

    @GetMapping("/createSurvey")
    public String getCreateSurveyPage(Model model) {
        return "createSurvey";
    }

    @GetMapping("/answerSurvey")
    public String getAnswerSurveyPage(@RequestParam(value = "surveyId") Long surveyId, Model model) {
        Optional<Survey> surveyO = surveyRepo.findById(surveyId);
        if (surveyO.isPresent()) {
            Survey survey = surveyO.get();
            System.out.println("Survey found:\n");
            System.out.println(survey);
            model.addAttribute("survey", survey);
        } else {
            System.out.println("ERROR: Survey could not be found");
            System.exit(1);
        }
        return "answerSurvey";
    }

    @GetMapping("/viewResponse")
    public String getViewResponsePage(Model model) {
        return "viewResponse";
    }
}
