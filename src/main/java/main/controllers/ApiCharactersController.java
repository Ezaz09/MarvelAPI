package main.controllers;

import main.api.requests.characters.AddNewCharacterRequest;
import main.api.requests.characters.EditCharacterRequest;
import main.api.responses.characters.*;
import main.services.AuthenticationService;
import main.services.CharactersService;
import main.services.ComicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/characters")
public class ApiCharactersController extends DefaultController {

    @Autowired
    public ApiCharactersController(ComicsService comicsService, AuthenticationService authenticationService, CharactersService charactersService) {
        super(comicsService, authenticationService, charactersService);
    }

    @GetMapping(path = "")
    public ResponseEntity<ListOfCharactersResponse> getCharacters(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                  @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                                  @RequestParam(value = "name", required = false) String name,
                                                                  @RequestParam(value = "nameStartsWith", required = false) String nameStartsWith,
                                                                  @RequestParam(value = "comics", defaultValue = "0") int comics) throws Exception {
        if (!this.getListOfParamsOfSortForCharacters().contains(orderBy)) {
            ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибка параметра", "Неверное значение параметра Order by");
            listOfCharactersResponse.setErrors(errors);
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.BAD_REQUEST);
        }

        return this.getCharactersService().getCharacters(
                this.getAuthenticationService().getUriForListOfCharacters(offset,
                        limit,
                        orderBy,
                        name,
                        nameStartsWith,
                        comics));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CharacterResponse> getCharacterByID(@PathVariable int id) throws Exception {
        String uriForCharacter = this.getAuthenticationService().getUriForCharacter(id);
        return this.getCharactersService().getCharacterByID(uriForCharacter);
    }

    @GetMapping(path = "/{id}/comics")
    public ResponseEntity<CharacterComicsResponse> getComicsForCharacter(@PathVariable int id,
                                                                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                         @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                         @RequestParam(value = "orderBy", defaultValue = "focDate") String orderBy,
                                                                         @RequestParam(value = "format", required = false) String format,
                                                                         @RequestParam(value = "title", required = false) String title,
                                                                         @RequestParam(value = "diamondCode", required = false) String diamondCode) throws Exception {
        if (!this.getListOfParamsOfSortForComics().contains(orderBy)) {
            CharacterComicsResponse characterComicsResponse = new CharacterComicsResponse();
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибка параметра", "Неверное значение параметра Order by");
            characterComicsResponse.setErrors(errors);
            return new ResponseEntity<>(characterComicsResponse, HttpStatus.BAD_REQUEST);
        }

        if (format != null) {
            if (!this.getListOfComicsFormats().contains(format)) {
                CharacterComicsResponse characterComicsResponse = new CharacterComicsResponse();
                HashMap<String, String> errors = new HashMap<>();
                errors.put("Ошибка параметра", "Неверное значение параметра Format");
                characterComicsResponse.setErrors(errors);
                return new ResponseEntity<>(characterComicsResponse, HttpStatus.BAD_REQUEST);
            }
        }

        return this.getComicsService().getCharacterComics(
                this.getAuthenticationService().getUriForListOfCharacterComics(id,
                        offset,
                        limit,
                        orderBy,
                        format,
                        title,
                        diamondCode));
    }

    @GetMapping(path = "/getFromDB")
    public ResponseEntity<ListOfCharactersResponse> getCharactersFromDB(@RequestParam(defaultValue = "0",required = false)int offset,
                                                                        @RequestParam(defaultValue = "20",required = false)int limit,
                                                                        @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
                                                                        @RequestParam(value = "name", required = false) String name,
                                                                        @RequestParam(value = "nameStartsWith", required = false) String nameStartsWith) {
        if (!this.getListOfParamsOfSortForCharacters().contains(orderBy)) {
            ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибка параметра", "Неверное значение параметра Order by");
            listOfCharactersResponse.setErrors(errors);
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.BAD_REQUEST);
        }

        return this.getCharactersService().getCharactersFromDB(this.getPageableForCharacters(offset,limit,orderBy), name, nameStartsWith);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<AddNewCharacterResponse> addNewCharacter(@ModelAttribute AddNewCharacterRequest characterRequest) {
        return this.getCharactersService().addNewCharacter(characterRequest);
    }

    @PutMapping(path = "/edit")
    public ResponseEntity<EditCharacterResponse> editCharacter(@ModelAttribute EditCharacterRequest editCharacterRequest) {
        return this.getCharactersService().editCharacter(editCharacterRequest);
    }
}
