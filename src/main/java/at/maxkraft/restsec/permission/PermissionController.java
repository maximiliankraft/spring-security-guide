package at.maxkraft.restsec.permission;

import at.maxkraft.restsec.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
@AllArgsConstructor
public class PermissionController {

    PermissionService permissionService;

    UserRepository userRepository;

    // todo check if user has grant permission
    // grant certain permission to user
    @PutMapping("/grant")
    void grant(
            @RequestBody PermissionEntity permissionEntity,
            Authentication authentication,
            HttpServletResponse response){

        boolean result;

        if (permissionEntity.getObjectId() != null){
            result = permissionService.grantIdBasedPermission(
                    // narrow down users to it actual db representation, don't allow stub users
                    userRepository.findByUsername(authentication.getName()).get(),
                    userRepository.findByUsername(permissionEntity.getPrincipal().getUsername()).get(),
                    permissionEntity.getObjectId(),
                    permissionEntity.getTargetTypeName(),
                    permissionEntity.getPermName());
        }else {
            result = permissionService.grantGeneralPermission(
                    userRepository.findByUsername(authentication.getName()).get(),
                    userRepository.findByUsername(permissionEntity.getPrincipal().getUsername()).get(),
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
                    userRepository.findByUsername(authentication.getName()).get(),
                    // todo don't allow stub users here either
                    permissionEntity.getPrincipal(),
                    permissionEntity.getObjectId(),
                    permissionEntity.getTargetTypeName()
            );
        }else {
            result = permissionService.revokeGeneralPermission(
                    userRepository.findByUsername(authentication.getName()).get(),
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
