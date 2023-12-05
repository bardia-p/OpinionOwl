package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Response class in charge holding the survey response.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Response {
    // The id of the response.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // The survey object used for the response.
    @ManyToOne
    private Survey survey;

    // The list of the questions for the response.
    @OneToMany(mappedBy = "response", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    /**
     * The constructor for the Response class.
     */
    public Response(Survey survey) {
        this.survey = survey;
        this.answers = new ArrayList<>();
    }

    /**
     * Adds an answer to the survey.
     * @param question the question id.
     * @param content the content of the reply.
     */
    public void addAnswer(Long question, String content){
        this.answers.add(new Answer(this, question, content));
    }

    /**
     * @return the Response class in string form.
     */
    @Override
    public String toString(){
        return "Response #" + id + " answers:" + answers;
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
        Response response = (Response) o;
        return Objects.equals(id, response.id) && Objects.equals(answers, response.answers);
    }

}
