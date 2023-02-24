package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

// This annotation marks the class as a component that should be automatically detected and registered as a bean
@Component
// This annotation generates a constructor that takes all non-static fields as arguments
@AllArgsConstructor
public class UserRepoAuthenticationProvider implements AuthenticationProvider {

    // This field is injected automatically by Spring, because it matches a bean that is defined elsewhere
    UserRepository userRepository;

    // This method is called by Spring Security to authenticate a user based on the provided authentication token
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // Extract the username and password from the authentication token
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Query the user repository to find a user with the matching username and password
        var dbResult = userRepository.findByUsernameAndPassword(name, password);

        // If a matching user is found, create a new authentication token for the user with their username, password, and authorities
        if (dbResult.isPresent()){
            return new UsernamePasswordAuthenticationToken(
                    name, password, dbResult.get().getAuthorities());
        }else {
            // If no matching user is found, throw a SessionAuthenticationException
            throw new SessionAuthenticationException(name);
        }
    }

    // This method returns true if the provided authentication token is supported by this authentication provider
    @Override
    public boolean supports(Class<?> authentication) {

        // Check if the authentication token is of type UsernamePasswordAuthenticationToken
        var isSupported = authentication.equals(UsernamePasswordAuthenticationToken.class);
        return isSupported;
    }
}