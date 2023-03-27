package at.maxkraft.restsec;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PermissionRepository extends CrudRepository<Permission, Long> {

    Optional<Permission> findByClassNameAndUserAndObjectId(
            String className,
            UserEntity user,
            Long objectId
    );

}
