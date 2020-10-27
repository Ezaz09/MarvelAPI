package main.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "characters2comics")
@AllArgsConstructor
@NoArgsConstructor
public class Character2Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @OneToOne
    @JoinColumn(name = "character_id", referencedColumnName = "id")
    private Character character;

    @OneToOne
    @JoinColumn(name = "comic_id", referencedColumnName = "id")
    private Comic comic;
}
