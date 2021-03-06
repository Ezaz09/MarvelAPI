package main.api.responses.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responses.Thumbnail;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {
    private String name;
    private String description;
    private Date modified;
    private Thumbnail thumbnail;
    private String resourceURI;
    private List<String> comics;
}
