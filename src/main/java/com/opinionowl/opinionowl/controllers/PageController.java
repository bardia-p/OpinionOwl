package com.opinionowl.opinionowl.controllers;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * Route controller for Opinion Owl pages
 */
@Controller
@NoArgsConstructor
public class PageController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    SurveyRepository surveyRepo;



    /**
     * <p>Home route that gets all the surveys in the database, sends it to the model and directs the user to the home page</p>
     * @param model Model, the client Model
     * @return String, the html template
     */
    @GetMapping("/")
    public String getHomePage(Model model, HttpServletRequest request) {
        List<Survey> surveys = surveyRepo.findAll();
        List<Survey> openSurveys = new ArrayList<>();
        for (Survey s: surveys){
            if (!s.isClosed()){
                openSurveys.add(s);
            }
        }
        model.addAttribute("surveys", openSurveys);
        CookieController.setUsernameCookie(model, request);
        return "index";
    }



    /**
     * <p>Route for the create survey page</p>
     * @param model Model, the client Model
     * @return String ,the html template
     */
    @GetMapping("/createSurvey")
    public String getCreateSurveyPage(Model model, HttpServletRequest request) {
        CookieController.setUsernameCookie(model, request);
        return "createSurvey";
    }

    /**
     * <p>Route to direct the client to the answer survey page, given a survey id to pass the Survey object to the Model</p>
     * <br />
     * <strong>Example call: /answerSurvey?surveyId=1</strong>
     * @param surveyId Long, the ID associated with a survey
     * @param model Model, the client Model
     * @return String, the html template
     */
    @GetMapping("/answerSurvey")
    public String getAnswerSurveyPage(@RequestParam(value = "surveyId") Long surveyId, Model model, HttpServletRequest request) {
        // find the survey by id
        Optional<Survey> surveyO = surveyRepo.findById(surveyId);
        if (surveyO.isPresent()) {
            // was able to obtain a survey from the database by id, and grab it from the Optional Object
            Survey survey = surveyO.get();
            System.out.println("Survey found:");
            System.out.println(survey);
            // cast the order of the questions to the associtate subclass they belong to
            // Cast in hashmaps as <question#, Question>
            List<Question> q = survey.getQuestions();
            HashMap<Integer, LongAnswerQuestion> longAnswerQuestions = new HashMap<>();
            HashMap<Integer, RadioChoiceQuestion> radioChoiceQuestions = new HashMap<>();
            HashMap<Integer, RangeQuestion> rangeQuestionQuestions = new HashMap<>();
            int numQuestions = q.size();
            String title = survey.getTitle();
            for (int i = 0; i<numQuestions; i++) {
                Question question = q.get(i);
                int questionNumber = i + 1;
                if (question.getType() == QuestionType.LONG_ANSWER) {
                    longAnswerQuestions.put(questionNumber, (LongAnswerQuestion) question);
                } else if (question.getType() == QuestionType.RADIO_CHOICE) {
                    radioChoiceQuestions.put(questionNumber, (RadioChoiceQuestion) question);
                } else if (question.getType() == QuestionType.RANGE) {
                    rangeQuestionQuestions.put(questionNumber, (RangeQuestion) question);
                }
            }
            // send the Model the data necessary for the page
            model.addAttribute("surveyId", survey.getId());
            model.addAttribute("surveyTitle", title);
            model.addAttribute("numberOfQuestions", numQuestions);
            model.addAttribute("longAnswerQuestions", longAnswerQuestions);
            model.addAttribute("radioChoiceQuestions", radioChoiceQuestions);
            model.addAttribute("rangeQuestionQuestions", rangeQuestionQuestions);
        } else {
            // could not find survey, Error
            // TODO: Redirect the user to a Error boundary page, or maybe the home page instead with a Toast message
            // for now redirect to home page
            System.out.println("ERROR: Survey could not be found. Redirecting to Index");
            return "index";
        }
        CookieController.setUsernameCookie(model, request);
        return "answerSurvey";
    }

    /**
     * <p>Route to direct the client to view the survey responses.</p>
     * <br />
     * <strong>Example call: /viewResponse?surveyId=1</strong>
     * @param surveyId Long, the ID associated with a survey
     * @param model Model, the client Model
     * @return String, the html template
     */
    @GetMapping("/viewResponse")
    public String getViewResponsePage(@RequestParam(value = "surveyId") Long surveyId, Model model, HttpServletRequest request) {
        // find the survey by id
        Optional<Survey> surveyO = surveyRepo.findById(surveyId);
        if (surveyO.isPresent()) {
            // was able to obtain a survey from the database by id, and grab it from the Optional Object
            Survey survey = surveyO.get();
            System.out.println("Survey found:");
            System.out.println(survey);

            List<Question> questions = survey.getQuestions();
            Map<Long, Map<String, Integer>> longAnswerResponses = new HashMap<>();
            Map<Long, Question> questionMap = new HashMap<>();

            // Populate the answers
            String title = survey.getTitle();
            for (Question q: questions) {
                questionMap.put(q.getId(), q);
                if (q.getType() == QuestionType.LONG_ANSWER) {
                    longAnswerResponses.put(q.getId(), survey.getResponsesForQuestion(q.getId()));
                }
            }

            System.out.println(longAnswerResponses);
            // send the Model the data necessary for the page
            model.addAttribute("surveyId", survey.getId());
            model.addAttribute("surveyTitle", title);
            model.addAttribute("questionMap", questionMap);
            model.addAttribute("longAnswerResponses", longAnswerResponses);
        } else {
            // could not find survey, Error
            // TODO: Redirect the user to a Error boundary page, or maybe the home page instead with a Toast message
            // for now redirect to home page
            System.out.println("ERROR: Survey could not be found. Redirecting to Index");
            return "index";
        }
        CookieController.setUsernameCookie(model, request);
        return "viewResponse";
    }

    /**
     * GET mapping for register user.
     * @return A String HTML template for registerUser.
     */
    @GetMapping("/registerUser")
    public String addUser(){
        return "registerUser";
    }

    /**
<<<<<<<<< Temporary merge branch 1
     * Route to direct the client to view the list of existing surveys to manage them.
     * @return, String HTML template for manageSurvey
     */
    @GetMapping("/manageSurvey")
    public String getManageSurvey(@RequestParam(value = "userId") Long userId, Model model, HttpServletRequest request) {
        List<Survey> surveys = surveyRepo.findAll();
        List<Survey> userSurveys = new ArrayList<>();

        for (Survey s : surveys){
            if (s.getUser().getId().equals(userId)){
                userSurveys.add(s);
            }
        }
        model.addAttribute("surveys", userSurveys);
        CookieController.setUsernameCookie(model, request);
        return "manageSurvey";
    }

    /**
     * GET mapping for login user.
     * @return A string HTML template for loginUsers
     */
    @GetMapping("/loginUser")
    public String loginUser(){
        return "loginUsers";
    }
}
