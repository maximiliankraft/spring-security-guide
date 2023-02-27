package at.maxkraft.restsec;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {


    CustomAuthenticationProvider customAuthenticationProvider;

    /**
    * Checklist for setting up an Authentication properly:
     * Existing user (with inital data generator)
     * (Request)
     * Custom AuthenticationProvider accessing user repository
     * Disable csrf - csrf(AbstractHttpConfigurer::disable)
     * Correctly formatted path starting with /resource/**

     * */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //.logout(LogoutConfigurer::permitAll)
                //.formLogin().disable()
                //.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.loginPage("/login"))
                .csrf(AbstractHttpConfigurer::disable) // !IMPORTANT see https://stackoverflow.com/a/19496356/17996814
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register/**").anonymous()
                        .requestMatchers("/user/login/**").anonymous()
                        //.requestMatchers("/user/changePassword/**").authenticated()
                        //.requestMatchers("/user/changePassword/**").authenticated()
                        .anyRequest().authenticated()
                        //.requestMatchers("/h2-console").permitAll()
                        //.anyRequest().authenticated()
                )
                //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt) // introduce a token based system
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // (3)
                .authenticationProvider(customAuthenticationProvider)
                .httpBasic(Customizer.withDefaults()) // (4)
                .build();
    }

}
