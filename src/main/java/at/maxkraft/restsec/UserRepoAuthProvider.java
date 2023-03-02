package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class UserRepoAuthProvider implements AuthenticationProvider {

    UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        var userOption = userRepository.findByUsernameAndPassword(username, password);

        if (userOption.isPresent()){
            // credentials valid
            return new UsernamePasswordAuthenticationToken(username, password, List.of());

        }else {
            // credentials invalid
            throw new BadCredentialsException(username);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // ensure passed auth object is of type UsernamePasswordAuthenticationToken
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
