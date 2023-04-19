package at.maxkraft.restsec.security;


import at.maxkraft.restsec.entities.PermissionLevel;
import at.maxkraft.restsec.repositories.AuthorityRepository;
import at.maxkraft.restsec.repositories.PermissionRepository;
import at.maxkraft.restsec.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PermissionChecker {

    PermissionRepository permissionRepository;

    UserRepository userRepository;

    AuthorityRepository authorityRepository;


    public boolean isAdmin(String username){
        var userOption = userRepository.findByUsername(username);

        if (userOption.isEmpty()){
            return false;
        }else {

            // get admin authority object from repository
            var adminAuth = authorityRepository.findByAuthority("admin");

            // unrwap user from Optional<>
            var user = userOption.get();
            // check if user has admin authority
            return user.getAuthorities().contains(adminAuth);
        }
    }

    public boolean hasPermissionInRepo(String className, PermissionLevel requestedPermissionLevel, String  user, Long objectId){

        var userEntity = userRepository.findByUsername(user).get();

        var permissionOption = permissionRepository.findByClassNameAndUserAndObjectId(
                className,
                userEntity,
                objectId);


        return permissionOption.isPresent() &&
                permissionOption.get().getPermissionLevel().ordinal() <= requestedPermissionLevel.ordinal();
    }


    public boolean isOwner(String username, String className, Long objectId){

        return this.hasPermissionInRepo(className, PermissionLevel.OWNER, username, objectId);
    }

    public boolean checkPermission(String name, String className, Long objectId, PermissionLevel permissionName) {

        /*
         * Reading is allowed when:
         *   owner calls the endpoint
         *   endpoint caller is an admin
         *   if the resoruce has been granted to the user
         * */

        return isAdmin(name) ||
                hasPermissionInRepo(className, permissionName, name, objectId);
                // isOwner(name, className, objectId);

    }

}
