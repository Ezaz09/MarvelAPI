package main.api.responses.comics;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfComicsResponse {
    private List<ComicDTO> comics;
    private HashMap<String, String> errors;
}
