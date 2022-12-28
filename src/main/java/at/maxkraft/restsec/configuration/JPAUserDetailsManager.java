package at.maxkraft.restsec.configuration;

import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.hibernate.NotYetImplementedFor6Exception;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component
public class JPAUserDetailsManager implements UserDetailsManager {

    UserRepository userRepository;
    SecurityContextHolderStrategy securityContextHolderStrategy;

    @Override
    public void createUser(UserDetails user) {

        if (user instanceof UserEntity){
            userRepository.save((UserEntity)user);
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        createUser(user);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

        // based on implementation of `InMemoryUserDetailsManager`
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();

        var user = userRepository.findByUsername(currentUser.getName());

        if (user.isPresent()){
            var currentPassword = user.get().getPassword();
            if (Objects.equals(new BCryptPasswordEncoder().encode(oldPassword), currentPassword)){
                user.get().setPassword(newPassword); // setter `setPassword` also encrypts the password
                userRepository.save(user.get());
            }
        }
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.countByUsername(username) > 0;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = userRepository.findByUsername(username);

        if (user.isPresent()){
            return user.get();
        }else {
            throw new UsernameNotFoundException(username);
        }
    }
}
