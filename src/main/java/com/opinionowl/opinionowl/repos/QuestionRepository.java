package com.opinionowl.opinionowl.repos;

import com.opinionowl.opinionowl.models.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository in charge of managing the CRUD operations for Question Entity.
 */
public interface QuestionRepository extends CrudRepository<Question, Long> {
    // Essentially performs SELECT * FROM question
    List<Question> findAll();
}
