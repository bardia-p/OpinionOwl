package com.opinionowl.opinionowl.repos;

import com.opinionowl.opinionowl.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository in charge of managing the CRUD operations for the User Entity.
 */
public interface UserRepository extends CrudRepository<User, Long>{
    List<User> findAll();
}
