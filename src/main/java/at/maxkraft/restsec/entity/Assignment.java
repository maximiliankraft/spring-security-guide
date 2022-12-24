package at.maxkraft.restsec.entity;

import at.maxkraft.restsec.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    String title, description;

    @OneToOne
    UserEntity owner;

}
