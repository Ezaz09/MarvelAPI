package main.api.responses.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfCharactersResponse {
    private List<CharacterDTO> characters;
    private HashMap<String, String> errors;
}
