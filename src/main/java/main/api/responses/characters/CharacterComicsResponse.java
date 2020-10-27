package main.api.responses.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responses.comics.ComicDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterComicsResponse {
    private List<ComicDTO> comics;
}
