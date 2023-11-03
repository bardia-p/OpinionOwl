package com.opinionowl.opinionowl.repos;

import com.opinionowl.opinionowl.models.Response;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository in charge of managing the CRUD operations for Response Entity.
 */
public interface ResponseRepository extends CrudRepository<Response, Long> {
    // Essentially performs SELECT * FROM response
    List<Response> findAll();
}
