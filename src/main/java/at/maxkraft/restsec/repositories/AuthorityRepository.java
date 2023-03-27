package at.maxkraft.restsec.repositories;

import at.maxkraft.restsec.entities.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {
    AuthorityEntity findByAuthority(String authority);
}
