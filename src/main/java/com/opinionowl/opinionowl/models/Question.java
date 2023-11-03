package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * The Question class which keeps track of the survey questions.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Question {
    // The id of the question.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // The prompt for the question.
    private String prompt;

    // The type of the question.
    private QuestionType type;

    @ManyToOne
    private Survey survey;

    /**
     * The constructor for the question class.
     * @param prompt the prompt for the question.
     * @param type the type of the question
     */
    public Question(String prompt, QuestionType type){
        this.prompt = prompt;
        this.type = type;
    }

    /**
     * @return the question in string form.
     */
    @Override
    public String toString(){
        return "Question id:" + id + " prompt:" + prompt;
    }
}
