package at.maxkraft.restsec.permission;

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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    String principal; // todo change to role based later

    @Builder.Default
    Long objectId = ALL_OBJECTS;

    String targetTypeName;

    @Builder.Default
    PermissionType permName = PermissionType.read;
}
