package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.characters.AddNewCharacterRequest;
import main.api.requests.characters.EditCharacterRequest;
import main.api.responses.characters.*;
import main.mappers.CharactersMapper;
import main.models.Character;
import main.repositories.Character2ComicRepository;
import main.repositories.CharactersRepository;
import main.repositories.ComicsRepository;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class CharactersService extends DefaultService {

    public CharactersService(ImageService imageService, CharactersRepository charactersRepository, ComicsRepository comicsRepository, Character2ComicRepository character2ComicRepository) {
        super(imageService, charactersRepository, comicsRepository, character2ComicRepository);
    }

    public ResponseEntity<ListOfCharactersResponse> getCharacters(String uri) throws ParseException, java.text.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        ListOfCharactersResponse listOfCharactersResponse = new CharactersMapper().characterJsonToCharactersList(result);
        return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.OK);
    }

    public ResponseEntity<CharacterResponse> getCharacterByID(String uri) throws ParseException, java.text.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        CharacterResponse characterResponse = new CharactersMapper().characterJsonToCharacterResponse(result);
        return new ResponseEntity<>(characterResponse, HttpStatus.OK);
    }

    public ResponseEntity<AddNewCharacterResponse> addNewCharacter(AddNewCharacterRequest characterRequest) {
        AddNewCharacterResponse addNewCharacterResponse = new AddNewCharacterResponse();
        if (characterRequest == null) {
            addNewCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Запрос", "Запрос на добавление нового персонажа пустой!");
            addNewCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.BAD_REQUEST);
        }

        Character characterByName = this.getCharactersRepository().getCharacterByName(characterRequest.getName());

        if (characterByName != null) {
            addNewCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Персонаж", "Добавляемый персонаж был найден в базе данных!");
            addNewCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.BAD_REQUEST);
        }

        HashMap<String, Object> structureOfCheck = checkParamsFromCharacterRequest(characterRequest);
        boolean result = (boolean) structureOfCheck.get("result");

        if (!result) {
            addNewCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибки", (String) structureOfCheck.get("errors"));
            addNewCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.BAD_REQUEST);
        }

        Character character = new Character();
        character.setName(characterRequest.getName());
        character.setDescription(characterRequest.getDescription());
        character.setModified(new Date());
        character.setResourceURI(characterRequest.getResourceURI());

        String pathToPhoto = this.getImageService().savePhoto(characterRequest.getThumbnail(), "Character");
        if (pathToPhoto != null) {
            character.setThumbnail(pathToPhoto);
        }

        this.getCharactersRepository().save(character);

        if(characterRequest.getComics() != null) {
            this.findComicsForCharacterAndSaveDependencies(characterRequest.getComics(),
                    character);
        }

        addNewCharacterResponse.setResult(true);

        return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.OK);
    }

    private HashMap<String, Object> checkParamsFromCharacterRequest(AddNewCharacterRequest characterRequest) {
        HashMap<String, Object> structureOfResult = new HashMap<>();
        boolean result = true;
        String errors = "";

        if (characterRequest.getName().length() > 255) {
            result = false;
            errors = errors + "Имя персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getDescription().length() > 255) {
            result = false;
            errors = errors + "Описание персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getResourceURI().length() > 255) {
            result = false;
            errors = errors + "Ссылка на персонажа превышает 255 символов! \n";
        }

        structureOfResult.put("result", result);
        structureOfResult.put("errors", errors);
        return structureOfResult;
    }

    private HashMap<String, Object> checkParamsFromEditCharacterRequest(EditCharacterRequest characterRequest) {
        HashMap<String, Object> structureOfResult = new HashMap<>();
        boolean result = true;
        String errors = "";

        if (characterRequest.getName().length() > 255) {
            result = false;
            errors = errors + "Имя персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getDescription().length() > 255) {
            result = false;
            errors = errors + "Описание персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getResourceURI().length() > 255) {
            result = false;
            errors = errors + "Ссылка на персонажа превышает 255 символов! \n";
        }

        structureOfResult.put("result", result);
        structureOfResult.put("errors", errors);
        return structureOfResult;
    }

    public ResponseEntity<ListOfCharactersResponse> getCharactersFromDB() {
        List<Character> characterList = this.getCharactersRepository().findAll();

        if(characterList.isEmpty()) {
            ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.OK);
        }

        List<CharacterDTO> characterDTOList = new ArrayList<>();
        for (Character character: characterList) {
            List<String> comicsForCharacter = this.findComicsForCharacterAndReturn(character);
            CharacterDTO characterDTO = new CharactersMapper().characterToCharacterDTO(character);

            if(characterDTO == null) {
                continue;
            }

            characterDTO.setComics(comicsForCharacter);
            characterDTOList.add(characterDTO);
        }

        ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
        listOfCharactersResponse.setCharacters(characterDTOList);
        return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.OK);
    }

    public ResponseEntity<EditCharacterResponse> editCharacter(EditCharacterRequest editCharacterRequest) {
        EditCharacterResponse editCharacterResponse = new EditCharacterResponse();
        editCharacterResponse.setResult(true);

        if (editCharacterRequest == null ||
                editCharacterRequest.getName().equals("")) {
            editCharacterResponse.setResult(false);
            return new ResponseEntity<>(editCharacterResponse, HttpStatus.OK);
        }

        HashMap<String, Object> structureOfCheck = checkParamsFromEditCharacterRequest(editCharacterRequest);
        boolean result = (boolean) structureOfCheck.get("result");

        if (!result) {
            editCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибки", (String) structureOfCheck.get("errors"));
            editCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(editCharacterResponse, HttpStatus.BAD_REQUEST);
        }

        Character characterByName = this.getCharactersRepository().getCharacterByName(editCharacterRequest.getName());

        if (characterByName == null) {
            editCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Персонаж", "Персонаж не был найден!");
            editCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(editCharacterResponse, HttpStatus.BAD_REQUEST);
        }

        characterByName.setDescription(editCharacterRequest.getDescription());
        characterByName.setModified(new Date());
        characterByName.setResourceURI(editCharacterRequest.getResourceURI());

        String pathToPhoto = this.getImageService().savePhoto(editCharacterRequest.getThumbnail(), "Character");
        if (pathToPhoto != null) {
            characterByName.setThumbnail(pathToPhoto);
        }

        this.getCharactersRepository().save(characterByName);

        if(editCharacterRequest.getComics() != null) {
            this.deleteCharacterFromComics(characterByName);
            this.findComicsForCharacterAndSaveDependencies(editCharacterRequest.getComics(),
                    characterByName);
        }

        return new ResponseEntity<>(editCharacterResponse, HttpStatus.OK);
    }
}
