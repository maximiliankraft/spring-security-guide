package at.maxkraft.restsec.permission;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class GranularPermissionEvaluator implements PermissionEvaluator {

    PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        var objName = targetDomainObject.getClass().getName();
        var permName = (PermissionType) permission;

        var username = authentication.getName();
        return permissionService.checkGeneralPermission(username, objName, permName);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        var username = authentication.getName();
        Long objectId = (Long) targetId;
        var permName = (PermissionType) permission;

        return permissionService.checkIdBasedPermission(username, objectId, targetType, permName);
    }
}