package at.maxkraft.restsec.permission;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PermissionRepository extends CrudRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByPrincipalAndObjectIdAndTargetTypeName(String principal, Long objectId, String targetTypeName);

    Boolean deleteByPrincipalAndObjectIdAndTargetTypeName(String principal, Long objectId, String targetTypeName);
}
