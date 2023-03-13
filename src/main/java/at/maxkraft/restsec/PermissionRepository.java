package at.maxkraft.restsec;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PermissionRepository extends CrudRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByUserUsernameAndDocumentId(String username, Long documentId);
}
