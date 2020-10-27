package main.repositories;

import main.models.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CharactersRepository extends JpaRepository<Character, Integer> {
    @Query("From Character as c where c.name = :name")
    Character getCharacterByName(@Param("name") String name);
}
