package at.maxkraft.restsec.configuration;

import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.exception.ActionForbiddenException;
import at.maxkraft.restsec.exception.ResourceNotFoundException;
import at.maxkraft.restsec.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.hibernate.NotYetImplementedFor6Exception;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
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
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Component
public class JPAUserDetailsManager implements UserDetailsManager {

    UserRepository userRepository;



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
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        var user = userRepository.findByUsername(currentUser.getName());

        if (user.isPresent()){
            var currentEncodedPassword = user.get().getPassword();

            if (UserEntity.ENCODER.matches(oldPassword, currentEncodedPassword)){
                user.get().setPassword(newPassword); // setter `setPassword` also encrypts the password
                userRepository.save(user.get());
            }else {
                throw new ActionForbiddenException();
            }
        }else {
            throw new ResourceNotFoundException();
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
