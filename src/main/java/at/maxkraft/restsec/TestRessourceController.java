package at.maxkraft.restsec;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestRessourceController {

    TestRessourceRepository testRessourceRepository;
    UserRepository userRepository;
    PermissionChecker permissionChecker;
    PermissionRepository permissionRepository;


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
    Optional<TestRessource> findTestId(@PathVariable Long id, Authentication auth, HttpServletResponse response) {

        // todo check if user is allowed to read ressource
        var permissions = StreamSupport.stream(permissionRepository.findAll().spliterator(), false).toArray();

        // requesting username, className: "TestResource", requested id: id, requested action: "read"
        boolean isAllowed = permissionChecker.checkPermission(auth.getName(), "TestResource", id, PermissionLevel.READ);

        if (!isAllowed){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Optional.empty();
        }

        Optional<TestRessource> result = testRessourceRepository.findById(id);
        if (result.isEmpty()) {
            response.setStatus(404); // Not found
        }

        return result;
    }

    @PutMapping("/")
    TestRessource addTestResource(Authentication auth, @RequestBody TestRessource testRessource){

        var savedResource = testRessourceRepository.save(testRessource);

        var user = userRepository.findByUsername((String)auth.getName()).get();

        Permission testResourcePermission = new Permission(
                null,
                user,
                "TestResource",
                savedResource.getId(),
                PermissionLevel.OWNER
        );

        permissionRepository.save(testResourcePermission);

        return savedResource;
    }

}
