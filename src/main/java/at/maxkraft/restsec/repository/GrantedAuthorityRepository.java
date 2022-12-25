package at.maxkraft.restsec.repository;

import at.maxkraft.restsec.entity.GrantedAuthorityEntity;
import org.springframework.data.repository.CrudRepository;

public interface GrantedAuthorityRepository extends CrudRepository<GrantedAuthorityEntity, String> {
}
