package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@AllArgsConstructor
@Component
public class InitialDataGenerator implements CommandLineRunner {

    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        userRepository.save(new UserEntity(null, "admin", "admin", true, List.of(), List.of()));
    }
}
