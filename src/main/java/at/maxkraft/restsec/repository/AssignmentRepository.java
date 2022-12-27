package at.maxkraft.restsec.repository;

import at.maxkraft.restsec.entity.Assignment;
import org.springframework.data.repository.CrudRepository;

public interface AssignmentRepository extends CrudRepository<Assignment, Long> {
    Iterable<Assignment> findAllByOwnerUsername(String username);

}
