package at.maxkraft.restsec;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@RequestMapping("/docs")
@AllArgsConstructor
public class DocumentController {

    DocumentRepository documentRepository;

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

        // todo store user


        return documentRepository.save(document);
    }

}
