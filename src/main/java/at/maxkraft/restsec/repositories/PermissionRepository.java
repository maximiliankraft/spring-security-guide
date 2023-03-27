package at.maxkraft.restsec.repositories;

import at.maxkraft.restsec.entities.Permission;
import at.maxkraft.restsec.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PermissionRepository extends CrudRepository<Permission, Long> {

    Optional<Permission> findByClassNameAndUserAndObjectId(
            String className,
            UserEntity user,
            Long objectId
    );

}
