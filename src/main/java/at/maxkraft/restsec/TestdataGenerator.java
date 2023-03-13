package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
@AllArgsConstructor
public class TestdataGenerator implements CommandLineRunner {

    UserRepository userRepository;

    AuthorityRepository authorityRepository;


    @Override
    public void run(String... args) throws Exception {

        var users = StreamSupport.stream(userRepository.findAll().spliterator(), true).toList();
        var authorities = StreamSupport.stream(authorityRepository.findAll().spliterator(), true).toList();

        AuthorityEntity read = new AuthorityEntity(0L, "read");
        AuthorityEntity write = new AuthorityEntity(1L, "write");
        AuthorityEntity delete = new AuthorityEntity(2L, "delete");
        AuthorityEntity grant = new AuthorityEntity(3L, "grant");
        AuthorityEntity owner = new AuthorityEntity(4L, "owner");

        AuthorityEntity adminAuthority = new AuthorityEntity(5L, "admin");

        authorityRepository.saveAll(
                List.of(read, write, delete, grant, owner,adminAuthority)
        );

        var admin = new UserEntity(
                0L,
                "admin",
                "admin",
                true,
                false,
                false,
                List.of(write, read, adminAuthority));

        userRepository.save(admin);

    }
}
