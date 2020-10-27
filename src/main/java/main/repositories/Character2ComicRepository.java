package main.repositories;

import main.models.Character2Comic;
import main.models.Comic;
import main.models.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Character2ComicRepository extends JpaRepository<Character2Comic, Integer> {
    @Query("From Character2Comic as c2c where c2c.character = :character")
    List<Character2Comic> findComicsForCharacter(@Param("character") Character character);

    @Query("From Character2Comic as c2c where c2c.comic = :comic")
    List<Character2Comic> findCharactersForComic(@Param("comic") Comic comic);
}
