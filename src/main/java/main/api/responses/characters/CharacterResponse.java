package main.api.responses.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private CharacterDTO character;
    private HashMap<String, String> errors;

}
