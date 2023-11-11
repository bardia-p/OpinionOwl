package com.opinionowl.opinionowl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class User for defining a User entity that uses the Survey website.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // Create an auto generated unique Id
    private Long id;

    // Define a username for the user
    private String username;

    // Define a password for the associated username
    private String password;

    // Define a list of surveys that a User has created
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Survey> listSurveys;

    /**
     * Default constructor for User.
     * @param username A string username.
     * @param password A string password.
     */
    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
        this.listSurveys = new ArrayList<>();
    }

    /**
     * Void method for adding a survey to the list of surveys.
     * @param survey A Survey survey.
     */
    public void addSurvey (Survey survey){
        this.listSurveys.add(survey);

    }

    /**
     * Boolean method for removing a survey from the list of surveys.
     * @param surveyId A Long survey ID.
     * @return True if the survey was removed, false otherwise.
     */
    public boolean removeSurvey (Long surveyId) {
        return this.listSurveys.removeIf(s -> s.getId().equals(surveyId));
    }

    /**
     * @return A user in String format.
     */
    @Override
    public String toString(){
        String user = "User ID: " + id + "Username: " + username + "Password: " + password;
        user += "\n-----Surveys -----";
        for (Survey s: listSurveys) {
            user += "\n" + s.toString();

        }
        return user;
    }

    public boolean equals(AppUser comparedUser){
        if(!(this.username.equals(comparedUser.getUsername()))){
            return false;
        }
        if(!(this.id.equals(comparedUser.getId()))){
            return false;
        }
        if(!(this.password.equals(comparedUser.getPassword()))){
            return false;
        }
        return this.listSurveys.equals(comparedUser.getListSurveys());
    }
}

