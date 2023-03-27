package at.maxkraft.restsec;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    //JPAUserDetailsManager jpaUserDetailsManager;

    UserRepository userRepository;

    PermissionRepository permissionRepository;

    @GetMapping("/register/{username}/{password}")
    UserEntity register( @PathVariable String username, @PathVariable String password){

        return userRepository.save(
                new UserEntity(null, password, username, true, false, false, List.of())
        );
    }


    @GetMapping("/login/{username}/{password}")
    void login(@PathVariable String username, @PathVariable String password, HttpServletResponse response){

        // login passt
        if(userRepository.findByUsernameAndPassword(username, password).isPresent()){
            response.setStatus(200);
        }else { // login passt nicht
            response.setStatus(401);
        }
    }


    @PostMapping("/name")
    String getUsername(Authentication authentication){
        return authentication.getName();
    }


    @PutMapping("/grant/{grantedUsername}/{permissionType}/{className}/{objectId}")
    Permission grantPermission(Authentication authentication,
                               @PathVariable String grantedUsername,
                               @PathVariable PermissionLevel permissionType,
                               @PathVariable String className,
                               @PathVariable Long objectId
    ){

        var grantedUserOption = userRepository.findByUsername(grantedUsername);
        var granteeUserOption = userRepository.findByUsername( authentication.getName());

        var permissionOption = permissionRepository.findByClassNameAndUserAndObjectId(
                "TestResource",
                granteeUserOption.get(),
                objectId);

        // if grantee is allowed to grant, add new permission
        if(permissionOption.isPresent()
        ){
            //check authorization
            return permissionRepository.save(new Permission(
                    null,
                    grantedUserOption.get(),
                    className,
                    objectId,
                    permissionType)
            );
        }

        return null;
    }


    // /grant
    @GetMapping("/grant/{grantingUsername}/{password}/{grantedUsername}/{permissionType}/{className}/{objectId}")
    Permission grantPermission(
            @PathVariable String grantingUsername,
            @PathVariable String password,
            @PathVariable String grantedUsername,
            @PathVariable PermissionLevel permissionType, // e.g read, write, delete...
            @PathVariable String className,
            @PathVariable Long objectId,
            HttpServletResponse response
    ){

        var grantedUserOption = userRepository.findByUsername(grantedUsername);

        if (grantedUserOption.isPresent()){
            var newPermission = new Permission(null, grantedUserOption.get(), className, objectId, permissionType);

            var grantingUser = userRepository.findByUsernameAndPassword(grantingUsername, password);

            if (grantingUser.isPresent()){ // user exists

                // todo continue here
                //grantingUser.get().getAuthorities()

            } else { // user credentials invalid
                response.setStatus(401);
                return null;
            }
        }else {
            response.setStatus(400);
            return null;
        }

        return null;
    }
}
