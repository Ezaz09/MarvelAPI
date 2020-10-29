package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.comics.AddNewComicRequest;
import main.api.requests.comics.EditComicRequest;
import main.api.responses.characters.CharacterComicsResponse;
import main.api.responses.comics.*;
import main.mappers.ComicsMapper;
import main.models.Comic;
import main.repositories.Character2ComicRepository;
import main.repositories.CharactersRepository;
import main.repositories.ComicsRepository;
import org.springframework.data.domain.Pageable;
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
public class ComicsService extends DefaultService {

    public ComicsService(ImageService imageService, CharactersRepository charactersRepository, ComicsRepository comicsRepository, Character2ComicRepository character2ComicRepository) {
        super(imageService, charactersRepository, comicsRepository, character2ComicRepository);
    }

    public ResponseEntity<ListOfComicsResponse> getComics(String uri) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String result;
        ListOfComicsResponse listOfComicsResponse = new ListOfComicsResponse();

        try {
            result = restTemplate.getForObject(uri, String.class);
        } catch (HttpClientErrorException e) {
            HashMap<String, String> errors = new HashMap<>();
            listOfComicsResponse.setErrors(errors);
            if (e.getRawStatusCode() == 409) {
                errors.put("Запрос не может быть выполнен из-за конфликтного обращения к ресурсу", e.getMessage());
                return new ResponseEntity<>(listOfComicsResponse, HttpStatus.NOT_FOUND);
            } else {
                errors.put("Ошибка сервера", e.getMessage());
                return new ResponseEntity<>(listOfComicsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        listOfComicsResponse = new ComicsMapper().comicsJsonToComicsResponse(result);

        if (listOfComicsResponse.getErrors() != null) {
            if (listOfComicsResponse.getErrors().get("Ошибка парсинга") != null) {
                return new ResponseEntity<>(listOfComicsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(listOfComicsResponse, HttpStatus.NOT_FOUND);
            }

        }

        return new ResponseEntity<>(listOfComicsResponse, HttpStatus.OK);
    }

    public ResponseEntity<ComicResponse> getComicByID(String uri) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String result;
        try {
            result = restTemplate.getForObject(uri, String.class);
        } catch (HttpClientErrorException e) {
            ComicResponse comicResponse = new ComicResponse();
            HashMap<String, String> errors = new HashMap<>();
            comicResponse.setErrors(errors);
            if (e.getRawStatusCode() == 404) {
                errors.put("Результат запроса", "Ответ на запрос пустой!");
                return new ResponseEntity<>(comicResponse, HttpStatus.NOT_FOUND);
            } else {
                errors.put("Ошибка сервера", e.getMessage());
                return new ResponseEntity<>(comicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        ComicResponse comic = new ComicsMapper().comicJsonToComicResponse(result);

        if (comic.getErrors() != null) {
            if (comic.getErrors().get("Ошибка парсинга") != null) {
                return new ResponseEntity<>(comic, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(comic, HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(comic, HttpStatus.OK);
    }

    public ResponseEntity<CharacterComicsResponse> getCharacterComics(String uri) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String result;

        try {
            result = restTemplate.getForObject(uri, String.class);
        } catch (HttpClientErrorException e) {
            CharacterComicsResponse characterComicsResponse = new CharacterComicsResponse();
            HashMap<String, String> errors = new HashMap<>();
            characterComicsResponse.setErrors(errors);
            if (e.getRawStatusCode() == 409) {
                errors.put("Запрос не может быть выполнен из-за конфликтного обращения к ресурсу", e.getMessage());
                return new ResponseEntity<>(characterComicsResponse, HttpStatus.NOT_FOUND);
            } else {
                errors.put("Ошибка сервера", e.getMessage());
                return new ResponseEntity<>(characterComicsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        CharacterComicsResponse characterComicsResponse = new ComicsMapper().characterComicsJsonToCharacterComicsResponse(result);

        if (characterComicsResponse.getErrors() != null) {
            if (characterComicsResponse.getErrors().get("Ошибка парсинга") != null) {
                return new ResponseEntity<>(characterComicsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(characterComicsResponse, HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(characterComicsResponse, HttpStatus.OK);
    }

    public ResponseEntity<AddNewComicResponse> addNewComic(AddNewComicRequest comicRequest) {
        AddNewComicResponse addNewComicResponse = new AddNewComicResponse();
        if (comicRequest == null) {
            addNewComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Запрос", "Запрос на добавление нового комикса пустой!");
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

        Comic comicByTitle = this.getComicsRepository().getComicByTitle(comicRequest.getTitle());

        if (comicByTitle != null) {
            addNewComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Комикс", "Добавляемый комикс был найден в базе данных!");
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

        try {
            this.getComicsRepository().save(comic);
        } catch (Exception e) {
            addNewComicResponse.setResult(false);
            HashMap<String, String> error = new HashMap<>();
            error.put("Ошибка при сохранении нового комикса", e.getMessage());
            addNewComicResponse.setErrors(error);
            return new ResponseEntity<>(addNewComicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        if (comicRequest.getCharacters() != null) {
            boolean resultOfSave = this.findCharactersForComicAndSaveDependencies(comicRequest.getCharacters(),
                    comic);
            if (!resultOfSave) {
                addNewComicResponse.setResult(false);
                HashMap<String, String> error = new HashMap<>();
                error.put("Ошибка при сохранении ссылок на персонажей для нового комикса", "Название комикса - " + comicRequest.getTitle());
                addNewComicResponse.setErrors(error);
                return new ResponseEntity<>(addNewComicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        addNewComicResponse.setResult(true);

        return new ResponseEntity<>(addNewComicResponse, HttpStatus.OK);
    }

    private HashMap<String, Object> checkParamsFromComicRequest(AddNewComicRequest comicRequest) {
        HashMap<String, Object> structureOfResult = new HashMap<>();
        boolean result = true;
        String errors = "";

        if (comicRequest.getTitle() == null ||
                comicRequest.getTitle().length() > 255 ||
                comicRequest.getTitle().equals("")) {
            result = false;
            errors = errors + "Проверьте заголовок комикса! \n ";
        }

        if (comicRequest.getIssueNumber() == null ||
                comicRequest.getIssueNumber().length() > 255 ||
                comicRequest.getIssueNumber().equals("")) {
            result = false;
            errors = errors + "Проверьте номер выпуска комикса! \n ";
        }

        if (comicRequest.getVariantDescription() == null ||
                comicRequest.getVariantDescription().length() > 255 ||
                comicRequest.getVariantDescription().equals("")) {
            result = false;
            errors = errors + "Проверьте описание выпуска комикса! \n ";
        }

        if (comicRequest.getDescription() == null ||
                comicRequest.getDescription().length() > 255 ||
                comicRequest.getDescription().equals("")) {
            result = false;
            errors = errors + "Проверьте описание комикса! \n ";
        }

        if (comicRequest.getIsbn() == null ||
                comicRequest.getIsbn().length() > 255 ||
                comicRequest.getIsbn().equals("")) {
            result = false;
            errors = errors + "Проверьте ISBN номер комикса! \n ";
        }

        if (comicRequest.getUpc() == null ||
                comicRequest.getUpc().length() > 255 ||
                comicRequest.getUpc().equals("")) {
            result = false;
            errors = errors + "Проверьте UPC номер комикса! \n ";
        }

        if (comicRequest.getDiamondCode() == null ||
                comicRequest.getDiamondCode().length() > 255 ||
                comicRequest.getDiamondCode().equals("")) {
            result = false;
            errors = errors + "Проверьте diamond code комикса! \n ";
        }

        if (comicRequest.getFormat() == null ||
                comicRequest.getFormat().length() > 255 ||
                comicRequest.getFormat().equals("")) {
            result = false;
            errors = errors + "Проверьте имя формата комикса! \n ";
        }

        if (comicRequest.getPageCount() == null ||
                comicRequest.getPageCount().length() > 255 ||
                comicRequest.getPageCount().equals("")) {
            result = false;
            errors = errors + "Проверьте заданное количество страниц комикса! \n ";
        }

        structureOfResult.put("result", result);
        structureOfResult.put("errors", errors);
        return structureOfResult;
    }

    private HashMap<String, Object> checkParamsFromEditComicRequest(EditComicRequest comicRequest) {
        HashMap<String, Object> structureOfResult = new HashMap<>();
        boolean result = true;
        String errors = "";

        if (comicRequest.getTitle() == null ||
                comicRequest.getTitle().length() > 255 ||
                comicRequest.getTitle().equals("")) {
            result = false;
            errors = errors + "Проверьте заголовок комикса! \n ";
        }

        if (comicRequest.getIssueNumber() == null ||
                comicRequest.getIssueNumber().length() > 255 ||
                comicRequest.getIssueNumber().equals("")) {
            result = false;
            errors = errors + "Проверьте номер выпуска комикса! \n ";
        }

        if (comicRequest.getVariantDescription() == null ||
                comicRequest.getVariantDescription().length() > 255 ||
                comicRequest.getVariantDescription().equals("")) {
            result = false;
            errors = errors + "Проверьте описание выпуска комикса! \n ";
        }

        if (comicRequest.getDescription() == null ||
                comicRequest.getDescription().length() > 255 ||
                comicRequest.getDescription().equals("")) {
            result = false;
            errors = errors + "Проверьте описание комикса! \n ";
        }

        if (comicRequest.getIsbn() == null ||
                comicRequest.getIsbn().length() > 255 ||
                comicRequest.getIsbn().equals("")) {
            result = false;
            errors = errors + "Проверьте ISBN номер комикса! \n ";
        }

        if (comicRequest.getUpc() == null ||
                comicRequest.getUpc().length() > 255 ||
                comicRequest.getUpc().equals("")) {
            result = false;
            errors = errors + "Проверьте UPC номер комикса! \n ";
        }

        if (comicRequest.getDiamondCode() == null ||
                comicRequest.getDiamondCode().length() > 255 ||
                comicRequest.getDiamondCode().equals("")) {
            result = false;
            errors = errors + "Проверьте diamond code комикса! \n ";
        }

        if (comicRequest.getFormat() == null ||
                comicRequest.getFormat().length() > 255 ||
                comicRequest.getFormat().equals("")) {
            result = false;
            errors = errors + "Проверьте имя формата комикса! \n ";
        }

        if (comicRequest.getPageCount() == null ||
                comicRequest.getPageCount().length() > 255 ||
                comicRequest.getPageCount().equals("")) {
            result = false;
            errors = errors + "Проверьте заданное количество страниц комикса! \n ";
        }

        structureOfResult.put("result", result);
        structureOfResult.put("errors", errors);
        return structureOfResult;
    }

    public ResponseEntity<ListOfComicsResponse> getComicsFromDB(Pageable pg,
                                                                String title,
                                                                String format,
                                                                String diamondCode) {
        List<Comic> comicList = new ArrayList<>();
        ListOfComicsResponse listOfComicsResponse = new ListOfComicsResponse();
        boolean queryWas = false;

        if(title != null) {
            Comic comicByTitle = this.getComicsRepository().getComicByTitle(title);
            comicList.add(comicByTitle);
            queryWas = true;
        }

        if(format != null) {
            if (comicList.isEmpty()) {
                comicList = this.getComicsRepository().getComicsByFormat(pg, format);
            } else {
                comicList = this.getComicsRepository().getComicsByFormatAndListOfComics(pg, format, comicList);
            }
            queryWas = true;
        }

        if(diamondCode != null) {
            if (comicList.isEmpty()) {
                comicList = this.getComicsRepository().getComicsByDiamondCode(pg, diamondCode);
            } else {
                comicList = this.getComicsRepository().getComicsByDiamondCodeAndListOfComics(pg, diamondCode, comicList);
            }
            queryWas = true;
        }

        if(!queryWas) {
            comicList = this.getComicsRepository().findAll(pg).getContent();
        }

        if (comicList.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Результат запроса", "Ответ на запрос пустой!");
            listOfComicsResponse.setErrors(errors);
            return new ResponseEntity<>(listOfComicsResponse, HttpStatus.NOT_FOUND);
        }

        List<ComicDTO> comicDTOList = new ArrayList<>();

        for (Comic comic : comicList) {
            ComicDTO comicDTO = new ComicsMapper().comicToComicDTO(comic);

            if (comicDTO == null) {
                continue;
            }

            List<String> charactersForComic = this.findCharactersForComicAndReturn(comic);
            comicDTO.setCharacters(charactersForComic);
            comicDTOList.add(comicDTO);
        }

        if(comicDTOList.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Результат запроса", "Ответ на запрос пустой!");
            listOfComicsResponse.setErrors(errors);
            return new ResponseEntity<>(listOfComicsResponse, HttpStatus.NOT_FOUND);
        }

        listOfComicsResponse.setComics(comicDTOList);
        return new ResponseEntity<>(listOfComicsResponse, HttpStatus.OK);
    }

    public ResponseEntity<EditComicResponse> editComic(EditComicRequest editComicRequest) {
        EditComicResponse editComicResponse = new EditComicResponse();

        if (editComicRequest == null) {
            editComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Запрос", "Запрос на редактирование комикса пустой!");
            editComicResponse.setErrors(errors);
            return new ResponseEntity<>(editComicResponse, HttpStatus.BAD_REQUEST);
        }

        HashMap<String, Object> structureOfCheck = checkParamsFromEditComicRequest(editComicRequest);
        boolean result = (boolean) structureOfCheck.get("result");

        if (!result) {
            editComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Ошибки", (String) structureOfCheck.get("errors"));
            editComicResponse.setErrors(errors);
            return new ResponseEntity<>(editComicResponse, HttpStatus.BAD_REQUEST);
        }

        Comic comicByTitle = this.getComicsRepository().getComicByTitle(editComicRequest.getTitle());

        if (comicByTitle == null) {
            editComicResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("Комикс", "Комикс не был найден!");
            editComicResponse.setErrors(errors);
            return new ResponseEntity<>(editComicResponse, HttpStatus.NOT_FOUND);
        }

        comicByTitle.setIssueNumber(editComicRequest.getIssueNumber());
        comicByTitle.setDescription(editComicRequest.getDescription());
        comicByTitle.setVariantDescription(editComicRequest.getVariantDescription());
        comicByTitle.setIsbn(editComicRequest.getIsbn());
        comicByTitle.setModified(new Date());
        comicByTitle.setUpc(editComicRequest.getUpc());
        comicByTitle.setDiamondCode(editComicRequest.getDiamondCode());
        comicByTitle.setFormat(editComicRequest.getFormat());
        comicByTitle.setPageCount(editComicRequest.getPageCount());

        if (comicByTitle.getThumbnail() != null) {
            this.getImageService().deleteUserPhotoFromServer(comicByTitle.getThumbnail());
        }

        if (editComicRequest.getThumbnail() == null) {
            comicByTitle.setThumbnail("");
        } else {
            String pathToPhoto = this.getImageService().savePhoto(editComicRequest.getThumbnail(), "Comic");
            if (pathToPhoto != null) {
                comicByTitle.setThumbnail(pathToPhoto);
            }
        }

        try {
            this.getComicsRepository().save(comicByTitle);
        } catch (Exception e) {
            editComicResponse.setResult(false);
            HashMap<String, String> error = new HashMap<>();
            error.put("Ошибка при сохранении изменений комикса", e.getMessage());
            editComicResponse.setErrors(error);
            return new ResponseEntity<>(editComicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        if (editComicRequest.getCharacters() != null) {
            boolean resultOfDeleting = this.deleteCharactersFromComic(comicByTitle);
            if (!resultOfDeleting) {
                editComicResponse.setResult(false);
                HashMap<String, String> error = new HashMap<>();
                error.put("Ошибка при удалении ссылок на персонажей из комикса", "Название комикса - " + editComicRequest.getTitle());
                editComicResponse.setErrors(error);
                return new ResponseEntity<>(editComicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            boolean resultOfSave = this.findCharactersForComicAndSaveDependencies(editComicRequest.getCharacters(),
                    comicByTitle);
            if (!resultOfSave) {
                editComicResponse.setResult(false);
                HashMap<String, String> error = new HashMap<>();
                error.put("Ошибка при сохранении ссылок на персонажей для комикса", "Название комикса - " + editComicRequest.getTitle());
                editComicResponse.setErrors(error);
                return new ResponseEntity<>(editComicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        editComicResponse.setResult(true);

        return new ResponseEntity<>(editComicResponse, HttpStatus.OK);
    }
}
