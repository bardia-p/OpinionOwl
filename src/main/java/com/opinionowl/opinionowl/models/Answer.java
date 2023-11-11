package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * The class in charge of holding the individual answers for each response.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer {
    // The id of the answer.
    @Id
    @GeneratedValue
    private Long id;

    // The response which the answer belongs to.
    @ManyToOne
    private Response response;

    // The id of the question.
    private Long question;

    // The contents of the response.
    private String content;

    /**
     * The constructor for answer.
     * @param response the response for which the answe belongs to.
     * @param question the question id.
     * @param content the content of the reply.
     */
    public Answer(Response response, Long question, String content){
        this.response = response;
        this.question = question;
        this.content = content;
    }

    /**
     * @return the answer in string form.
     */
    @Override
    public String toString(){
        return "Answer#" + id + " value:" + content;
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
        Answer answer = (Answer) o;
        return Objects.equals(id, answer.id) && Objects.equals(question, answer.question) && Objects.equals(content, answer.content);
    }

}

