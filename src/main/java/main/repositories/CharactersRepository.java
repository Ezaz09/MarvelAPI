package main.repositories;

import main.models.Character;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharactersRepository extends JpaRepository<Character, Integer> {
    @Query("From Character as c where c.name = :name")
    Character getCharacterByName(@Param("name") String name);

    @Query("From Character as c where c.id = :id")
    Character getCharacterByID(@Param("id")int id);

    @Query("From Character as c where c.name LIKE :name% ")
    List<Character> getCharactersByNameStartsWithPlusPageable(Pageable pg, @Param("name") String name);
}
