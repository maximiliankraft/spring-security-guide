package at.maxkraft.restsec;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/docs")
@AllArgsConstructor
public class DocumentController {

    DocumentRepository documentRepository;

    UserRepository userRepository;

    PermissionRepository permissionRepository;

    UserEntity findByAuthentication(Authentication auth){
        return userRepository.findByUsername(auth.getName()).get() ;
    }

    // grant document
    // todo continue here...


    // get Document
    @GetMapping("/{id}")
    DocumentEntity getDocument(@PathVariable Long id, Authentication authentication, HttpServletResponse response){

        var permissionOption = permissionRepository.findByUserUsernameAndDocumentId(authentication.getName(), id);

        if (    permissionOption.isPresent() && // check if permission exists
                permissionOption.get().getAuth().compareTo(PermissionLevel.READ) <= 0) // check if permission is at least read level
        {
            return permissionOption.get().getDocument();
        }else {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
    }

    // add document
    @PostMapping("/")
    DocumentEntity addDocument(@RequestBody DocumentEntity document, Authentication authentication, HttpServletResponse response){

        // allow creation date in the past, if null set it

        // if in future
        if (document.getCreationDate().isAfter(LocalDateTime.now())){
            // reset to current time
            document.setCreationDate(LocalDateTime.now());
            response.setStatus(203);
        }

        var storedDoc = documentRepository.save(document);

        UserEntity currentUser = findByAuthentication(authentication);

        var permission = new PermissionEntity(
                null,// id?
                currentUser, // user (userEntity)
                storedDoc,// documents
                PermissionLevel.OWNER// authoritiy
        );

        // todo store user
        permissionRepository.save(permission);

        return storedDoc;
    }

}
