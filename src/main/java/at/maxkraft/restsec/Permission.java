package at.maxkraft.restsec;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    private Long id;

    @OneToOne
    UserEntity user;

    String className;

    Long objectId;

    String name; // e.g. read, write, delete, grant, ...

}
