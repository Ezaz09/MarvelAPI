package main.api.responses.comics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditComicResponse {
    private boolean result;
    private HashMap<String, String> errors;
}
