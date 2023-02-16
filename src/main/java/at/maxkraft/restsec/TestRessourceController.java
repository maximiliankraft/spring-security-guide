package at.maxkraft.restsec;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestRessourceController {

    TestRessourceRepository testRessourceRepository;

    UserRepository userRepository;


    @GetMapping("/")
    Iterable<TestRessource> allData(){
        return testRessourceRepository.findAll();
    }

    boolean isAuthValid(String authHeader){
        if (authHeader == null){
            return false;
        }

        // response: "admin admin"
        // format: "<username> <password>"

        String[] authDetails = authHeader.split(" ");

        if (authDetails.length != 2){
            return false;
        }

        Optional<UserEntity> givenUser = userRepository.findByUsernameAndPassword(authDetails[0], authDetails[1]);

        if (givenUser.isEmpty()){
            // user not found, return false
            return false;
        }else {
            // user exits, credentials match
            return true;
        }

    }

    @GetMapping("/{id}")
    Optional<TestRessource> findTestId(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response){

        String authHeader = request.getHeader("Authorization");

        boolean authValid = isAuthValid(authHeader);

        if (authValid){
            Optional<TestRessource> result = testRessourceRepository.findById(id);
            if (result.isEmpty()){
                response.setStatus(404); // Not found
            }

            return result;

        }else {
            response.setStatus(401); // Unauthorized
            return Optional.empty();
        }

    }



}
