package main.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "comics")
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column()
    private String thumbnail;

    @Column(nullable = false)
    private String issueNumber;

    @Column(nullable = false)
    private String variantDescription;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Date modified;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String upc;

    @Column(nullable = false)
    private String diamondCode;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private String pageCount;

    @OneToMany
    @JoinColumn(name = "comic_id", referencedColumnName = "id", updatable = false)
    private List<Character2Comic> character2Comics;
}
