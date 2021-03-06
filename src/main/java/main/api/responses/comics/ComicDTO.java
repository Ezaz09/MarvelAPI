package main.api.responses.comics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responses.Thumbnail;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComicDTO {
    private String title;
    private String issueNumber;
    private String variantDescription;
    private String description;
    private Thumbnail thumbnail;
    private Date modified;
    private String isbn;
    private String upc;
    private String diamondCode;
    private String format;
    private String pageCount;
    private List<String> characters;
}
