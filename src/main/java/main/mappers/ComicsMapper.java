package main.mappers;

import main.api.responses.characters.CharacterComicsResponse;
import main.api.responses.comics.ComicDTO;
import main.api.responses.comics.ComicResponse;
import main.api.responses.comics.ListOfComicsResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComicsMapper {

    public ListOfComicsResponse comicsJsonToComicsResponse(String comicsJson) throws ParseException, java.text.ParseException {
        if (comicsJson == null) {
            return new ListOfComicsResponse();
        }

        JSONArray resultList = parseComicsJsonData(comicsJson);

        if (resultList == null) {
            return new ListOfComicsResponse();
        }

        Iterator jsonDataItr = resultList.iterator();

        List<ComicDTO> comicsList = new ArrayList<>();
        while (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            ComicDTO comic = createNewComicDTO(recordFromJson);

            comicsList.add(comic);
        }
        ListOfComicsResponse listOfComics = new ListOfComicsResponse();
        listOfComics.setComics(comicsList);

        return listOfComics;
    }

    public CharacterComicsResponse characterComicsJsonToCharacterComicsResponse(String characterComicsJson) throws ParseException, java.text.ParseException {
        if (characterComicsJson == null) {
            return new CharacterComicsResponse();
        }

        JSONArray resultList = parseComicsJsonData(characterComicsJson);

        if (resultList == null) {
            return new CharacterComicsResponse();
        }

        Iterator jsonDataItr = resultList.iterator();

        List<ComicDTO> charactersComicsList = new ArrayList<>();
        while (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            ComicDTO comic = createNewComicDTO(recordFromJson);

            charactersComicsList.add(comic);
        }

        CharacterComicsResponse characterComicsResponse = new CharacterComicsResponse();
        characterComicsResponse.setComics(charactersComicsList);
        return characterComicsResponse;
    }

    public ComicResponse comicJsonToComicResponse(String comicJson) throws ParseException, java.text.ParseException {
        if(comicJson == null) {
            return new ComicResponse();
        }

        JSONArray resultList = parseComicsJsonData(comicJson);

        if(resultList == null) {
            return new ComicResponse();
        }

        Iterator jsonDataItr = resultList.iterator();

        ComicResponse comicResponse = new ComicResponse();
        if (jsonDataItr.hasNext()) {
            JSONObject recordFromJson = (JSONObject) jsonDataItr.next();

            ComicDTO comicDTO = createNewComicDTO(recordFromJson);

            comicResponse.setComic(comicDTO);
        }
        return comicResponse;
    }

    private ComicDTO createNewComicDTO(JSONObject recordFromJson) throws java.text.ParseException {
        ComicDTO comic = new ComicDTO();
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

        return comic;
    }

    private JSONArray parseComicsJsonData(String characterJson) throws ParseException {
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

}
