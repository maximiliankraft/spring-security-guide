package at.maxkraft.restsec;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    void deleteByUsername(String username);
    Optional<UserDetails> findByUsername(String username);

    int countByUsername(String username);

    List<UserEntity> findAll();

    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
