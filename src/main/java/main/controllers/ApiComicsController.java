package main.controllers;

import main.api.requests.comics.AddNewComicRequest;
import main.api.requests.comics.EditComicRequest;
import main.api.responses.characters.ListOfCharactersResponse;
import main.api.responses.comics.AddNewComicResponse;
import main.api.responses.comics.ComicResponse;
import main.api.responses.comics.EditComicResponse;
import main.api.responses.comics.ListOfComicsResponse;
import main.services.AuthenticationService;
import main.services.CharactersService;
import main.services.ComicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/comics")
public class ApiComicsController extends DefaultController {

    @Autowired
    public ApiComicsController(ComicsService comicsService,
                               AuthenticationService authenticationService,
                               CharactersService charactersService) {
        super(comicsService, authenticationService, charactersService);
    }

    @GetMapping(path = "")
    public ResponseEntity<ListOfComicsResponse> getComics(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                          @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                          @RequestParam(value = "orderBy", defaultValue = "focDate") String orderBy,
                                                          @RequestParam(value = "format", required = false) String format,
                                                          @RequestParam(value = "title", required = false) String title,
                                                          @RequestParam(value = "diamondCode", required = false) String diamondCode) throws Exception {
        ListOfComicsResponse listOfComicsResponse = new ListOfComicsResponse();
        if (!this.getListOfParamsOfSortForComics().contains(orderBy)) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Параметр запроса", "Неправильное значение параметра order by");
            listOfComicsResponse.setErrors(errors);
            return new ResponseEntity<>(listOfComicsResponse, HttpStatus.BAD_REQUEST);
        }

        if (format != null) {
            if (!this.getListOfComicsFormats().contains(format)) {
                HashMap<String, String> errors = new HashMap<>();
                errors.put("Параметр запроса", "Неправильное значение параметра format");
                listOfComicsResponse.setErrors(errors);
                return new ResponseEntity<>(listOfComicsResponse, HttpStatus.BAD_REQUEST);
            }
        }

        return this.getComicsService().getComics(
                this.getAuthenticationService().getUriForListOfComics(offset,
                        limit,
                        orderBy,
                        format,
                        title,
                        diamondCode));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ComicResponse> getComicByID(@PathVariable int id) throws Exception {
        String uriForCharacter = this.getAuthenticationService().getUriForComic(id);
        return this.getComicsService().getComicByID(uriForCharacter);
    }

    @GetMapping(path = "/{id}/characters")
    public ResponseEntity<ListOfCharactersResponse> getCharacters(@PathVariable int id,
                                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                  @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                                  @RequestParam(value = "name", required = false) String name,
                                                                  @RequestParam(value = "nameStartsWith", required = false) String nameStartsWith) throws Exception {
        ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
        if (!this.getListOfParamsOfSortForCharacters().contains(orderBy)) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Параметр запроса", "Неправильное значение параметра order by");
            listOfCharactersResponse.setErrors(errors);
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.OK);
        }

        return this.getCharactersService().getCharacters(
                this.getAuthenticationService().getUriForComicCharacters(id,
                        offset,
                        limit,
                        orderBy,
                        name,
                        nameStartsWith));
    }

    @GetMapping(path = "/getFromDB")
    public ResponseEntity<ListOfComicsResponse> getComicsFromDB(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                @RequestParam(value = "orderBy", defaultValue = "title") String orderBy,
                                                                @RequestParam(value = "title", required = false) String title,
                                                                @RequestParam(value = "format", required = false) String format,
                                                                @RequestParam(value = "diamondCode", required = false) String diamondCode) {
        ListOfComicsResponse listOfComicsResponse = new ListOfComicsResponse();
        if (!this.getListOfParamsOfSortForComics().contains(orderBy)) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Параметр запроса", "Неправильное значение параметра order by");
            listOfComicsResponse.setErrors(errors);
            return new ResponseEntity<>(listOfComicsResponse, HttpStatus.BAD_REQUEST);
        }

        return this.getComicsService().getComicsFromDB(this.getPageableForComics(offset,limit,orderBy), title, format, diamondCode);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<AddNewComicResponse> addNewComic(@ModelAttribute AddNewComicRequest addNewComicRequest) {
      return this.getComicsService().addNewComic(addNewComicRequest);
    }

    @PutMapping(path = "/edit")
    public ResponseEntity<EditComicResponse> editComic(@ModelAttribute EditComicRequest editComicRequest) {
        return this.getComicsService().editComic(editComicRequest);
    }
}
