package at.maxkraft.restsec;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    // JPAUserDetailsManager jpaUserDetailsManager;

    UserRepository userRepository;

    boolean isUserCredentialsValid(Authentication auth){

        String givenUsername = (String)auth.getPrincipal();
        String givenPassword = (String)auth.getCredentials();

        return isUserCredentialsValid(givenUsername, givenPassword);
    }

    boolean isUserCredentialsValid(String username, String password){
        Optional<UserEntity> optionalUser = userRepository.findByUsernameAndPassword(username, password);

        if(optionalUser.isPresent()){
            // valid credentials
            return true;
        }else{
            // invalid credentials
            return false;
        }
    }

    
    @GetMapping("/login")
    void checkUserCredentials(Authentication auth, HttpServletResponse response, HttpServletRequest request){

        var authHeader = request.getHeader("Authorization").split(" ");
        var username = authHeader[0];
        var password = authHeader[1];

        if (isUserCredentialsValid(username, password)) {
            response.setStatus(200);
        }else{
            response.setStatus(403);
        }
    }

    @GetMapping("/login/{username}/{password}")
    void checkUserCredentials(@PathVariable String username, @PathVariable String password, HttpServletResponse response){

        if (isUserCredentialsValid(username, password)) {
            response.setStatus(200);
        }else{
            response.setStatus(403);
        }
    }


    @GetMapping("/register/{username}/{password}")
    UserEntity registerUser(@PathVariable String username, @PathVariable String password){
        UserEntity newUser = new UserEntity(null, username, password, true);

        return userRepository.save(newUser);
    }

    @PutMapping("/register")
    UserEntity registerUser(@RequestBody UserEntity user){
        return userRepository.save(user);
    }

    @DeleteMapping("/")
    void deleteUser(Authentication auth, HttpServletResponse response){

        if(isUserCredentialsValid(auth)){
            // valid credentials
            userRepository.delete(userRepository.findByUsername((String)auth.getPrincipal()).get());
            response.setStatus(200); // OK
        }else{
            // invalid credentials
            response.setStatus(401); // 401 unauthorized, 403 forbidden
        }

    }

    @PatchMapping("/changePassword")
    UserEntity changePassword(@RequestBody UserEntity userWithNewPw, Authentication auth, HttpServletResponse response){

        if(isUserCredentialsValid(auth) && ((String)auth.getPrincipal()).equals(userWithNewPw.getUsername()) ){
            return userRepository.save(userWithNewPw);
        }

        response.setStatus(401);
        return null;

    }


}
