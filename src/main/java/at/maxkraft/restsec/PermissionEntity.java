package at.maxkraft.restsec;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    UserEntity user;

    @Deprecated
    @OneToOne
    DocumentEntity document;

    // todo DocumentEntity in the PermissionEntity only allows for
    // todo Permissions about a Document to be stored. Split it up into
    // todo String ressourceType, and
    // todo String ressourceId

    @Enumerated(EnumType.ORDINAL)
    PermissionLevel auth; // "none", "read", write, grant, owner


}
