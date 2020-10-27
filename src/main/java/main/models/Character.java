package main.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Date modified;

    @Column()
    private String thumbnail;

    @Column(nullable = false)
    private String resourceURI;

    @OneToMany
    @JoinColumn(name = "character_id", referencedColumnName = "id", updatable = false)
    private List<Character2Comic> comics2Character;

}
