package main.api.requests.comics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComicRequest {
    @Column(nullable = false)
    private String title;
    private MultipartFile thumbnail;
    private String issueNumber;
    private String variantDescription;
    private String description;
    private String isbn;
    private String upc;
    private String diamondCode;
    private String format;
    private String pageCount;
    private List<String> characters;
}
