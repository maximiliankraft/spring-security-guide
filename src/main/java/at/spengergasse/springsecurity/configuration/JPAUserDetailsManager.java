package at.spengergasse.springsecurity.configuration;

import at.spengergasse.springsecurity.entity.UserEntity;
import at.spengergasse.springsecurity.exception.ActionForbiddenException;
import at.spengergasse.springsecurity.exception.ResourceNotFoundException;
import at.spengergasse.springsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

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
