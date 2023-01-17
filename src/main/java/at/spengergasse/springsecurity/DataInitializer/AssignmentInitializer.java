package at.spengergasse.springsecurity.DataInitializer;

import at.spengergasse.springsecurity.entity.Assignment;
import at.spengergasse.springsecurity.repository.AssignmentRepository;
import at.spengergasse.springsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(50)
@Component
@AllArgsConstructor
@Log // injects logger into class
public class AssignmentInitializer implements CommandLineRunner {

    AssignmentRepository assignmentRepository;
    UserRepository userRepository;

    public void run(String... args){

        var admin = userRepository.findByUsername("admin");
        var max = userRepository.findByUsername("max");

        if (admin.isPresent()){
            Assignment a1 = new Assignment(1L, "Admins assignment", "Test", admin.get());
            assignmentRepository.save(a1);
        }

        if (max.isPresent()){
            Assignment m2 = Assignment.builder()
                    .id(2L)
                    .title("Max assignment")
                    .description("test")
                    .owner(max.get())
                    .build();

            assignmentRepository.save(m2);
        }
    }
}
