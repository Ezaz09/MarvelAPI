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

    @Column(name = "character_id", nullable = false)
    private int characterId;

    @Column(name = "comic_id", nullable = false)
    private int comicId;
}
