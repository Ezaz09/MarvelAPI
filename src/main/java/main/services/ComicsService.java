package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.comics.AddNewComicRequest;
import main.api.responses.characters.CharacterComicsResponse;
import main.api.responses.comics.AddNewComicResponse;
import main.api.responses.comics.ComicResponse;
import main.api.responses.comics.ListOfComicsResponse;
import main.mappers.ComicsMapper;
import main.models.Comic;
import main.repositories.Character2ComicRepository;
import main.repositories.CharactersRepository;
import main.repositories.ComicsRepository;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
public class ComicsService extends DefaultService {

    public ComicsService(ImageService imageService, CharactersRepository charactersRepository, ComicsRepository comicsRepository, Character2ComicRepository character2ComicRepository) {
        super(imageService, charactersRepository, comicsRepository, character2ComicRepository);
    }

    public ResponseEntity<ListOfComicsResponse> getComics(String uri) throws ParseException, java.text.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        ListOfComicsResponse comicsList = new ComicsMapper().comicsJsonToComicsResponse(result);
        return new ResponseEntity<>(comicsList, HttpStatus.OK);
    }

    public ResponseEntity<ComicResponse> getComicByID(String uri) throws ParseException, java.text.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        ComicResponse comic = new ComicsMapper().comicJsonToComicResponse(result);
        return new ResponseEntity<>(comic, HttpStatus.OK);
    }

    public ResponseEntity<CharacterComicsResponse> getCharacterComics(String uri) throws ParseException, java.text.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        CharacterComicsResponse characterComicsResponse = new ComicsMapper().characterComicsJsonToCharacterComicsResponse(result);
        return new ResponseEntity<>(characterComicsResponse, HttpStatus.OK);
    }

    public ResponseEntity<AddNewComicResponse> addNewComic(AddNewComicRequest comicRequest) {
        AddNewComicResponse addNewComicResponse = new AddNewComicResponse();
        if(comicRequest == null) {
            addNewComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Запрос", "Запрос на добавление нового персонажа пустой!");
            addNewComicResponse.setErrors(errors);
            return new ResponseEntity<>(addNewComicResponse, HttpStatus.BAD_REQUEST);
        }

        Comic comicByTitle = this.getComicsRepository().getComicByTitle(comicRequest.getTitle());

        if (comicByTitle != null) {
            addNewComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Комикс", "Добавляемый комикс был найден в базе данных!");
            addNewComicResponse.setErrors(errors);
            return new ResponseEntity<>(addNewComicResponse, HttpStatus.BAD_REQUEST);
        }

        HashMap<String, Object> structureOfCheck = checkParamsFromComicRequest(comicRequest);
        boolean result = (boolean) structureOfCheck.get("result");

        if (!result) {
            addNewComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибки", (String) structureOfCheck.get("errors"));
            addNewComicResponse.setErrors(errors);
            return new ResponseEntity<>(addNewComicResponse, HttpStatus.BAD_REQUEST);
        }

        Comic comic = new Comic();
        comic.setTitle(comicRequest.getTitle());
        comic.setIssueNumber(comicRequest.getIssueNumber());
        comic.setDescription(comicRequest.getDescription());
        comic.setVariantDescription(comicRequest.getVariantDescription());
        comic.setIsbn(comicRequest.getIsbn());
        comic.setModified(new Date());
        comic.setUpc(comicRequest.getUpc());
        comic.setDiamondCode(comicRequest.getDiamondCode());
        comic.setFormat(comicRequest.getFormat());
        comic.setPageCount(comicRequest.getPageCount());

        String pathToPhoto = this.getImageService().savePhoto(comicRequest.getThumbnail(), "Comic");
        if (pathToPhoto != null) {
            comic.setThumbnail(pathToPhoto);
        }

        this.getComicsRepository().save(comic);

        if(comicRequest.getCharacters() != null) {
            this.findCharactersForComicAndSaveDependencies(comicRequest.getCharacters(),
                    comic);
        }

        addNewComicResponse.setResult(true);

        return new ResponseEntity<>(addNewComicResponse, HttpStatus.OK);
    }

    private HashMap<String, Object> checkParamsFromComicRequest(AddNewComicRequest comicRequest) {
        HashMap<String, Object> structureOfResult = new HashMap<>();
        boolean result = true;
        String errors = "";

        if (comicRequest.getTitle().length() > 255) {
            result = false;
            errors = errors + "Заголовок комикса превышает 255 символов! \n";
        }

        if (comicRequest.getIssueNumber().length() > 255) {
            result = false;
            errors = errors + "Номер выпуска комикса превышает 255 символов! \n";
        }

        if (comicRequest.getVariantDescription().length() > 255) {
            result = false;
            errors = errors + "Описание выпуска комикса превышает 255 символов! \n";
        }

        if (comicRequest.getDescription().length() > 255) {
            result = false;
            errors = errors + "Описание комикса превышает 255 символов! \n";
        }

        if (comicRequest.getIsbn().length() > 255) {
            result = false;
            errors = errors + "ISBN номер комикса превышает 255 символов! \n";
        }

        if (comicRequest.getUpc().length() > 255) {
            result = false;
            errors = errors + "UPC номер комикса превышает 255 символов! \n";
        }

        if (comicRequest.getDiamondCode().length() > 255) {
            result = false;
            errors = errors + "Diamond code комикса превышает 255 символов! \n";
        }

        if (comicRequest.getFormat().length() > 255) {
            result = false;
            errors = errors + "Имя формата комикса превышает 255 символов! \n";
        }

        if (comicRequest.getPageCount().length() > 255) {
            result = false;
            errors = errors + "Количество страниц комикса превышает 255 символов! \n";
        }

        structureOfResult.put("result", result);
        structureOfResult.put("errors", errors);
        return structureOfResult;
    }
}
