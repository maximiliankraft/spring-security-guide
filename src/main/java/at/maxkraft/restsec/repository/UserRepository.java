package at.maxkraft.restsec.repository;

import at.maxkraft.restsec.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    void deleteByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    int countByUsername(String username);

    List<UserEntity> findAll();

    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
