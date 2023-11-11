package com.opinionowl.opinionowl.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * The class used to describe the range questions.
 */
@Entity
@Getter
@Setter
public class RangeQuestion extends Question {
    // The lower value for the range.
    private int lower;

    // the upper value for the range.
    private int upper;

    // the increment the range.
    private int increment;

    /**
     * The default constructor for the class.
     */
    public RangeQuestion(){
        this("", 0, 0);
    }

    /**
     * The constructor for the class without increment.
     * @param prompt the prompt for the question.
     * @param lower the lower value for the range.
     * @param upper the upper value for the range.
     */
    public RangeQuestion(String prompt, int lower, int upper){
        this(null, prompt, lower, upper, 1);
    }

    /**
     * The constructor for the class with increment.
     * @param prompt the prompt for the question.
     * @param lower the lower value for the range.
     * @param upper the upper value for the range.
     * @param increment the increment value for the range.
     */
    public RangeQuestion(Survey survey, String prompt, int lower, int upper, int increment){
        super(survey, prompt, QuestionType.RANGE);
        this.lower = lower;
        this.upper = upper;
        this.increment = increment;
    }

    /**
     * @return the question in string form.
     */
    @Override
    public String toString(){
        return super.toString() + " lower:" + lower + " upper:" + upper + " increment:" + increment;
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
        RangeQuestion that = (RangeQuestion) o;
        return this.getId() == that.getId();
    }

}
