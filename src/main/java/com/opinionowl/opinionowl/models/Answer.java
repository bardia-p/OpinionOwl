package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The class in charge of holding the indivdual answers for each response.
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

}
