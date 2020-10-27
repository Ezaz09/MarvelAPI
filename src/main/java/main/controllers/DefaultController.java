package main.controllers;

import main.services.AuthenticationService;
import main.services.CharactersService;
import main.services.ComicsService;

import java.util.ArrayList;
import java.util.List;


public class DefaultController {
    private final CharactersService charactersService;
    private final ComicsService comicsService;
    private final AuthenticationService authenticationService;

    private final List<String> listOfParamsOfSortForComics = new ArrayList<>();
    private final List<String> listOfParamsOfSortForCharacters = new ArrayList<>();
    private final List<String> listOfComicsFormats = new ArrayList<>();

    public DefaultController(ComicsService comicsService,
                             AuthenticationService authenticationService,
                             CharactersService charactersService) {
        this.charactersService = charactersService;
        this.comicsService = comicsService;
        this.authenticationService = authenticationService;

        this.listOfParamsOfSortForCharacters.add("name");
        this.listOfParamsOfSortForCharacters.add("modified");
        this.listOfParamsOfSortForCharacters.add("-name");
        this.listOfParamsOfSortForCharacters.add("-modified");

        this.listOfParamsOfSortForComics.add("focDate");
        this.listOfParamsOfSortForComics.add("onsaleDate");
        this.listOfParamsOfSortForComics.add("title");
        this.listOfParamsOfSortForComics.add("issueNumber");
        this.listOfParamsOfSortForComics.add("modified");
        this.listOfParamsOfSortForComics.add("-focDate");
        this.listOfParamsOfSortForComics.add("-onsaleDate");
        this.listOfParamsOfSortForComics.add("-title");
        this.listOfParamsOfSortForComics.add("-issueNumber");
        this.listOfParamsOfSortForComics.add("-modified");

        this.listOfComicsFormats.add("comic");
        this.listOfComicsFormats.add("magazine");
        this.listOfComicsFormats.add("trade paperback");
        this.listOfComicsFormats.add("hardcover");
        this.listOfComicsFormats.add("digest");
        this.listOfComicsFormats.add("graphic novel");
        this.listOfComicsFormats.add("digital comic");
        this.listOfComicsFormats.add("infinite comic");
    }

    public CharactersService getCharactersService() {
        return charactersService;
    }

    public ComicsService getComicsService() {
        return comicsService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public List<String> getListOfParamsOfSortForComics() {
        return listOfParamsOfSortForComics;
    }

    public List<String> getListOfParamsOfSortForCharacters() {
        return listOfParamsOfSortForCharacters;
    }

    public List<String> getListOfComicsFormats() {
        return listOfComicsFormats;
    }
}
