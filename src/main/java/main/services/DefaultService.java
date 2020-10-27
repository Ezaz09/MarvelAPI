package main.services;

import main.models.Character;
import main.models.Character2Comic;
import main.models.Comic;
import main.repositories.Character2ComicRepository;
import main.repositories.CharactersRepository;
import main.repositories.ComicsRepository;

import java.util.ArrayList;
import java.util.List;

public class DefaultService {
    private final ImageService imageService;
    private final CharactersRepository charactersRepository;
    private final ComicsRepository comicsRepository;
    private final Character2ComicRepository character2ComicRepository;

    public DefaultService(ImageService imageService,
                          CharactersRepository charactersRepository,
                          ComicsRepository comicsRepository,
                          Character2ComicRepository character2ComicRepository) {
        this.imageService = imageService;
        this.charactersRepository = charactersRepository;
        this.comicsRepository = comicsRepository;
        this.character2ComicRepository = character2ComicRepository;
    }

    public ImageService getImageService() {
        return imageService;
    }

    public CharactersRepository getCharactersRepository() {
        return charactersRepository;
    }

    public ComicsRepository getComicsRepository() {
        return comicsRepository;
    }

    public boolean deleteCharacterFromComics(Character character) {
        List<Character2Comic> comicsForCharacter = character2ComicRepository.findComicsForCharacter(character);
        for (Character2Comic c2c: comicsForCharacter) {
            character2ComicRepository.delete(c2c);
        }
        return true;
    }

    public boolean findComicsForCharacterAndSaveDependencies(List<String> comics,
                                                             Character character) {
        for (String comic : comics) {
            Comic comicByName = this.getComicsRepository().getComicByTitle(comic);

            if(comicByName == null) {
                continue;
            }

            Character2Comic character2Comic = new Character2Comic();
            character2Comic.setCharacter(character);
            character2Comic.setComic(comicByName);

            Character2Comic save = character2ComicRepository.save(character2Comic);
        }
        return true;
    }

    public boolean findCharactersForComicAndSaveDependencies(List<String> characters,
                                                             Comic comic) {
        for (String character : characters) {
            Character characterByName = this.getCharactersRepository().getCharacterByName(character);

            if(characterByName == null) {
                continue;
            }

            Character2Comic character2Comic = new Character2Comic();
            character2Comic.setCharacter(characterByName);
            character2Comic.setComic(comic);

            Character2Comic save = character2ComicRepository.save(character2Comic);
        }
        return true;
    }

    public List<String> findComicsForCharacterAndReturn(Character character) {
        List<String> comicsList = new ArrayList<>();

        if(character == null) {
            return comicsList;
        }

        List<Character2Comic> comicsForCharacter = character2ComicRepository.findComicsForCharacter(character);

        if(comicsForCharacter == null) {
            return comicsList;
        }

        for (Character2Comic dependency:comicsForCharacter) {
            comicsList.add(dependency.getComic().getTitle());
        }

        return comicsList;
    }
}
