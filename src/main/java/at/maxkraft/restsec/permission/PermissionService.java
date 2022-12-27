package at.maxkraft.restsec.permission;

import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class PermissionService {

    PermissionRepository permissionRepository;

    UserRepository userRepository;

    // grant new general permission
    public boolean grantIdBasedPermission(UserEntity granter, UserEntity grantee, Long objectId, String targetType, PermissionType permName){

        // check if the granter has grant permission on target type, to add a new user (grantee
        if (checkGeneralPermission(granter, targetType, PermissionType.grant)){

            var permission = PermissionEntity.builder()
                    // reload userobject in case a stub got passed
                    .principal(userRepository.findByUsername(grantee.getUsername()).get())
                    .objectId(objectId)
                    .targetTypeName(targetType)
                    .permName(permName).build();

            permissionRepository.save(permission);
            return true;
        }else {
            return false;
        }
    }

    public boolean revokeIdBasedPermission(UserEntity revoker, UserEntity permissionOwner, Long objectId, String targetType){
        // grant permission implies revoke permission
        if (checkIdBasedPermission(revoker, objectId, targetType, PermissionType.grant)){
            return permissionRepository.deleteByPrincipalAndObjectIdAndTargetTypeName(permissionOwner, objectId,targetType);
        }

        return false;
    }

    public boolean revokeGeneralPermission(UserEntity revoker, UserEntity permissionOwner, String targetType){
        return revokeIdBasedPermission(revoker,permissionOwner , PermissionEntity.ALL_OBJECTS, targetType);
    }

    // grant new id based permission
    public boolean grantGeneralPermission(UserEntity granter, UserEntity grantee, String targetType, PermissionType permName){
        return grantIdBasedPermission(granter, grantee, -1L, targetType, permName);
    }

    // check general permission
    public boolean checkGeneralPermission(UserEntity username, String objName, PermissionType permName){
        return checkIdBasedPermission(username, PermissionEntity.ALL_OBJECTS, objName, permName);
    }


    public boolean checkOwnership(UserEntity user, Long objectId, String targetType){
        var permission = permissionRepository.findByObjectIdAndPrincipalUsernameAndTargetTypeName(objectId, user.getUsername(), targetType);

        return permission.filter(
                permissionEntity ->
                        Objects.equals(
                                permissionEntity.getPrincipal().getUsername(),
                                user.getUsername())
        ).isPresent();
    }

    // check id based permission
    public boolean checkIdBasedPermission(UserEntity username, Long objectId, String objName, PermissionType permName){
        // check if user is owner
        if (checkOwnership(username, objectId,objName)){
            return true;
        }

        // check for a specific id
        var permissionEntity = permissionRepository
                .findByPrincipalAndObjectIdAndTargetTypeName(username, objectId, objName);

        // check if permission exists and is greater or equal the requested permission
        var permissionGranted = permissionEntity.isPresent() &&
                permissionEntity.get().getPermName().getPowerLevel() >= permName.getPowerLevel();


        // if the check was performed for all objects return right away
        if (!permissionGranted && Objects.equals(objectId, PermissionEntity.ALL_OBJECTS)){
            return false;
        }

        if (permissionGranted) {
            return true;
        }

        //  check if user has general permission for all objects of that type
        if (checkGeneralPermission(username, objName, permName)){
            return true;
        }

        // if all fails no authority is granted
        return false;
    }

    public boolean checkIdBasedPermission(UserEntity username, Long objectId, String objName, String permName){
        return checkIdBasedPermission(username, objectId, objName, PermissionType.fromString(permName));
    }

    public boolean checkGeneralPermission(UserEntity username, String objName, String permName){
        return checkGeneralPermission(username,objName, PermissionType.fromString(permName));
    }

    public boolean checkUserAuthority(UserEntity entity, PermissionType type) {
        for (var authority : entity.getAuthorities()) {
            if (PermissionType.fromString(authority.getAuthority()).getPowerLevel() >= type.getPowerLevel()){
                return true;
            }
        }

        return false;
    }
}
