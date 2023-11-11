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
    public Question(Survey survey, String prompt, QuestionType type){
        this.survey = survey;
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

    public boolean equals(Question comparedQuestion){
        if(!(this.id.equals(comparedQuestion.getId()))){
            return false;
        }
        if(!(this.prompt.equals(comparedQuestion.getPrompt()))){
            return false;
        }
        if(!(this.type.equals(comparedQuestion.getType()))){
            return false;
        }
        return true;
    }
}
