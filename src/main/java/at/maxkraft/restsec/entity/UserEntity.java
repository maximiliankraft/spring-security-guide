package at.maxkraft.restsec.entity;

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

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    Long userId;

    public String username, password;

    // don't use lombok here
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
