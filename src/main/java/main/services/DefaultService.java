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
        List<Character2Comic> comicsForCharacter = character2ComicRepository.findComicsForCharacter(character.getId());
        for (Character2Comic c2c: comicsForCharacter) {
            try {
                character2ComicRepository.delete(c2c);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public boolean deleteCharactersFromComic(Comic comic) {
        List<Character2Comic> comicsForCharacter = character2ComicRepository.findCharactersForComic(comic.getId());
        for (Character2Comic c2c: comicsForCharacter) {
            try {
                character2ComicRepository.delete(c2c);
            } catch (Exception e) {
                return false;
            }
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
            character2Comic.setCharacterId(character.getId());
            character2Comic.setComicId(comicByName.getId());

            try {
                character2ComicRepository.save(character2Comic);
            } catch (Exception e) {
                return false;
            }

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
            character2Comic.setCharacterId(characterByName.getId());
            character2Comic.setComicId(comic.getId());

            try {
                character2ComicRepository.save(character2Comic);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public List<String> findComicsForCharacterAndReturn(Character character) {
        List<String> comicsList = new ArrayList<>();

        if(character == null) {
            return comicsList;
        }

        List<Character2Comic> comicsForCharacter = character2ComicRepository.findComicsForCharacter(character.getId());

        if(comicsForCharacter == null) {
            return comicsList;
        }

        for (Character2Comic dependency:comicsForCharacter) {
            Comic byId = comicsRepository.getComicByID(dependency.getComicId());
            comicsList.add(byId.getTitle());
        }

        return comicsList;
    }

    public List<String> findCharactersForComicAndReturn(Comic comic) {
        List<String> charactersList = new ArrayList<>();

        if(comic == null) {
            return charactersList;
        }

        List<Character2Comic> charactersForComic = character2ComicRepository.findCharactersForComic(comic.getId());

        if(charactersForComic == null) {
            return charactersList;
        }

        for (Character2Comic dependency:charactersForComic) {
            Character characterByID = charactersRepository.getCharacterByID(dependency.getCharacterId());
            charactersList.add(characterByID.getName());
        }

        return charactersList;
    }
}
