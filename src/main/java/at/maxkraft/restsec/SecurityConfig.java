package at.maxkraft.restsec;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    UserRepoAuthProvider userRepoAuthProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .logout(LogoutConfigurer::permitAll)
                //.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.loginPage("/login"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register/**").anonymous()
                        .requestMatchers("/user/name").authenticated()
                        //.requestMatchers("/test/**").anonymous()
                        .requestMatchers("/test/**").authenticated()
                        .requestMatchers("/test").authenticated()
                        .anyRequest().authenticated()
                )
                //.authenticationProvider(customAuthenticationProvider) // not necessary but helps to explicitly include it
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt) // introduce a token based system
                .authenticationProvider(userRepoAuthProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // (3)
                .httpBasic(Customizer.withDefaults()) // (4)
                .build();
    }
}
