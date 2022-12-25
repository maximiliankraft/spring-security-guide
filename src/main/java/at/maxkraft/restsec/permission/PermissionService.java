package at.maxkraft.restsec.permission;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermissionService {

    PermissionRepository permissionRepository;

    // grant new general permission
    public boolean grantIdBasedPermission(String granter, String grantee, Long objectId, String targetType, PermissionType permName){

        // check if the granter has grant permission on target type, to add a new user (grantee
        if (checkGeneralPermission(granter, targetType, PermissionType.grant)){
            PermissionEntity permission = new PermissionEntity(null, grantee, objectId, targetType, permName);
            permissionRepository.save(permission);
            return true;
        }else {
            return false;
        }
    }

    public boolean revokeIdBasedPermission(String revoker, String permissionOwner, Long objectId, String targetType){
        // grant permission implies revoke permission
        if (checkIdBasedPermission(revoker, objectId, targetType, PermissionType.grant)){
            return permissionRepository.deleteByPrincipalAndObjectIdAndTargetTypeName(permissionOwner, objectId,targetType);
        }

        return false;
    }

    public boolean revokeGeneralPermission(String revoker, String permissionOwner, String targetType){
        return revokeIdBasedPermission(revoker,permissionOwner , PermissionEntity.ALL_OBJECTS, targetType);
    }

    // grant new id based permission
    public boolean grantGeneralPermission(String granter, String grantee, String targetType, PermissionType permName){
        return grantIdBasedPermission(granter, grantee, -1L, targetType, permName);
    }

    // check general permission
    public boolean checkGeneralPermission(String username, String objName, PermissionType permName){
        return checkIdBasedPermission(username, -1L, objName, permName);
    }

    // check id based permission
    public boolean checkIdBasedPermission(String username, Long objectId, String objName, PermissionType permName){

        var permissionEntity = permissionRepository
                .findByPrincipalAndObjectIdAndTargetTypeName(username, objectId, objName);

        // check if permission exists and is greater or equal the requested permission
        return permissionEntity.isPresent() &&
                permissionEntity.get().getPermName().getPowerLevel() >= permName.getPowerLevel();
    }
}
