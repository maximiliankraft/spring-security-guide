package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    JPAUserDetailsManager jpaUserDetailsManager;

    @Override
    public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
 
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // check if user is valid
        if (jpaUserDetailsManager.isAuthenticationValid(name, password)) {

            return new UsernamePasswordAuthenticationToken(
              name, password, List.of() );
        } else {
            throw new BadCredentialsException(name);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}