package main.controllers;

import main.api.requests.comics.AddNewComicRequest;
import main.api.responses.characters.ListOfCharactersResponse;
import main.api.responses.comics.AddNewComicResponse;
import main.api.responses.comics.ComicResponse;
import main.api.responses.comics.ListOfComicsResponse;
import main.services.AuthenticationService;
import main.services.CharactersService;
import main.services.ComicsService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

//TODO Посмотреть в каком поле API храниться обложка комикса
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
                                                          @RequestParam(value = "diamondCode", required = false) String diamondCode) throws NoSuchAlgorithmException, ParseException, java.text.ParseException {
        if (!this.getListOfParamsOfSortForComics().contains(orderBy)) {
            return new ResponseEntity<>(new ListOfComicsResponse(), HttpStatus.OK);
        }

        if (format != null) {
            if (!this.getListOfComicsFormats().contains(format)) {
                return new ResponseEntity<>(new ListOfComicsResponse(), HttpStatus.OK);
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
    public ResponseEntity<ComicResponse> getComicByID(@PathVariable int id) throws NoSuchAlgorithmException, ParseException, java.text.ParseException {
        String uriForCharacter = this.getAuthenticationService().getUriForComic(id);
        return this.getComicsService().getComicByID(uriForCharacter);
    }

    @GetMapping(path = "/{id}/characters")
    public ResponseEntity<ListOfCharactersResponse> getCharacters(@PathVariable int id,
                                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                  @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                                  @RequestParam(value = "name", required = false) String name,
                                                                  @RequestParam(value = "nameStartsWith", required = false) String nameStartsWith,
                                                                  @RequestParam(value = "comics", required = false) String comics) throws NoSuchAlgorithmException, ParseException, java.text.ParseException {
        if (!this.getListOfParamsOfSortForCharacters().contains(orderBy)) {
            return new ResponseEntity<>(new ListOfCharactersResponse(), HttpStatus.OK);
        }

        return this.getCharactersService().getCharacters(
                this.getAuthenticationService().getUriForComicCharacters(id,
                        offset,
                        limit,
                        orderBy,
                        name,
                        nameStartsWith));
    }

    @PostMapping(path = "/add")
    public ResponseEntity<AddNewComicResponse> addNewComic(@ModelAttribute AddNewComicRequest addNewComicRequest) {
      return this.getComicsService().addNewComic(addNewComicRequest);
    }
}
