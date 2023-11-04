package com.opinionowl.opinionowl.controllers;

import com.opinionowl.opinionowl.models.*;
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
            System.out.println("Survey found:");
            System.out.println(survey);
            List<Question> q = survey.getQuestions();
            HashMap<Integer, LongAnswerQuestion> longAnswerQuestions = new HashMap<>();
            HashMap<Integer, RadioChoiceQuestion> radioChoiceQuestions = new HashMap<>();
            HashMap<Integer, RangeQuestion> rangeQuestionQuestions = new HashMap<>();
            int numQuestions = q.size();
            String title = survey.getTitle();
            for (int i = 0; i<numQuestions; i++) {
                Question question = q.get(i);
                int questionNumber = i + 1;
                if (question instanceof LongAnswerQuestion) {
                    longAnswerQuestions.put(questionNumber, (LongAnswerQuestion) question);
                } else if (question instanceof RadioChoiceQuestion) {
                    radioChoiceQuestions.put(questionNumber, (RadioChoiceQuestion) question);
                } else if (question instanceof RangeQuestion) {
                    rangeQuestionQuestions.put(questionNumber, (RangeQuestion) question);
                }
            }
            model.addAttribute("surveyId", survey.getId());
            model.addAttribute("surveyTitle", title);
            model.addAttribute("numberOfQuestions", numQuestions);
            model.addAttribute("longAnswerQuestions", longAnswerQuestions);
            model.addAttribute("radioChoiceQuestions", radioChoiceQuestions);
            model.addAttribute("rangeQuestionQuestions", rangeQuestionQuestions);
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
