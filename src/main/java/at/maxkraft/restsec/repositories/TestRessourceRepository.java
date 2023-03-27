package at.maxkraft.restsec.repositories;

import at.maxkraft.restsec.entities.TestRessource;
import org.springframework.data.repository.CrudRepository;

public interface TestRessourceRepository extends CrudRepository<TestRessource, Long> {
}
