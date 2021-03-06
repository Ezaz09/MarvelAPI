package main.api.responses.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddNewCharacterResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
