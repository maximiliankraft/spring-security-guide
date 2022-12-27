package at.maxkraft.restsec.DataInitializer;

import at.maxkraft.restsec.entity.GrantedAuthorityEntity;
import at.maxkraft.restsec.entity.UserEntity;
import at.maxkraft.restsec.repository.GrantedAuthorityRepository;
import at.maxkraft.restsec.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

import java.util.Set;

@Order(0)
@AllArgsConstructor
@Component
public class UserInitializer implements CommandLineRunner {

    UserRepository userRepository;

    GrantedAuthorityRepository grantedAuthorityRepository;


    // configure role hierarchy
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "admin > write \nwrite > read \nread > nothing";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Override
    public void run(String... args) {

        // add roles
        grantedAuthorityRepository.save(GrantedAuthorityEntity.of("admin"));
        grantedAuthorityRepository.save(GrantedAuthorityEntity.of("write"));
        grantedAuthorityRepository.save(GrantedAuthorityEntity.of("read"));
        grantedAuthorityRepository.save(GrantedAuthorityEntity.of("nothing"));

        // add users
        userRepository.save(
                UserEntity.builder()
                        .username("admin")
                        .password("admin")
                        .isUser(true)
                        .authorities(
                                Set.of(
                                        GrantedAuthorityEntity.of("admin"),
                                        GrantedAuthorityEntity.of("write"),
                                        GrantedAuthorityEntity.of("read")
                                )
                        ).build());

        userRepository.save(
                UserEntity.builder()
                        .username("max")
                        .password("123456")
                        .isUser(true)
                        .authorities(
                                Set.of(
                                        GrantedAuthorityEntity.of("write"),
                                        GrantedAuthorityEntity.of("read"))
                        ).build());
    }

}
