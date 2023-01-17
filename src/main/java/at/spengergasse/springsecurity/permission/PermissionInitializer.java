package at.spengergasse.springsecurity.permission;

import at.spengergasse.springsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Store permissions without any previous checks
 * mainly for admin user to grant new permissions from
 * there on.
 * */
@Component
// not really necessary since it is all decoupled */
@Order(30)
@AllArgsConstructor
public class PermissionInitializer implements CommandLineRunner {

    PermissionRepository permissionRepository;

    UserRepository userRepository;

    @Override
    public void run(String... args) {

        // admin has the full access to grant new permissions
        PermissionEntity adminFullPermissions = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("PermissionEntity")
                .principal(userRepository.findByUsername("admin").get())
                .build();

        // admin has full control over all assignment objects
        PermissionEntity adminFullAssignment = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("Assignment")
                .principal(userRepository.findByUsername("admin").get())
                .build();

        // admin has full control over all user objects
        PermissionEntity adminFullUser = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("UserEntity")
                .principal(userRepository.findByUsername("admin").get())
                .build();

        // max has full control over his Assignment objects
        PermissionEntity maxHisAssignments = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("Assignment")
                .principal(userRepository.findByUsername("max").get())
                .build();

        PermissionEntity adminMaxReadAccess = PermissionEntity.builder()
                .permName(PermissionType.read)
                .objectId(2L)
                .targetTypeName("Assignment")
                .principal(userRepository.findByUsername("admin").get())
                .build();

        PermissionEntity assignmentMaxFullAccess = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(2L)
                .targetTypeName("Assignment")
                .principal(userRepository.findByUsername("max").get())
                .build();

        permissionRepository.save(adminFullAssignment);
        permissionRepository.save(adminFullUser);
        permissionRepository.save(maxHisAssignments);
        permissionRepository.save(adminFullPermissions);
        permissionRepository.save(adminMaxReadAccess);
        permissionRepository.save(assignmentMaxFullAccess);
    }
}
