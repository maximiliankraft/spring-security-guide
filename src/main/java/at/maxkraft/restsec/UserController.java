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


    // /grant

    @PutMapping("/grant")
    Optional<Permission> grantPermission(@RequestBody Permission permission, HttpServletRequest request){



        return Optional.of(permissionRepository.save(permission)) ;
    }


}
