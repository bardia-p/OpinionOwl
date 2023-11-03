package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The survey class which contains all the information needed to create survey.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Survey {
    // Keeps track of the Id of the survey.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Keeps track of the questions of the survey.
    @OneToMany(mappedBy = "survey", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Question> questions;

    // Keeps track of the survey responses.
    @OneToMany(mappedBy = "survey", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Response> responses;

    // Keeps track of whether the survey is open or closed.
    private boolean closed;

    // The title for the survey.
    private String title;

    // The user for the survey.
    @ManyToOne
    private User user;

    /**
     * The default constructor for the survey.
     */
    public Survey(String title){
        this.title = title;
        this.questions = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.closed = false;
    }

    /**
     * Adds a question to the survey.
     *
     * @param question the question to add to the survey.
     */
    public boolean addQuestion(Question question){
        if (!closed) {
            this.questions.add(question);
            return true;
        }
        return false;
    }

    /**
     * Removes a question from the survey.
     * @param questionId the Id of the question.
     * @return true if successfully removed the question, false otherwise.
     */
    public boolean removeQuestion(Long questionId){
        if (!closed) {
            return this.questions.removeIf(q -> q.getId() == questionId);
        }
        return false;
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
        List<String> result = new ArrayList<>();
        for (Response r: responses){
            for (Answer a: r.getAnswers()){
                if (a.getQuestion() == questionId){
                    result.add(a.getContent());
                }
            }
        }
        return result;
    }

    /**
     * @return the survey in string form.
     */
    @Override
    public String toString(){
        String res = "Survey#" + id + " Title:" + title + " Closed?" + closed;
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
