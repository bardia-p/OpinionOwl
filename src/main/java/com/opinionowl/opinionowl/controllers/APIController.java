package com.opinionowl.opinionowl.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.ResponseRepository;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.json.*;
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
    private SurveyRepository surveyRepo;

    @Autowired
    private ResponseRepository responseRepo;

    @Autowired
    private UserRepository userRepository;

    /**
     * Method for building a JSON format from a request.
     * @param request An HttpServletRequest request.
     * @return An ObjectMapper that maps a request's parameter to a particular value in JSON format.
     * @throws IOException
     */
    private String JSONBuilder(HttpServletRequest request) throws IOException {
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
        return jsonData;
    }

    /**
     * <p>Api call to handle the survey response by a user.</p>
     * <br />
     * <strong>Api route: api/v1/postSurveyResponses?surveyId=${your id}</strong>
     * <strong>Example of a json</strong>
     * <pre>
     *     JSON DATA: {"1":"tony","2":"on","3":"11"...etc}
     * </pre>
     * @throws IOException
     */
    @PostMapping("/postSurveyResponses/{surveyId}")
    public int postSurveyResponses(@PathVariable("surveyId") Long surveyId, HttpServletRequest request) throws IOException {
        // handle save of survey data
        // redirect to home
        System.out.println("Post survey response api API");
        String jsonData = JSONBuilder(request);
        System.out.println("JSONDATA: " + jsonData);
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<Survey> surveyO =  surveyRepo.findById(surveyId);

        Survey s = surveyO.orElse(null);

        if (s == null) {
            System.out.println("Could not find survey with ID: " + surveyId);
            return 400;
        }

        Response responseToSurvey = new Response(s);
        HashMap<Long, String> surveyData = objectMapper.readValue(jsonData, new TypeReference<HashMap<Long, String>>() {});
        for (Long questionId : surveyData.keySet()) {
            String content = surveyData.get(questionId);
            responseToSurvey.addAnswer(questionId, content);
        }

        responseRepo.save(responseToSurvey);
        // TODO: maybe consider toast messages? for now printing is fine for proof
        System.out.println(responseToSurvey);

        return 200;
    }

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
        String jsonData = this.JSONBuilder(request);
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

    /**
     * <p>API Call to get the results for a specific survey. </p>
     * <br />
     * <strong>Example of a JSON:</strong>
     * <pre>
     * json = {
     *     "questions": {
     *         "1": {
     *             "type": "Long Answer",
     *             "prompt": "test1"
     *         },
     *         "2": {
     *             "type": "Radio Choice",
     *             "prompt": "test2"
     *         },
     *         "3": {
     *             "type": "Range",
     *             "prompt": "test3"
     *         }
     *     },
     *     "responses": {
     *         "1": {
     *             "hi": 1,
     *             "bye": 1
     *         },
     *         "2": {
     *             "a": 1,
     *             "c": 1,
     *             "d": 0
     *         },
     *         "3": {"1": 1, "2": 0, "3": 0, "4": 0, "5": 1}
     *     }
     * }
     * </pre>
     * @param id String the id of the survey.
     * @return resObject, the results of the survey in JSON format.
     * @throws JSONException
     */
    @GetMapping("/getSurveyResults/{id}")
    public String getSurveyResults(@PathVariable("id") String id) throws JSONException {
        System.out.println("getSurveyResults() API");
        Long surveyId = Long.parseLong(id);
        Optional<Survey> s = surveyRepo.findById(surveyId);
        JSONObject resObject = new JSONObject();
        if (s.isPresent()) {
            Survey survey = s.get();
            HashMap<Long, String> questions = new HashMap<>();
            JSONObject qObject = new JSONObject();
            JSONObject rObject = new JSONObject();
            for (Question q : survey.getQuestions()) {
                JSONObject indQObject = new JSONObject();
                indQObject.put("type", q.getType().getType());
                indQObject.put("prompt", q.getPrompt());
                qObject.put(Long.toString(q.getId()), indQObject);

                Map<String, Integer> qRes = survey.getResponsesForQuestion(q.getId());
                JSONObject indRObject = new JSONObject();
                for (String val : qRes.keySet()) {
                    indRObject.put(val, qRes.get(val));
                }
                rObject.put(Long.toString(q.getId()), indRObject);
            }
            resObject.put("questions", qObject);
            resObject.put("responses", rObject);
        }
        System.out.println(resObject);
        return resObject.toString();
    }

    /**
     * <p>API Call to post a new user. A user generated JSON is required from the client</p>
     * <br />
     * <strong>Example of a JSON:</strong>
     * <pre>
     * json = {
     *     username: "username",
     *     password: "password"
     * }
     * </pre>
     * @param request HttpServletRequest, a request from the client.
     * @return 200, if the API was a success.
     * @throws IOException
     */
    @PostMapping("/createUser")
    public int createUser(HttpServletRequest request) throws IOException {
        System.out.println("createUser() API");
        String jsonData = this.JSONBuilder(request);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> userData = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, String>>() {});
        String username = userData.get("username");
        String password = userData.get("password");
        AppUser appUser = new AppUser(username, password);
        userRepository.save(appUser);
        System.out.println(appUser);
        return 200;
    }

    @PostMapping("/loginUser")
    public int loginUser(HttpServletRequest request) throws IOException{
        AppUser loggedInUser = null;
        String jsonData = this.JSONBuilder(request);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> userData = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, String>>() {
        });
        String username = userData.get("username");
        String password = userData.get("password");
        for(AppUser user : userRepository.findAll()){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                loggedInUser = user;
                break;
            }
        }
        if(loggedInUser == null){
            return 401; //indicates unauthorized
        }
        return 200;
    }
}
