package at.maxkraft.restsec.controller;

import at.maxkraft.restsec.entity.Assignment;
import at.maxkraft.restsec.permission.PermissionService;
import at.maxkraft.restsec.permission.PermissionType;
import at.maxkraft.restsec.repository.AssignmentRepository;
import at.maxkraft.restsec.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assignment")
@AllArgsConstructor
public class AssignmentController {

    AssignmentRepository assignmentRepository;
    UserRepository userRepository;
    PermissionService permissionService;

    // get all assignments with read access
    //@PreAuthorize("hasAnyAuthority('read')")
    @GetMapping("/")
    public Iterable<Assignment> fetchEntities(Authentication authentication, HttpServletResponse response) {

        var permitted = permissionService.checkGeneralPermission(
                authentication.getName(),
                "Assignment",
                PermissionType.read
        );

        if (permitted){
            // todo find all owned or granted access to
            return assignmentRepository.findAllByOwnerUsername(authentication.getName());
        }else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return List.of();
        }
    }

    // get by id
    // @PreAuthorize("hasAnyAuthority('read')")
    @GetMapping("/{id}")
    public Optional<Assignment> fetchEntity(
            @PathVariable("id") Long id,
            Authentication authentication,
            HttpServletResponse response
    ) {
        var permitted = permissionService.checkIdBasedPermission(
                authentication.getName(),
                id,
                "Assignment",
                PermissionType.read
        );

        if (permitted){
            return assignmentRepository.findById(id);
        }else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return Optional.empty();
        }
    }

    // create
    //@PreAuthorize("@customPermissionEvaluator.hasPermission()")
    @PutMapping("/")
    public Assignment addEntity(
            @RequestBody Assignment assignment,
            Authentication authentication,
            HttpServletResponse response
    ) {
        var permitted = permissionService.checkGeneralPermission(
                authentication.getName(),
                "Assignment",
                PermissionType.write
        );

        if (permitted){
            var owner = userRepository.findByUsername(authentication.getName());

            if (owner.isPresent()){
                response.setStatus(HttpServletResponse.SC_CREATED);
                assignment.setOwner(owner.get());
                return assignmentRepository.save(assignment);
            }
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }

    // todo patch

    // todo delete


}
