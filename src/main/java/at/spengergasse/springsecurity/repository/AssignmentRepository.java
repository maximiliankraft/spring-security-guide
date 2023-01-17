package at.spengergasse.springsecurity.repository;

import at.spengergasse.springsecurity.entity.Assignment;
import org.springframework.data.repository.CrudRepository;

public interface AssignmentRepository extends CrudRepository<Assignment, Long> {
    Iterable<Assignment> findAllByOwnerUsername(String username);

}
