package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

@AllArgsConstructor
@Component
@Log
public class JPAUserDetailsManager implements UserDetailsManager {

    UserRepository userRepository;


    public boolean isAuthenticationValid(String username, String password){

        return userRepository.existsByUsernameAndPassword(username, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var userOption = userRepository.findByUsername(username);

        if (username == null || userOption.isEmpty()){
            throw new UsernameNotFoundException(username);
        }else {
            return userOption.get();
        }
    }

    @Override
    public void createUser(UserDetails user) {

        if (user instanceof UserEntity){
            userRepository.save((UserEntity) user);
        }else {
            log.warning("could not save user");
        }


    }

    @Override
    public void updateUser(UserDetails user) {

        // same as instanceof
        if (user.getClass().equals(UserEntity.class)){
            userRepository.save((UserEntity) user);
        }else {
            log.warning("could not update user");
        }
    }

    @Override
    public void deleteUser(String username) {

        try{
            this.loadUserByUsername(username);
            userRepository.deleteByUsername(username);
        }catch (UsernameNotFoundException e){
            log.severe(e.getMessage());
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub

        // todo utilze context holder
        // SecurityContextHolder holder = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
    }

    @Override
    public boolean userExists(String username) {

        return userRepository.existsByUsername(username);
    }
}
