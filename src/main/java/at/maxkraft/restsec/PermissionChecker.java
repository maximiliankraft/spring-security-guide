package at.maxkraft.restsec;


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

    public boolean hasPermissionInRepo(String className, String permissionName, String  user, Long objectId){

        var userEntity = userRepository.findByUsername(user).get();

        var permissionOption = permissionRepository.findByClassNameAndNameAndUserAndObjectId(
                className,
                permissionName,
                userEntity,
                objectId);

        return permissionOption.isPresent();
    }


    public boolean isOwner(String username, String className, Long objectId){

        return this.hasPermissionInRepo(className, "owner", username, objectId);
    }

    public boolean checkPermission(String name, String className, Long objectId, String permissionName) {

        /*
         * Reading is allowed when:
         *   owner calls the endpoint
         *   endpoint caller is an admin
         *   if the resoruce has been granted to the user
         * */

        return isAdmin(name) ||
                hasPermissionInRepo(className, permissionName, name, objectId) ||
                isOwner(name, className, objectId);

    }

}
