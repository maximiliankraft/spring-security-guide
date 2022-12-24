package at.maxkraft.restsec.configuration;

import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.NotYetImplementedFor6Exception;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

@AllArgsConstructor
public class JPAUserDetailsManager implements UserDetailsManager {



    UserRepository userRepository;

    // Optional<UserDetails> currentUser = Optional.empty();

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
        throw new NotYetImplementedFor6Exception("changePassword");
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
