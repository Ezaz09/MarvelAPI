package main.mappers;

import liquibase.util.file.FilenameUtils;
import main.api.responses.characters.CharacterDTO;
import main.api.responses.characters.CharacterResponse;
import main.api.responses.characters.CharacterThumbnail;
import main.api.responses.characters.ListOfCharactersResponse;
import main.models.Character;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CharactersMapper {

    public ListOfCharactersResponse characterJsonToCharactersList(String characterJson) throws ParseException, java.text.ParseException {

        if (characterJson == null) {
            return new ListOfCharactersResponse();
        }

        JSONArray resultList = parseCharacterJsonData(characterJson);

        if (resultList == null) {
            return new ListOfCharactersResponse();
        }

        Iterator jsonDataItr = resultList.iterator();

        List<CharacterDTO> charactersList = new ArrayList<>();
        while (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            CharacterDTO character = createNewCharacterDTO(recordFromJson);

            charactersList.add(character);
        }
        ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
        listOfCharactersResponse.setCharacters(charactersList);
        return listOfCharactersResponse;
    }

    public CharacterResponse characterJsonToCharacterResponse(String characterJson) throws ParseException, java.text.ParseException {

        if (characterJson == null) {
            return new CharacterResponse();
        }

        JSONArray resultList = parseCharacterJsonData(characterJson);

        if (resultList == null) {
            return new CharacterResponse();
        }

        Iterator jsonDataItr = resultList.iterator();

        CharacterResponse characterResponse = new CharacterResponse();
        if (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            CharacterDTO character = createNewCharacterDTO(recordFromJson);

            characterResponse.setCharacter(character);
        }
        return characterResponse;
    }

    private CharacterDTO createNewCharacterDTO(JSONObject recordFromJson) throws java.text.ParseException {
        CharacterDTO character = new CharacterDTO();
        character.setName((String) recordFromJson.get("name"));
        character.setDescription((String) recordFromJson.get("description"));

        String modified = (String) recordFromJson.get("modified");
        character.setModified(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").parse(modified));

        CharacterThumbnail characterThumbnail = new CharacterThumbnail();
        JSONObject thumbnail = (JSONObject) recordFromJson.get("thumbnail");

        characterThumbnail.setExtension((String) thumbnail.get("extension"));
        characterThumbnail.setPath((String) thumbnail.get("path"));

        character.setThumbnail(characterThumbnail);
        character.setResourceURI((String) recordFromJson.get("resourceURI"));

        character.setComics(parseComicsForCharacter(recordFromJson));

        return character;
    }

    private List<String> parseComicsForCharacter(JSONObject recordFromJson) {
        List<String> parseComicsList = new ArrayList<>();
        JSONArray comicsList = null;

        if (recordFromJson.containsKey("comics")) {
            JSONObject comics = (JSONObject) recordFromJson.get("comics");
            if (comics.containsKey("items")) {
                comicsList = (JSONArray) comics.get("items");
            }
        }

        if (comicsList == null) {
            return parseComicsList;
        }

        for (Object comic : comicsList) {
            JSONObject recordFromComicsList = (JSONObject) comic;
            parseComicsList.add((String) recordFromComicsList.get("name"));
        }

        return parseComicsList;
    }

    private JSONArray parseCharacterJsonData(String characterJson) throws ParseException {
        JSONArray resultList = null;

        Object parsedData = new JSONParser().parse(characterJson);
        JSONObject resultJsonData;
        if (!parsedData.getClass().getSimpleName().equals("JSONObject")) {
            //Ошибка парсинга
            return null;
        } else {
            resultJsonData = (JSONObject) parsedData;
        }

        if (resultJsonData.containsKey("data")) {
            JSONObject dataFromResult = (JSONObject) resultJsonData.get("data");
            if (dataFromResult.containsKey("results")) {
                resultList = (JSONArray) dataFromResult.get("results");
            }
        }

        return resultList;
    }

    public CharacterDTO characterToCharacterDTO(Character character) {
        CharacterDTO characterDTO = new CharacterDTO();

        if (character == null) {
            return null;
        }

        characterDTO.setName(character.getName());
        characterDTO.setDescription(character.getDescription());
        characterDTO.setModified(character.getModified());

        CharacterThumbnail characterThumbnail = new CharacterThumbnail();
        characterThumbnail.setPath(character.getThumbnail());
        String extension = FilenameUtils.getExtension(character.getThumbnail());
        characterThumbnail.setExtension(extension);

        characterDTO.setResourceURI(character.getResourceURI());

        return characterDTO;
    }
}
