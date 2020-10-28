package main.mappers;

import liquibase.util.file.FilenameUtils;
import main.api.responses.Thumbnail;
import main.api.responses.characters.CharacterComicsResponse;
import main.api.responses.comics.ComicDTO;
import main.api.responses.comics.ComicResponse;
import main.api.responses.comics.ListOfComicsResponse;
import main.models.Comic;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComicsMapper extends DefaultMapper {

    public ListOfComicsResponse comicsJsonToComicsResponse(String comicsJson) throws Exception {
        ListOfComicsResponse listOfComicsResponse = new ListOfComicsResponse();
        if (comicsJson == null) {
            listOfComicsResponse.setErrors(this.getEmptyJsonError());
            return listOfComicsResponse;
        }

        JSONArray resultList = parseComicsJsonData(comicsJson);

        if (resultList == null) {
            listOfComicsResponse.setErrors(this.getParsingError());
            return listOfComicsResponse;
        }

        Iterator jsonDataItr = resultList.iterator();

        List<ComicDTO> comicsList = new ArrayList<>();
        while (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            ComicDTO comic = createNewComicDTO(recordFromJson);

            comicsList.add(comic);
        }

        if (comicsList.isEmpty()) {
            listOfComicsResponse.setErrors(this.getNotFoundError());
        } else {
            listOfComicsResponse.setComics(comicsList);
        }

        return listOfComicsResponse;
    }

    public CharacterComicsResponse characterComicsJsonToCharacterComicsResponse(String characterComicsJson) throws Exception {
        CharacterComicsResponse characterComicsResponse = new CharacterComicsResponse();
        if (characterComicsJson == null) {
            characterComicsResponse.setErrors(this.getEmptyJsonError());
            return characterComicsResponse;
        }

        JSONArray resultList = parseComicsJsonData(characterComicsJson);

        if (resultList == null) {
            characterComicsResponse.setErrors(this.getParsingError());
            return characterComicsResponse;
        }

        Iterator jsonDataItr = resultList.iterator();

        List<ComicDTO> charactersComicsList = new ArrayList<>();
        while (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            ComicDTO comic = createNewComicDTO(recordFromJson);

            charactersComicsList.add(comic);
        }

        if (charactersComicsList.isEmpty()) {
            characterComicsResponse.setErrors(this.getNotFoundError());
        } else {
            characterComicsResponse.setComics(charactersComicsList);
        }

        return characterComicsResponse;
    }

    public ComicResponse comicJsonToComicResponse(String comicJson) throws Exception {
        ComicResponse comicResponse = new ComicResponse();
        if (comicJson == null) {
            comicResponse.setErrors(this.getEmptyJsonError());
            return comicResponse;
        }

        JSONArray resultList = parseComicsJsonData(comicJson);

        if (resultList == null) {
            comicResponse.setErrors(this.getParsingError());
            return comicResponse;
        }

        Iterator jsonDataItr = resultList.iterator();

        if (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            ComicDTO comicDTO = createNewComicDTO(recordFromJson);

            comicResponse.setComic(comicDTO);
        }

        if (comicResponse.getComic() == null) {
            comicResponse.setErrors(this.getNotFoundError());
        }

        return comicResponse;
    }

    private ComicDTO createNewComicDTO(JSONObject recordFromJson) throws Exception {
        ComicDTO comic = new ComicDTO();
        try {
            comic.setTitle((String) recordFromJson.get("title"));
            comic.setIssueNumber(String.valueOf(recordFromJson.get("issueNumber")));
            comic.setVariantDescription((String) recordFromJson.get("variantDescription"));
            comic.setDescription((String) recordFromJson.get("description"));

            String modified = (String) recordFromJson.get("modified");
            comic.setModified(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ").parse(modified));

            comic.setIsbn((String) recordFromJson.get("isbn"));
            comic.setUpc((String) recordFromJson.get("upc"));
            comic.setDiamondCode((String) recordFromJson.get("diamondCode"));
            comic.setFormat((String) recordFromJson.get("format"));
            comic.setPageCount(String.valueOf(recordFromJson.get("pageCount")));

            Thumbnail comicThumbnail = new Thumbnail();
            JSONObject thumbnail = (JSONObject) recordFromJson.get("thumbnail");

            comicThumbnail.setExtension((String) thumbnail.get("extension"));
            comicThumbnail.setPath((String) thumbnail.get("path"));

            comic.setThumbnail(comicThumbnail);

            JSONArray jsonArray = parseCharactersJsonData(recordFromJson);
            comic.setCharacters(parseCharactersForComicDTO(jsonArray));
        } catch (java.text.ParseException e) {
            throw new Exception("Произошла ошибка при парсинге элемента ответа - " + e.getMessage());
        }
        return comic;
    }

    private List<String> parseCharactersForComicDTO(JSONArray resultList) {
        List<String> characters = new ArrayList<>();

        for (Object o : resultList) {
            JSONObject recordFromJson = (JSONObject) o;
            String name = (String) recordFromJson.get("name");
            characters.add(name);
        }
        return characters;
    }

    private JSONArray parseCharactersJsonData(JSONObject recordFromJson) {
        JSONArray resultList = null;

        if (recordFromJson.containsKey("characters")) {
            JSONObject dataFromResult = (JSONObject) recordFromJson.get("characters");
            if (dataFromResult.containsKey("items")) {
                resultList = (JSONArray) dataFromResult.get("items");
            }
        }

        return resultList;
    }

    private JSONArray parseComicsJsonData(String characterJson) throws Exception {
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

    public ComicDTO comicToComicDTO(Comic comic) {
        ComicDTO comicDTO = new ComicDTO();

        if (comic == null) {
            return null;
        }

        comicDTO.setTitle(comic.getTitle());
        comicDTO.setIssueNumber(comic.getIssueNumber());
        comicDTO.setVariantDescription(comic.getVariantDescription());
        comicDTO.setDescription(comic.getDescription());
        comicDTO.setModified(comic.getModified());
        comicDTO.setIsbn(comic.getIsbn());
        comicDTO.setUpc(comic.getUpc());
        comicDTO.setDiamondCode(comic.getDiamondCode());
        comicDTO.setFormat(comic.getFormat());
        comicDTO.setPageCount(comic.getPageCount());

        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setPath(comic.getThumbnail());
        String extension = FilenameUtils.getExtension(comic.getThumbnail());
        thumbnail.setExtension(extension);

        comicDTO.setThumbnail(thumbnail);

        return comicDTO;
    }
}
