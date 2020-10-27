package main.api.requests.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditCharacterRequest {
    private String name;
    private String description;
    private MultipartFile thumbnail;
    private String resourceURI;
    private List<String> comics;
}
