package at.spengergasse.springsecurity.permission;

import at.spengergasse.springsecurity.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PermissionRepository extends CrudRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByPrincipalAndObjectIdAndTargetTypeName(UserEntity principal, Long objectId, String targetTypeName);

    Optional<PermissionEntity> findByObjectIdAndPrincipalUsernameAndTargetTypeName(Long objectOd, String username, String targetType);

    Boolean deleteByPrincipalAndObjectIdAndTargetTypeName(UserEntity principal, Long objectId, String targetTypeName);
}
