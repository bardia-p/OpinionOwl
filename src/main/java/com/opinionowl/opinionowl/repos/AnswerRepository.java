package com.opinionowl.opinionowl.repos;

import com.opinionowl.opinionowl.models.Answer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository in charge of managing the CRUD operations for Answer Entity.
 */
public interface AnswerRepository extends CrudRepository<Answer, Long> {
    // Essentially performs SELECT * FROM answer
    List<Answer> findAll();
}
