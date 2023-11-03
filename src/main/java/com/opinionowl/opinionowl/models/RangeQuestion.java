package com.opinionowl.opinionowl.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * The class used to describe the range questions.
 */
@Entity
@Getter
@Setter
public class RangeQuestion extends Question {
    // The lower value for the range.
    float lower;

    // the upper value for the range.
    float upper;

    // the increment the range.
    float increment;

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
    public RangeQuestion(String prompt, float lower, float upper){
        this(prompt, lower, upper, 1);
    }

    /**
     * The constructor for the class with increment.
     * @param prompt the prompt for the question.
     * @param lower the lower value for the range.
     * @param upper the upper value for the range.
     * @param increment the increment value for the range.
     */
    public RangeQuestion(String prompt, float lower, float upper, float increment){
        super(prompt, QuestionType.RANGE);
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
}
