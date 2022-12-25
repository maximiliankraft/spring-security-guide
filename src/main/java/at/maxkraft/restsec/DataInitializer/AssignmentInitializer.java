package at.maxkraft.restsec.DataInitializer;

import at.maxkraft.restsec.entity.Assignment;
import at.maxkraft.restsec.repository.AssignmentRepository;
import at.maxkraft.restsec.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(10)
@Component
@AllArgsConstructor
@Log // injects logger into class
public class AssignmentInitializer implements CommandLineRunner {

    AssignmentRepository assignmentRepository;
    UserRepository userRepository;

    public void run(String... args){

        var testUser = userRepository.findByUsername("admin");

        if (testUser.isPresent()){
            Assignment a1 = new Assignment(null, "Test", "Test", testUser.get());
            assignmentRepository.save(a1);
        }
    }
}
