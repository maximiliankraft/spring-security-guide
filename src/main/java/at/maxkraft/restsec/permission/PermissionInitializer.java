package at.maxkraft.restsec.permission;

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

    @Override
    public void run(String... args) {

        // admin has the full access to grant new permissions
        PermissionEntity adminFullPermissions = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("PermissionEntity")
                .principal("admin")
                .build();

        // admin has full control over all assignment objects
        PermissionEntity adminFullAssignment = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("Assignment")
                .principal("admin")
                .build();

        // admin has full control over all user objects
        PermissionEntity adminFullUser = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("UserEntity")
                .principal("admin")
                .build();

        // max has full control over his Assignment objects
        // todo user based ownership
        PermissionEntity maxHisAssignments = PermissionEntity.builder()
                .permName(PermissionType.full)
                .objectId(PermissionEntity.ALL_OBJECTS)
                .targetTypeName("Assignment")
                .principal("max")
                .build();

        permissionRepository.save(adminFullAssignment);
        permissionRepository.save(adminFullUser);
        permissionRepository.save(maxHisAssignments);
        permissionRepository.save(adminFullPermissions);
    }
}
