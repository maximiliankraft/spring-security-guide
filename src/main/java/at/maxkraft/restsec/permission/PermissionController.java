package at.maxkraft.restsec.permission;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
@AllArgsConstructor
public class PermissionController {

    PermissionService permissionService;

    // grant certain permission to user
    @PutMapping("/grant")
    void grant(
            @RequestBody PermissionEntity permissionEntity,
            Authentication authentication,
            HttpServletResponse response){

        boolean result;

        if (permissionEntity.getObjectId() != null){
            result = permissionService.grantIdBasedPermission(
                    authentication.getName(),
                    permissionEntity.getPrincipal(),
                    permissionEntity.getObjectId(),
                    permissionEntity.getTargetTypeName(),
                    permissionEntity.getPermName());
        }else {
            result = permissionService.grantGeneralPermission(
                    authentication.getName(),
                    permissionEntity.getPrincipal(),
                    permissionEntity.getTargetTypeName(),
                    permissionEntity.getPermName());
        }

        if(result) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    // revoke certain permission from user
    @DeleteMapping("/revoke")
    void revoke(
            @RequestBody PermissionEntity permissionEntity,
            Authentication authentication,
            HttpServletResponse response){

        boolean result;

        if (permissionEntity.getObjectId() != null){
            result = permissionService.revokeIdBasedPermission(
                    authentication.getName(),
                    permissionEntity.getPrincipal(),
                    permissionEntity.getObjectId(),
                    permissionEntity.getTargetTypeName()
            );
        }else {
            result = permissionService.revokeGeneralPermission(
                    authentication.getName(),
                    permissionEntity.getPrincipal(),
                    permissionEntity.getTargetTypeName()
            );
        }

        if (result){
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
