package main.repositories;

import main.models.Character2Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Character2ComicRepository extends JpaRepository<Character2Comic, Integer> {
    @Query("From Character2Comic as c2c where c2c.characterId = :characterId")
    List<Character2Comic> findComicsForCharacter(@Param("characterId") int characterId);

    @Query("From Character2Comic as c2c where c2c.comicId = :comicId")
    List<Character2Comic> findCharactersForComic(@Param("comicId") int comicId);
}
