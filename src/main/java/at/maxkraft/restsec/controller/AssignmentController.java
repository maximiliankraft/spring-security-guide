package at.maxkraft.restsec.controller;

import at.maxkraft.restsec.entity.Assignment;
import at.maxkraft.restsec.exception.ResourceNotFoundException;
import at.maxkraft.restsec.permission.PermissionEntity;
import at.maxkraft.restsec.permission.PermissionService;
import at.maxkraft.restsec.permission.PermissionType;
import at.maxkraft.restsec.repository.AssignmentRepository;
import at.maxkraft.restsec.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    PermissionService ps;

    // get all assignments with read access
    //@PreAuthorize("hasAnyAuthority('read')")
    @GetMapping("/")
    public Iterable<Assignment> fetchEntities(Authentication authentication, HttpServletResponse response) {

        var permitted = ps.checkGeneralPermission(
                userRepository.findByUsername(authentication.getName()).get(),
                "Assignment",
                PermissionType.read
        );

        if (permitted){
            return assignmentRepository.findAllByOwnerUsername(authentication.getName());
        }else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return List.of();
        }
    }

    // get by id
    // uses Spring EL syntax
    @PreAuthorize("@permissionService.checkIdBasedPermission(@userRepository.findByUsername(authentication.name).get(),#id,'Assignment', 'read')")
    @GetMapping("/{id}")
    public Assignment fetchEntity(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        var result = assignmentRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException();
        } else {
            return result.get();
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
        var permitted = ps.checkIdBasedPermission(
                userRepository.findByUsername(authentication.getName()).get(),
                PermissionEntity.NEW_OBJECT, // id is a wildcard mask
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


    @PreAuthorize("@permissionService.checkIdBasedPermission(@userRepository.findByUsername(authentication.name).get(),#id,'Assignment', 'write')")
    @PatchMapping("/{id}")
    Assignment updateAssignment(@RequestBody Optional<Assignment> assignment, @PathVariable Long id, Authentication authentication, HttpServletResponse response){
        if (assignment.isPresent()){
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return assignmentRepository.save(assignment.get());
        }else {
            throw new ResourceNotFoundException();
        }
    }

    // todo maybe require full access?
    @PreAuthorize("@permissionService.checkIdBasedPermission(@userRepository.findByUsername(authentication.name).get(),#id,'Assignment', 'write')")
    @DeleteMapping("/{id}")
    void updateAssignment(@PathVariable Long id, Authentication authentication, HttpServletResponse response){
        var deletionCandidate = assignmentRepository.findById(id);

        if (deletionCandidate.isPresent()){
            assignmentRepository.delete(deletionCandidate.get());
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
