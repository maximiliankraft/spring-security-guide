package at.maxkraft.restsec.controller;


import at.maxkraft.restsec.configuration.JPAUserDetailsManager;
import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.exception.ActionForbiddenException;
import at.maxkraft.restsec.repository.UserRepository;
import at.maxkraft.restsec.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    JPAUserDetailsManager jpaUserDetailsManager;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    TokenService tokenService;

    @GetMapping("/")
    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    @PreAuthorize("@tokenService.verifyAuthentication(authentication, @userRepository.findByUsername(authentication.getName()).get())")
    @GetMapping("/name")
    public String home(Principal principal) {
        return principal.getName();
    }

    // login user and return a token

    @PreAuthorize("@tokenService.verifyAuthentication(authentication, @userRepository.findByUsername(authentication.getName()).get())")
    @GetMapping("/login")
    public String login(Authentication authentication) {


        LOG.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.debug("Token granted: {}", token);
        return token;
    }

    // allows access unauthenticated via SecurityConfig
    @PutMapping("/register/{username}/{password}")
    public UserDetails register(@PathVariable String username, @PathVariable String password, HttpServletResponse response) {
        LOG.debug("Registration requested for user: '{}'", username);

        response.setStatus(HttpServletResponse.SC_CREATED);
        return userRepository.save(UserEntity.builder()
                .username(username)
                .isUser(true)
                .password(password)
                .build());
    }

    @PreAuthorize("@tokenService.verifyAuthentication(authentication, @userRepository.findByUsername(authentication.getName()).get())")
    @PostMapping("/changePassword/{oldPassword}/{newPassword}")
    public void changePassword(@PathVariable String oldPassword, @PathVariable String newPassword, Authentication authentication){
        // will get current user via SecurityContextHolder
        Authentication repoAuth = new UsernamePasswordAuthenticationToken(
                userRepository.findByUsername(authentication.getName()).get(), null, List.of());

        SecurityContextHolder.getContext().setAuthentication(repoAuth);

        // will throw a 403 exception if not allowed
        jpaUserDetailsManager.changePassword(oldPassword, newPassword);
    }


    @PreAuthorize("@tokenService.verifyAuthentication(authentication, @userRepository.findByUsername(authentication.getName()).get())")
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
    @PreAuthorize("@tokenService.verifyAuthentication(authentication, @userRepository.findByUsername(authentication.getName()).get())")
    @GetMapping("/logout")
    public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response){
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

}
