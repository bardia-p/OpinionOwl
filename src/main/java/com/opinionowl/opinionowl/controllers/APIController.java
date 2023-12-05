package com.opinionowl.opinionowl.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.opinionowl.opinionowl.models.*;
import com.opinionowl.opinionowl.repos.QuestionRepository;
import com.opinionowl.opinionowl.repos.ResponseRepository;
import com.opinionowl.opinionowl.repos.SurveyRepository;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
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
    private UserRepository userRepo;

    @Autowired
    private SurveyRepository surveyRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ResponseRepository responseRepo;

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

        String username = CookieController.getUsernameFromCookie(request);
        AppUser user = null;
        if (username != null){
            user = userRepo.findByUsername(username).orElse(null);
        }

        String jsonData = JSONBuilder(request);
        System.out.println("JSONDATA: " + jsonData);
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<Survey> surveyO =  surveyRepo.findById(surveyId);

        Survey s = surveyO.orElse(null);

        if (s == null) {
            System.out.println("Could not find survey with ID: " + surveyId);
            return 400;
        }

        if (s.isClosed()){
            System.out.println("Survey is closed!");
            return 400;
        }

        Response responseToSurvey = new Response(s);
        HashMap<Long, String> surveyData = objectMapper.readValue(jsonData, new TypeReference<HashMap<Long, String>>() {});
        for (Long questionId : surveyData.keySet()) {
            String content = surveyData.get(questionId);
            responseToSurvey.addAnswer(questionId, content);
        }

        responseRepo.save(responseToSurvey);

        // Add the response to the user.
        if (user != null){
            user.addResponse(responseToSurvey.getId());
            userRepo.save(user);
        }

        // TODO: maybe consider toast messages? for now printing is fine for proof
        System.out.println(responseToSurvey);

        return 200;
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

        String username = CookieController.getUsernameFromCookie(request);
        if (username == null){
            System.out.println("You must be logged in first");
            return 400;
        }

        String jsonData = this.JSONBuilder(request);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> surveyData = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, Object>>() {});

        // Extract specific data from the parsed JSON
        String title = (String) surveyData.get("title");
        List<String> textQuestions = (List<String>) surveyData.get("textQuestions");
        HashMap<String, List<String>> radioQuestions = (HashMap<String, List<String>>) surveyData.get("radioQuestions");
        HashMap<String, List<Integer>> numericRanges = (HashMap<String, List<Integer>>) surveyData.get("numericRanges");

        Optional<AppUser> optionalAppUser = userRepo.findByUsername(username);
        AppUser user;
        if (optionalAppUser.isPresent()){
            user = optionalAppUser.get();
        } else {
            System.out.println("Could not find the user!");
            return 400;
        }

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
    public String getSurveyResults(@PathVariable("id") String id, HttpServletRequest request) throws JSONException {
        System.out.println("getSurveyResults() API");

        String username = CookieController.getUsernameFromCookie(request);
        if (username == null){
            System.out.println("You must be logged in first");
            return "";
        }

        Long surveyId = Long.valueOf(id);
        Optional<Survey> s = surveyRepo.findById(surveyId);
        JSONObject resObject = new JSONObject();
        if (s.isPresent()) {
            Survey survey = s.get();
            if (!survey.getUser().getUsername().equals(username)){
                return "";
            }
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
        for (AppUser appUser: userRepo.findAll()){
            if (appUser.getUsername().equals(username)){
                return 401;
            }
        }
        AppUser appUser = new AppUser(username, password);
        userRepo.save(appUser);
        System.out.println(appUser);
        return 200;
    }

    /**
     * API call to close a survey of a specified survey id
     * @param id, id of the logged-in user
     * @return 200, if the API was a success.
     * @throws IOException
     */
    @PostMapping("/closeSurvey/{id}")
    public int closeSurvey(@PathVariable("id") Long id, HttpServletRequest request) throws IOException {
        System.out.println("closeSurvey() API");

        String username = CookieController.getUsernameFromCookie(request);
        if (username == null){
            System.out.println("You must be logged in first");
            return 400;
        }

        Survey survey = surveyRepo.findById(id).orElse(null);
        if (survey == null) {
            return 400;
        }

        if (!survey.getUser().getUsername().equals(username)){
            return 401;
        }

        survey.setClosed(true);
        surveyRepo.save(survey);
        return 200;
    }

    /**
     * API call to get all the survey responses.
     * @param username, username of the logged-in user
     * @return 200, if the API was a success.
     * @throws IOException
     */
    @GetMapping("/savedResponses/{username}")
    public String getSavedResponses(@PathVariable("username") String username, HttpServletRequest request) throws IOException, JSONException {
        System.out.println("getSavedResponses() API");

        String storedUsername = CookieController.getUsernameFromCookie(request);
        if (storedUsername == null){
            System.out.println("You must be logged in first");
            return "";
        }

        if (!username.equals(storedUsername)){
            return "";
        }

        AppUser user = userRepo.findByUsername(username).orElse(null);
        if (user == null) {
            return "";
        }

        JSONObject rObject = new JSONObject();

        for (Long rid: user.getResponses()){
            Response r = responseRepo.findById(rid).orElse(null);
            if (r != null){
                JSONObject responseObject = new JSONObject();
                JSONObject answerObject = new JSONObject();
                for (Answer a : r.getAnswers()){
                    Question q = questionRepo.findById(a.getQuestion()).orElse(null);
                    if (q != null){
                        answerObject.put(q.getPrompt(), a.getContent());
                    }
                }
                responseObject.put("answers", answerObject);
                responseObject.put("surveyTitle", r.getSurvey().getTitle());
                rObject.put(r.getId().toString(), responseObject);
            }
        }

        System.out.println(rObject);
        return rObject.toString();
    }

    /**
     * <p>API Call to login a user by verifying that it exists in the userRespository</p>
     * @param request HttpServletRequest, a request from the client.
     * @return 200, if user successfully logs in, 401 if user is not authenticated properly.
     * @throws IOException
     */
    @PostMapping("/loginUser")
    public int loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException{
        AppUser loggedInUser = null;
        String jsonData = this.JSONBuilder(request);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> userData = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, String>>() {
        });
        String username = userData.get("username");
        String password = userData.get("password");
        for(AppUser user : userRepo.findAll()){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                loggedInUser = user;
                break;
            }
        }
        if(loggedInUser == null){
            return 401; //indicates unauthorized
        }

        Cookie cookie = new Cookie( "username", loggedInUser.getUsername());
        cookie.setPath("/");
        response.addCookie(cookie);
        return 200;
    }

    /**
     * <p>API Call to log out a user by deleting their cookies</p>
     * @param response HttpServletResponse server side response.
     * @param request HttpServletRequest, a request from the client.
     * @return 200, if the API was a success.
     * @throws IOException
     */
    @PostMapping("/logout")
    public int logoutUser(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("username")) {
                    Cookie cRemove = new Cookie("username", "");
                    cRemove.setMaxAge(0);
                    cRemove.setPath("/");
                    response.addCookie(cRemove);
                    break;
                }
            }
        }
        return 200;
    }

    /**
     * <p>Handler for retrieving the survey questions based on its ID as a JSON</p>
     * <strong>Example return json will full data of questions</strong>
     * <pre>
     *     returnJson = {
     *         "1": {
     *             "type": "Long Answer",
     *             "prompt": "Question prompt",
     *         },
     *         "2": {
     *             "type": "Radio Choice",
     *             "prompt": "Question prompt",
     *             "choices": ["choice 1", "choice 2"],
     *         },
     *         "3": {
     *             "type": "Range",
     *             "prompt": "Question prompt",
     *             "ranges": [0, 22],
     *         }
     *     }
     * </pre>
     * @param id String, the survey ID
     * @param request
     * @return JSON of survey questions
     * @throws JSONException
     */
    @GetMapping("/getSurveyQuestions/{id}")
    public String getSurveyQuestions(@PathVariable("id") String id, HttpServletRequest request) throws JSONException  {
        System.out.println("getSurveyResults() API");

        Optional<Survey> s = surveyRepo.findById(Long.valueOf(id));
        JSONObject resObject = new JSONObject();
        if (s.isPresent()) {
            Survey survey = s.get();
            JSONObject questionObject = new JSONObject();
            for (Question q : survey.getQuestions()) {
                JSONObject indQObject = new JSONObject();
                indQObject.put("type", q.getType().getType());
                indQObject.put("prompt", q.getPrompt());
                if (q.getType() == QuestionType.RANGE) {
                    RangeQuestion rangeQuestion = (RangeQuestion) q;
                    int[] ranges = {rangeQuestion.getLower(), rangeQuestion.getUpper()};
                    indQObject.put("ranges", ranges);
                } else if (q.getType() == QuestionType.RADIO_CHOICE) {
                    indQObject.put("choices", ((RadioChoiceQuestion) q).getChoices());
                }
                questionObject.put(q.getId().toString(), indQObject);
            }
            resObject.put("questions", questionObject);
        } else {
            System.out.println("No survey found of ID " + id);
            return "";
        }
        return resObject.toString();
    }

    /**
     * <p>Handle the update of a survey based on its ID</p>
     * @param id The survey ID
     * @param request
     * @return 200 if successful, otherwise 400
     * @throws IOException
     */
    @PostMapping("/updateSurvey/{id}")
    public int updateSurvey(@PathVariable("id") String id, HttpServletRequest request) throws IOException {
        System.out.println("Updating survey API()");

        String username = CookieController.getUsernameFromCookie(request);
        if (username == null){
            System.out.println("You must be logged in first");
            return 400;
        }

        String jsonData = this.JSONBuilder(request);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> surveyData = objectMapper.readValue(jsonData, new TypeReference<HashMap<String, Object>>() {});
        // Extract specific data from the parsed JSON
        String title = (String) surveyData.get("title");
        List<String> textQuestions = (List<String>) surveyData.get("textQuestions");
        HashMap<String, List<String>> radioQuestions = (HashMap<String, List<String>>) surveyData.get("radioQuestions");
        HashMap<String, List<Integer>> numericRanges = (HashMap<String, List<Integer>>) surveyData.get("numericRanges");

        AppUser appUser = userRepo.findByUsername(username).orElse(null);
        Survey currSurvey = surveyRepo.findById((Long.valueOf(id))).orElse(null);
        Survey newSurvey = new Survey(appUser, title);

        if (appUser == null) {
            System.out.println("Could not find the user!");
            return 400;
        }

        if (currSurvey == null) {
            System.out.println("Could not find survey");
            return 400;
        }

        if (!Objects.equals(currSurvey.getUser().getUsername(), appUser.getUsername())) {
            System.out.println("Not the user associated with the user");
            return 400;
        }

        appUser.removeSurvey(currSurvey.getId());
        surveyRepo.deleteById(currSurvey.getId());
        // add all the question types to the survey
        for (String questionTitle : textQuestions) {
            newSurvey.addQuestion(new LongAnswerQuestion(newSurvey, questionTitle, 50));
        }

        for (String questionTitle : radioQuestions.keySet()) {
            String[] radioQuestionsArr = new String[radioQuestions.get(questionTitle).size()];
            newSurvey.addQuestion(new RadioChoiceQuestion(newSurvey, questionTitle, radioQuestions.get(questionTitle).toArray(radioQuestionsArr)));
        }

        for (String questionTitle : numericRanges.keySet()) {
            List<Integer> ranges = numericRanges.get(questionTitle);
            newSurvey.addQuestion(new RangeQuestion(newSurvey, questionTitle, ranges.get(0), ranges.get(1), 1));
        }
        appUser.addSurvey(newSurvey);
        surveyRepo.save(newSurvey);
        return 200;
    }
}
