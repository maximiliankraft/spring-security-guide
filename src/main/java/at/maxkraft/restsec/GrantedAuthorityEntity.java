package at.maxkraft.restsec;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GrantedAuthorityEntity implements GrantedAuthority {

    public static GrantedAuthorityEntity of(String authority){
        return new GrantedAuthorityEntity(authority);
    }

    @Id
    String authority;

}
