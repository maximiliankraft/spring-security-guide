package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TestdataGenerator implements CommandLineRunner {

    UserRepository userRepository;

    AuthorityRepository authorityRepository;


    @Override
    public void run(String... args) throws Exception {


        AuthorityEntity read = new AuthorityEntity(null, "read");
        AuthorityEntity write = new AuthorityEntity(null, "write");
        AuthorityEntity delete = new AuthorityEntity(null, "delete");
        AuthorityEntity grant = new AuthorityEntity(null, "grant");
        AuthorityEntity owner = new AuthorityEntity(null, "owner");

        AuthorityEntity adminAuthority = new AuthorityEntity(null, "admin");

        authorityRepository.saveAll(
                List.of(read, write, delete, grant, owner,adminAuthority)
        );

        var admin = new UserEntity(
                null,
                "admin",
                "admin",
                true,
                false,
                false,
                List.of(write, read, adminAuthority));

        userRepository.save(admin);

    }
}
