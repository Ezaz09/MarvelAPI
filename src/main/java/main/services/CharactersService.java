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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    public ResponseEntity<ListOfCharactersResponse> getCharacters(String uri) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String result;

        try {
            result = restTemplate.getForObject(uri, String.class);
        } catch (HttpClientErrorException e) {
            ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
            HashMap<String, String> errors = new HashMap<>();
            listOfCharactersResponse.setErrors(errors);
            if (e.getRawStatusCode() == 409) {
                errors.put("Запрос не может быть выполнен из-за конфликтного обращения к ресурсу", e.getMessage());
                return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.NOT_FOUND);
            } else {
                errors.put("Ошибка сервера", e.getMessage());
                return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        ListOfCharactersResponse listOfCharactersResponse = new CharactersMapper().characterJsonToCharactersList(result);

        if (listOfCharactersResponse.getErrors() != null) {
            if (listOfCharactersResponse.getErrors().get("Ошибка парсинга") != null) {
                return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.NOT_FOUND);
            }

        } else {
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.OK);
        }
    }

    public ResponseEntity<CharacterResponse> getCharacterByID(String uri) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String result;

        try {
            result = restTemplate.getForObject(uri, String.class);
        } catch (HttpClientErrorException e) {
            CharacterResponse characterResponse = new CharacterResponse();
            HashMap<String, String> errors = new HashMap<>();
            characterResponse.setErrors(errors);
            if (e.getRawStatusCode() == 404) {
                errors.put("Результат запроса", "Ответ на запрос пустой!");
                return new ResponseEntity<>(characterResponse, HttpStatus.NOT_FOUND);
            } else {
                errors.put("Ошибка сервера", e.getMessage());
                return new ResponseEntity<>(characterResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        CharacterResponse characterResponse = new CharactersMapper().characterJsonToCharacterResponse(result);

        if (characterResponse.getErrors() != null) {
            if (characterResponse.getErrors().get("Ошибка парсинга") != null) {
                return new ResponseEntity<>(characterResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(characterResponse, HttpStatus.NOT_FOUND);
            }
        }

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

        HashMap<String, Object> structureOfCheck = checkParamsFromCharacterRequest(characterRequest);
        boolean result = (boolean) structureOfCheck.get("result");

        if (!result) {
            addNewCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибки", (String) structureOfCheck.get("errors"));
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

        Character character = new Character();
        character.setName(characterRequest.getName());
        character.setDescription(characterRequest.getDescription());
        character.setModified(new Date());
        character.setResourceURI(characterRequest.getResourceURI());

        String pathToPhoto = this.getImageService().savePhoto(characterRequest.getThumbnail(), "Character");
        if (pathToPhoto != null) {
            character.setThumbnail(pathToPhoto);
        }

        try {
            this.getCharactersRepository().save(character);
        } catch (Exception e) {
            addNewCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибка при сохранении нового персонажа", e.getMessage());
            addNewCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.BAD_REQUEST);
        }

        if (characterRequest.getComics() != null) {
            boolean resultOfSave = this.findComicsForCharacterAndSaveDependencies(characterRequest.getComics(),
                    character);
            if (!resultOfSave) {
                addNewCharacterResponse.setResult(false);
                HashMap<String, String> errors = new HashMap<>();
                errors.put("Ошибка при сохранении ссылок на комиксы для нового персонажа", "Имя персонажа - " + characterRequest.getName());
                addNewCharacterResponse.setErrors(errors);
                return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.BAD_REQUEST);
            }
        }

        addNewCharacterResponse.setResult(true);

        return new ResponseEntity<>(addNewCharacterResponse, HttpStatus.OK);
    }

    private HashMap<String, Object> checkParamsFromCharacterRequest(AddNewCharacterRequest characterRequest) {
        HashMap<String, Object> structureOfResult = new HashMap<>();
        boolean result = true;
        String errors = "";

        if (characterRequest.getName() == null ||
                characterRequest.getName().length() > 255 ||
                characterRequest.getName().equals("")) {
            result = false;
            errors = errors + "Имя персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getDescription() == null ||
                characterRequest.getDescription().length() > 255 ||
                characterRequest.getDescription().equals("")) {
            result = false;
            errors = errors + "Описание персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getResourceURI() == null ||
                characterRequest.getResourceURI().length() > 255 ||
                characterRequest.getResourceURI().equals("")) {
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

        if (characterRequest.getName() == null ||
                characterRequest.getName().length() > 255 ||
                characterRequest.getName().equals("")) {
            result = false;
            errors = errors + "Имя персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getDescription() == null ||
                characterRequest.getDescription().length() > 255 ||
                characterRequest.getDescription().equals("")) {
            result = false;
            errors = errors + "Описание персонажа превышает 255 символов! \n";
        }

        if (characterRequest.getResourceURI() == null ||
                characterRequest.getResourceURI().length() > 255 ||
                characterRequest.getResourceURI().equals("")) {
            result = false;
            errors = errors + "Ссылка на персонажа превышает 255 символов! \n";
        }

        structureOfResult.put("result", result);
        structureOfResult.put("errors", errors);
        return structureOfResult;
    }

    public ResponseEntity<ListOfCharactersResponse> getCharactersFromDB(Pageable pg,
                                                                        String name,
                                                                        String nameStartsWith) {
        List<Character> characterList = new ArrayList<>();
        ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();

        if (name != null) {
            Character characterByName = this.getCharactersRepository().getCharacterByName(name);
            characterList.add(characterByName);
        } else if (nameStartsWith != null) {
            characterList = this.getCharactersRepository().getCharactersByNameStartsWithPlusPageable(pg, nameStartsWith);
        } else {
            characterList = this.getCharactersRepository().findAll(pg).getContent();
        }

        if (characterList.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Результат запроса", "Ответ на запрос пустой!");
            listOfCharactersResponse.setErrors(errors);
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.NOT_FOUND);
        }

        List<CharacterDTO> characterDTOList = new ArrayList<>();
        for (Character character : characterList) {
            CharacterDTO characterDTO = new CharactersMapper().characterToCharacterDTO(character);

            if (characterDTO == null) {
                continue;
            }

            List<String> comicsForCharacter = this.findComicsForCharacterAndReturn(character);
            characterDTO.setComics(comicsForCharacter);
            characterDTOList.add(characterDTO);
        }

        if(characterDTOList.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Результат запроса", "Ответ на запрос пустой!");
            listOfCharactersResponse.setErrors(errors);
            return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.NOT_FOUND);
        }

        listOfCharactersResponse.setCharacters(characterDTOList);
        return new ResponseEntity<>(listOfCharactersResponse, HttpStatus.OK);
    }


    public ResponseEntity<EditCharacterResponse> editCharacter(EditCharacterRequest editCharacterRequest) {
        EditCharacterResponse editCharacterResponse = new EditCharacterResponse();

        if (editCharacterRequest == null) {
            editCharacterResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Запрос", "Запрос на редактирование персонажа пустой!");
            editCharacterResponse.setErrors(errors);
            return new ResponseEntity<>(editCharacterResponse, HttpStatus.BAD_REQUEST);
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

        if (editCharacterRequest.getThumbnail() != null) {
            this.getImageService().deleteUserPhotoFromServer(characterByName.getThumbnail());
        }

        if (editCharacterRequest.getThumbnail() == null) {
            characterByName.setThumbnail("");
        } else {
            String pathToPhoto = this.getImageService().savePhoto(editCharacterRequest.getThumbnail(), "Character");
            if (pathToPhoto != null) {
                characterByName.setThumbnail(pathToPhoto);
            }
        }

        try {
            this.getCharactersRepository().save(characterByName);
        } catch (Exception e) {
            editCharacterResponse.setResult(false);
            HashMap<String, String> error = new HashMap<>();
            error.put("Ошибка при редактировании персонажа", e.getMessage());
            editCharacterResponse.setErrors(error);
            return new ResponseEntity<>(editCharacterResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (editCharacterRequest.getComics() != null) {
            boolean resultOfDeleting = this.deleteCharacterFromComics(characterByName);
            if (!resultOfDeleting) {
                editCharacterResponse.setResult(false);
                HashMap<String, String> error = new HashMap<>();
                error.put("Ошибка при удалении ссылок на комиксы для персонажа", "Имя персонажа - " + editCharacterRequest.getName());
                editCharacterResponse.setErrors(error);
                return new ResponseEntity<>(editCharacterResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            boolean resultOfSave = this.findComicsForCharacterAndSaveDependencies(editCharacterRequest.getComics(),
                    characterByName);
            if (!resultOfSave) {
                editCharacterResponse.setResult(false);
                HashMap<String, String> error = new HashMap<>();
                error.put("Ошибка при сохранении ссылок на комиксы для персонажа", "Имя персонажа - " + editCharacterRequest.getName());
                editCharacterResponse.setErrors(error);
                return new ResponseEntity<>(editCharacterResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        editCharacterResponse.setResult(true);

        return new ResponseEntity<>(editCharacterResponse, HttpStatus.OK);
    }
}
