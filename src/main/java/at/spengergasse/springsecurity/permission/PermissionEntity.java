package at.spengergasse.springsecurity.permission;

import at.spengergasse.springsecurity.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionEntity {

    public static final Long ALL_OBJECTS = -1L;
    public static final Long NEW_OBJECT = -2L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(nullable = false)
    @OneToOne(cascade = CascadeType.MERGE)
    UserEntity principal;

    @Column(nullable = false)
    Long objectId;

    String targetTypeName;

    @Builder.Default
    PermissionType permName = PermissionType.none;
}
