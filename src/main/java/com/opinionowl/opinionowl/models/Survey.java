package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

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
    @OneToMany(mappedBy = "survey", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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
    private AppUser user;

    /**
     * The default constructor for the survey.
     */
    public Survey(AppUser user, String title){
        this.user = user;
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
            return this.questions.removeIf(q -> Objects.equals(q.getId(), questionId));
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
        return this.responses.removeIf(r -> Objects.equals(r.getId(), responseId));
    }

    /**
     * Returns a list of response for a specific question.
     * @param questionId the id of the question.
     * @return a list of the responses for that question.
     */
    public Map<String, Integer> getResponsesForQuestion(Long questionId){
        Map<String, Integer> result = new HashMap<>();
        Optional<Question> question = this.questions.stream().filter(q -> q.getId().equals(questionId)).findAny();
        if (question.isPresent()) {
            Question q = question.get();
            // Adds default values for radio choice questions.
            if (q.getType() == QuestionType.RADIO_CHOICE){
                for (String c: ((RadioChoiceQuestion)q).getChoices()){
                    result.put(c, 0);
                }
            } else if (q.getType() == QuestionType.RANGE) { // Adds default values for range questions.
                RangeQuestion rq = (RangeQuestion) q;
                for (int i = rq.getLower(); i <= rq.getUpper(); i += rq.getIncrement()){
                    result.put(Integer.toString(i), 0);
                }
            }
            for (Response r : responses) {
                for (Answer a : r.getAnswers()) {
                    if (Objects.equals(a.getQuestion(), questionId)) {
                        String content = a.getContent();
                        if (result.containsKey(content)) {
                            result.put(content, result.get(content) + 1);
                        } else {
                            result.put(content, 1);
                        }
                    }
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
        StringBuilder res = new StringBuilder("Survey#" + id + " Title:" + title + " Closed?" + closed);
        res.append("\n-----Questions-----");
        for (Question q: questions){
            res.append("\n").append(q.toString());
        }
        res.append("\n-----Response-----");
        for (Response r: responses){
            res.append("\n").append(r.toString());
        }
        return res.toString();
    }

    /**
     *
     * @param o object that is being compared with
     * @return boolean value saying whether objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Survey survey = (Survey) o;
        return closed == survey.closed && Objects.equals(id, survey.id) && Objects.equals(questions, survey.questions) && Objects.equals(responses, survey.responses) && Objects.equals(title, survey.title);
    }

}
