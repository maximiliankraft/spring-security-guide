package at.spengergasse.springsecurity.configuration;


import at.spengergasse.springsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@AllArgsConstructor
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider, Serializable {

    UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // get user from db
        var result = userRepository.findByUsernameAndPassword(
                authentication.getName(),
                (String) authentication.getCredentials()
        );

        if (result.isPresent()){

            // assign authorities from db to user, implicity sets autehticated to true
            var authorizedToken =  new UsernamePasswordAuthenticationToken(
                    authentication.getName(),
                    authentication.getCredentials(),
                    result.get().getAuthorities()
            );

            return authorizedToken;

        }else {
            authentication.setAuthenticated(false);
            return authentication;
        }

    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
