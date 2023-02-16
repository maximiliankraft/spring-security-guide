package at.maxkraft.restsec;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TestRessource {
    @Id
    private Long id;

    @Column(unique = true)
    String title;

    String description;
}
