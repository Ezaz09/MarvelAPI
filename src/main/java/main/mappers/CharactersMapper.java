package main.mappers;

import liquibase.util.file.FilenameUtils;
import main.api.responses.characters.CharacterDTO;
import main.api.responses.characters.CharacterResponse;
import main.api.responses.Thumbnail;
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

public class CharactersMapper extends DefaultMapper {

    public ListOfCharactersResponse characterJsonToCharactersList(String characterJson) throws Exception {
        ListOfCharactersResponse listOfCharactersResponse = new ListOfCharactersResponse();
        if (characterJson == null) {
            listOfCharactersResponse.setErrors(this.getEmptyJsonError());
            return listOfCharactersResponse;
        }

        JSONArray resultList = parseCharacterJsonData(characterJson);

        if (resultList == null) {
            listOfCharactersResponse.setErrors(this.getParsingError());
            return listOfCharactersResponse;
        }

        Iterator jsonDataItr = resultList.iterator();

        List<CharacterDTO> charactersList = new ArrayList<>();
        while (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            CharacterDTO character = createNewCharacterDTO(recordFromJson);

            charactersList.add(character);
        }

        if(charactersList.isEmpty()) {
            listOfCharactersResponse.setErrors(this.getNotFoundError());
        } else {
            listOfCharactersResponse.setCharacters(charactersList);
        }

        return listOfCharactersResponse;
    }

    public CharacterResponse characterJsonToCharacterResponse(String characterJson) throws Exception {
        CharacterResponse characterResponse = new CharacterResponse();

        if (characterJson == null) {
            characterResponse.setErrors(this.getEmptyJsonError());
            return characterResponse;
        }

        JSONArray resultList = parseCharacterJsonData(characterJson);

        if (resultList == null) {
            characterResponse.setErrors(this.getParsingError());
            return characterResponse;
        }

        Iterator jsonDataItr = resultList.iterator();

        if (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            CharacterDTO character = createNewCharacterDTO(recordFromJson);

            characterResponse.setCharacter(character);
        }

        if(characterResponse.getCharacter() == null) {
            characterResponse.setErrors(this.getNotFoundError());
        }

        return characterResponse;
    }

    private CharacterDTO createNewCharacterDTO(JSONObject recordFromJson) throws Exception {
        CharacterDTO character = new CharacterDTO();
        try {
            character.setName((String) recordFromJson.get("name"));
            character.setDescription((String) recordFromJson.get("description"));

            String modified = (String) recordFromJson.get("modified");
            character.setModified(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").parse(modified));

            Thumbnail characterThumbnail = new Thumbnail();
            JSONObject thumbnail = (JSONObject) recordFromJson.get("thumbnail");

            characterThumbnail.setExtension((String) thumbnail.get("extension"));
            characterThumbnail.setPath((String) thumbnail.get("path"));

            character.setThumbnail(characterThumbnail);
            character.setResourceURI((String) recordFromJson.get("resourceURI"));

            character.setComics(parseComicsForCharacter(recordFromJson));
        } catch (java.text.ParseException e) {
            throw new Exception("Произошла ошибка при парсинге элемента ответа - " + e.getMessage());
        }

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

    private JSONArray parseCharacterJsonData(String characterJson) throws Exception {
        JSONArray resultList = null;
        Object parsedData;

        try {
           parsedData = new JSONParser().parse(characterJson);
        } catch (ParseException e) {
            throw new Exception("Произошла ошибка при парсинге ответа - " + e.getMessage());
        }

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

        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setPath(character.getThumbnail());
        String extension = FilenameUtils.getExtension(character.getThumbnail());
        thumbnail.setExtension(extension);

        characterDTO.setThumbnail(thumbnail);
        characterDTO.setResourceURI(character.getResourceURI());

        return characterDTO;
    }
}
