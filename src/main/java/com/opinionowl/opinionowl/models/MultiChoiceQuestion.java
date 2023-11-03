package com.opinionowl.opinionowl.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * The class used to describe the multiple choice questions.
 */
@Entity
@Getter
@Setter
public class MultiChoiceQuestion extends Question {
    // The choices for the question.
    private String[] choices;

    /**
     * The default constructor for the class.
     */
    public MultiChoiceQuestion() {
        this("", new String[0]);
    }

    /**
     * The constructor for the class.
     * @param prompt the prompt for the question.
     * @param choices the choices for the question.
     */
    public MultiChoiceQuestion(String prompt, String[] choices){
        super(prompt, QuestionType.MULTIPLE_CHOICE);
        this.choices = choices;
    }

    /**
     * @return the question in string form.
     */
    @Override
    public String toString(){
        String res = super.toString() + " choices:[";
        if (choices.length > 0) {
            for (int i = 0; i < choices.length - 1; i++){
                res += choices[i] + ", ";
            }
            res += choices[choices.length - 1];
        }
        res += "]";
        return res;
    }
}
