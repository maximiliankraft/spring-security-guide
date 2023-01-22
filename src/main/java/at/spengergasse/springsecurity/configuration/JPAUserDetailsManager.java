package at.spengergasse.springsecurity.configuration;

import at.spengergasse.springsecurity.entity.UserEntity;
import at.spengergasse.springsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component
public class JPAUserDetailsManager implements UserDetailsManager {

    UserRepository userRepository;
    final SecurityContextHolderStrategy securityContextHolderStrategy = new CustomSecurityHolderStrategy();


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
