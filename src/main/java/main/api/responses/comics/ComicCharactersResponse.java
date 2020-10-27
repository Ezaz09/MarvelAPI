package main.api.responses.comics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responses.characters.CharacterDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComicCharactersResponse {
    private List<CharacterDTO> characters;
}
