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

    @OneToOne
    DocumentEntity document;

    @Enumerated(EnumType.ORDINAL)
    PermissionLevel auth; // "none", "read", write, grant, owner


}
