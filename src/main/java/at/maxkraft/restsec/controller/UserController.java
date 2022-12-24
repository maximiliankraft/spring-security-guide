package at.maxkraft.restsec.controller;


import at.maxkraft.restsec.configuration.JPAUserDetailsManager;
import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.repository.UserRepository;
import at.maxkraft.restsec.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    JPAUserDetailsManager jpaUserDetailsManager;

    UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    TokenService tokenService;

    @GetMapping("/")
    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    @GetMapping("/name")
    public String home(Principal principal) {
        return principal.getName();
    }

    // login user and return a token
    @GetMapping("/login")
    public String login(Authentication authentication) {
        LOG.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.debug("Token granted: {}", token);
        return token;
    }

    // allows access unauthenticated via SecurityConfig
    @GetMapping("/register/{username}/{password}")
    public UserDetails register(@PathVariable String username, @PathVariable String password) {
        LOG.debug("Registration requested for user: '{}'", username);

        return userRepository.save(UserEntity.builder().username(username).password(password).build() );
    }

    @GetMapping("/delete")
    public UserDetails removeAccount(Authentication authentication, HttpServletResponse response){

        var userEntity = userRepository.findByUsername(authentication.getName());

        if (userEntity.isPresent()){
            userRepository.deleteByUsername(authentication.getName());
            return userEntity.get();
        }else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    // https://stackoverflow.com/a/46889039/17996814
    @GetMapping("/logout")
    public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response){
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

}
