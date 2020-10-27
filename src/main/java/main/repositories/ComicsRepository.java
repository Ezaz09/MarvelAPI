package main.repositories;

import main.models.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComicsRepository extends JpaRepository<Comic, Integer> {
    @Query("From Comic as c where c.title = :title")
    Comic getComicByTitle(@Param("title") String title);
}
