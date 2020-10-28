package main.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;

@Slf4j
@Service
public class AuthenticationService {
    @Value("${PublicApiKey}")
    private String publicApiKey;

    @Value("${PrivateApiKey}")
    private String privateApiKey;

    public String getUriForListOfCharacters(int offset,
                                            int limit,
                                            String sortBy,
                                            String name,
                                            String nameStartsWith,
                                            int comics) throws NoSuchAlgorithmException {
        HashMap<String, Object> mapWihParams = getAuthenticationHash();

        boolean isFiltered = false;
        String uri = "http://gateway.marvel.com/v1/public/characters?";
        if (name != null) {
            uri = uri + "name=" + name;
            isFiltered = true;
        }
        if (nameStartsWith != null) {
            uri = uri + (!isFiltered ? "nameStartsWith=" + nameStartsWith : "&nameStartsWith=" + nameStartsWith);
            isFiltered = true;
        }
        if (comics != 0) {
            uri = uri + (!isFiltered ? "comics=" + comics : "&comics=" + comics);
            isFiltered = true;
        }
        uri = uri +
                (!isFiltered ? "orderBy=" : "&orderBy=") + sortBy +
                "&limit=" + limit +
                "&offset=" + offset +
                "&ts=" + mapWihParams.get("time") +
                "&apikey=" + mapWihParams.get("apikey") +
                "&hash=" + mapWihParams.get("hash");

        return uri;
    }

    public String getUriForListOfCharacterComics(int id,
                                                 int offset,
                                                 int limit,
                                                 String sortBy,
                                                 String format,
                                                 String title,
                                                 String diamondCode) throws NoSuchAlgorithmException {
        HashMap<String, Object> mapWihParams = getAuthenticationHash();

        boolean isFiltered = false;
        String uri = "http://gateway.marvel.com/v1/public/characters/" + id + "/comics?";

        if (format != null) {
            uri = uri + "format=" + format;
            isFiltered = true;
        }
        if (title != null) {
            uri = uri + (!isFiltered ? "title=" + title : "&title=" + title);
            isFiltered = true;
        }
        if (diamondCode != null) {
            uri = uri + (!isFiltered ? "diamondCode=" + diamondCode : "&diamondCode=" + diamondCode);
            isFiltered = true;
        }
        uri = uri +
                (!isFiltered ? "orderBy=" : "&orderBy=") + sortBy +
                "&limit=" + limit +
                "&offset=" + offset +
                "&ts=" + mapWihParams.get("time") +
                "&apikey=" + mapWihParams.get("apikey") +
                "&hash=" + mapWihParams.get("hash");

        return uri;
    }

    public String getUriForListOfComics(int offset,
                                        int limit,
                                        String sortBy,
                                        String format,
                                        String title,
                                        String diamondCode) throws NoSuchAlgorithmException {
        HashMap<String, Object> mapWihParams = getAuthenticationHash();

        boolean isFiltered = false;
        String uri = "http://gateway.marvel.com/v1/public/comics?";

        if (format != null) {
            uri = uri + "format=" + format;
            isFiltered = true;
        }
        if (title != null) {
            uri = uri + (!isFiltered ? "title=" + title : "&title=" + title);
            isFiltered = true;
        }
        if (diamondCode != null) {
            uri = uri + (!isFiltered ? "diamondCode=" + diamondCode : "&diamondCode=" + diamondCode);
            isFiltered = true;
        }
        uri = uri +
                (!isFiltered ? "orderBy=" : "&orderBy=") + sortBy +
                "&limit=" + limit +
                "&offset=" + offset +
                "&ts=" + mapWihParams.get("time") +
                "&apikey=" + mapWihParams.get("apikey") +
                "&hash=" + mapWihParams.get("hash");

        return uri;
    }


    public String getUriForCharacter(int id) throws NoSuchAlgorithmException {
        HashMap<String, Object> mapWihParams = getAuthenticationHash();

        final String uri = "http://gateway.marvel.com/v1/public/characters/" + id + "?ts=" + mapWihParams.get("time") +
                "&apikey=" + mapWihParams.get("apikey") + "&hash=" + mapWihParams.get("hash");

        return uri;
    }

    public String getUriForComic(int id) throws NoSuchAlgorithmException {
        HashMap<String, Object> mapWihParams = getAuthenticationHash();

        final String uri = "http://gateway.marvel.com/v1/public/comics/" + id + "?ts=" + mapWihParams.get("time") +
                "&apikey=" + mapWihParams.get("apikey") + "&hash=" + mapWihParams.get("hash");

        return uri;
    }

    public String getUriForComicCharacters(int id,
                                           int offset,
                                           int limit,
                                           String sortBy,
                                           String name,
                                           String nameStartsWith) throws NoSuchAlgorithmException {
        HashMap<String, Object> mapWihParams = getAuthenticationHash();

        boolean isFiltered = false;
        String uri = "http://gateway.marvel.com/v1/public/comics/" + id + "/characters?";

        if (name != null) {
            uri = uri + "name=" + name;
            isFiltered = true;
        }
        if (nameStartsWith != null) {
            uri = uri + (!isFiltered ? "nameStartsWith=" + nameStartsWith : "&nameStartsWith=" + nameStartsWith);
            isFiltered = true;
        }

        uri = uri +
                (!isFiltered ? "orderBy=" : "&orderBy=") + sortBy +
                "&limit=" + limit +
                "&offset=" + offset +
                "&ts=" + mapWihParams.get("time") +
                "&apikey=" + mapWihParams.get("apikey") +
                "&hash=" + mapWihParams.get("hash");

        return uri;
    }

    public HashMap<String, Object> getAuthenticationHash() throws NoSuchAlgorithmException {

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        String hash = ts.getTime() + this.privateApiKey + this.publicApiKey;

        MessageDigest md;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Ошибка инициализации алгоритма шифрования - " + e.getMessage());
        }

        md.update(hash.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter
                .printHexBinary(digest).toLowerCase();

        HashMap<String, Object> mapWithParams = new HashMap<>();
        mapWithParams.put("time", ts.getTime());
        mapWithParams.put("apikey", this.publicApiKey);
        mapWithParams.put("hash", myHash);

        return mapWithParams;
    }
}
