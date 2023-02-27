package at.maxkraft.restsec;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    //@Query
    // SELECT username, password FROM UserEntiy where username = {username} and password = {password}
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);

    Optional<UserEntity> findByUsername(String username);

    void deleteByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByUsernameAndPassword(String username, String password);


}
