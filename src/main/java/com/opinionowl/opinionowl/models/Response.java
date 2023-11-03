package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
    @GeneratedValue
    private Long id;

    // The hashmap for the answers of the response.
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="question")
    @Column(name="answer")
    @CollectionTable(name="response_answers", joinColumns=@JoinColumn(name="response_id"))
    private Map<Long, String> answers;

    /**
     * The constructor for the Response class.
     * @param answers the answers in the Response.
     */
    public Response(HashMap<Long, String> answers){
        this.answers = answers;
    }

    /**
     * @return the Response class in string form.
     */
    @Override
    public String toString(){
        return "Response #" + id + " answers:" + answers;
    }
}
