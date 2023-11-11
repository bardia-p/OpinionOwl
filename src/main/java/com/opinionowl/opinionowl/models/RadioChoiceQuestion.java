package com.opinionowl.opinionowl.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * The class used to describe the multiple choice questions.
 */
@Entity
@Getter
@Setter
public class RadioChoiceQuestion extends Question {
    // The choices for the question.
    private String[] choices;

    /**
     * The default constructor for the class.
     */
    public RadioChoiceQuestion(){
        this(null, "", new String[0]);
    }

    /**
     * The constructor for the class.
     * @param prompt the prompt for the question.
     * @param choices the choices for the question.
     */
    public RadioChoiceQuestion(Survey survey, String prompt, String[] choices){
        super(survey, prompt, QuestionType.RADIO_CHOICE);
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

    /**
     * @param o object that is being compared with
     * @return boolean value saying whether objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RadioChoiceQuestion that = (RadioChoiceQuestion) o;
        return Objects.equals(this.getId(), that.getId());
    }

}
