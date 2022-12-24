package at.maxkraft.restsec.entity;

import at.maxkraft.restsec.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;

import java.util.Set;

public class UserEntityInitializer implements CommandLineRunner {

    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        userRepository.save(
                UserEntity.builder()
                        .username("admin")
                        .password("admin")
                        .authorities(
                                Set.of(GrantedAuthorityEntity.of("admin"))
                        ).build());

        userRepository.save(
                UserEntity.builder()
                        .username("max")
                        .password("123456")
                        .authorities(
                                Set.of(GrantedAuthorityEntity.of("read"))
                        ).build());
    }
}
