package com.opinionowl.opinionowl.repos;

import com.opinionowl.opinionowl.models.Survey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository in charge of managing the CRUD operations for Survey Entity.
 */
public interface SurveyRepository extends CrudRepository<Survey, Long> {
    // Essentially performs SELECT * FROM survey
    List<Survey> findAll();
}
