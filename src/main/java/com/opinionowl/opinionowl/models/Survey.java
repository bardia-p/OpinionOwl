package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The survey class which contains all the information needed to create survey.
 */
@Entity
@Getter
@Setter
public class Survey {
    // Keeps track of the Id of the survey.
    @Id
    @GeneratedValue
    private Long id;

    // Keeps track of the questions of the survey.
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Question> questions;

    // Keeps track of the survey responses.
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Response> responses;

    // Keeps track of whether the survey is open or closed.
    private boolean closed;

    /**
     * The default constructor for the survey.
     */
    public Survey(){
        this.questions = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.closed = false;
    }

    /**
     * Adds a question to the survey.
     *
     * @param question the question to add to the survey.
     */
    public void addQuestion(Question question){
        this.questions.add(question);
    }

    /**
     * Removes a question from the survey.
     * @param questionId the Id of the question.
     * @return true if successfully removed the question, false otherwise.
     */
    public boolean removeQuestion(Long questionId){
        return this.questions.removeIf(q -> q.getId() == questionId);
    }

    /**
     * Adds a response to the survey.
     * @param response the response to add to the survey.
     */
    public boolean addResponse(Response response){
        if (!closed){
            this.responses.add(response);
            return true;
        }
        return false;
    }

    /**
     * Removes the response from the survey.
     * @param responseId the id of the response to remove.
     * @return true if successfully remove, false otherwise.
     */
    public boolean removeResponse(Long responseId){
        return this.responses.removeIf(r -> r.getId() == responseId);
    }

    /**
     * Returns a list of response for a specific question.
     * @param questionId the id of the question.
     * @return a list of the responses for that question.
     */
    public List<String> getResponsesForQuestion(Long questionId){
        List<String> answers = new ArrayList<>();
        for (Response r: responses){
            answers.add(r.getAnswers().get(questionId));
        }
        return answers;
    }

    /**
     * @return the survey in string form.
     */
    @Override
    public String toString(){
        String res = "Survey #" + id + " Closed? " + closed;
        res += "\n-----Questions-----";
        for (Question q: questions){
            res += "\n" + q.toString();
        }
        res += "\n-----Response-----";
        for (Response r: responses){
            res += "\n" + r.toString();
        }
        return res;
    }
}
