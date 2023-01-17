package at.spengergasse.springsecurity.repository;

import at.spengergasse.springsecurity.entity.GrantedAuthorityEntity;
import org.springframework.data.repository.CrudRepository;

public interface GrantedAuthorityRepository extends CrudRepository<GrantedAuthorityEntity, String> {
}
