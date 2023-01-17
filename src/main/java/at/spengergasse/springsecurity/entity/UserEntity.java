package at.spengergasse.springsecurity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Set;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    public static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    Long userId;

    @Column(nullable = false, unique = true)
    public String username;

    @Column(nullable = false)
    public String password;

    // overrides lombok
    public void setPassword(String password){
        this.password = ENCODER.encode(password);
    }

    @ManyToMany
    Set<GrantedAuthorityEntity> authorities;

    @Column(nullable = false)
    Boolean isUser;

    // override lombok builder behaviour
    public static class UserEntityBuilder {
        public UserEntityBuilder password(String password) {
            this.password = ENCODER.encode(password);
            return this;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
