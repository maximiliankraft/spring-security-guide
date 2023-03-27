package at.maxkraft.restsec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// todo ordner erstellen

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    UserEntity user;

    String className;

    Long objectId;

    PermissionLevel permissionLevel; // e.g. read, write, delete, grant, owner

}
