package com.opinionowl.opinionowl.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * The class used to describe the long answer questions.
 */
@Entity
@Getter
@Setter
public class LongAnswerQuestion extends Question{
    // The character limit for the question.
    private int charLimit;

    /**
     * The default constructor for the class.
     */
    public LongAnswerQuestion(){
        this(null, "", 0);
    }

    /**
     * The constructor for the class.
     * @param prompt the prompt for the question
     * @param charLimit the character limit for the response.
     */
    public LongAnswerQuestion(Survey survey, String prompt, int charLimit){
        super(survey, prompt, QuestionType.LONG_ANSWER);
        this.charLimit = charLimit;
    }

    /**
     * @return the question in string form.
     */
    @Override
    public String toString(){
        return super.toString() + " charLimit:" + charLimit;
    }

    @Override
    public boolean equals(Object object){
        LongAnswerQuestion comparedQuestion = (LongAnswerQuestion) object;
        if(!(this.getId().equals(comparedQuestion.getId()))){
            return false;
        }
        if(!(this.getPrompt().equals(comparedQuestion.getPrompt()))){
            return false;
        }
        if(!(this.getType().equals(comparedQuestion.getType()))){
            return false;
        }
        return true;
    }

}
