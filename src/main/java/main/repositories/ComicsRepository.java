package main.repositories;

import main.models.Comic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComicsRepository extends JpaRepository<Comic, Integer> {
    @Query("From Comic as c where c.title = :title")
    Comic getComicByTitle(@Param("title") String title);

    @Query("From Comic as c where c.id = :id")
    Comic getComicByID(@Param("id")int id);

    @Query("From Comic as c where c.format = :format")
    List<Comic> getComicsByFormat(Pageable pg, @Param("format") String format);

    @Query("From Comic as c where c.format = :format AND c IN (:comics)")
    List<Comic> getComicsByFormatAndListOfComics(Pageable pg, @Param("format") String format, @Param("comics") List<Comic> comics);

    @Query("From Comic as c where c.diamondCode = :diamondCode")
    List<Comic> getComicsByDiamondCode(Pageable pg, @Param("diamondCode") String diamondCode);

    @Query("From Comic as c where c.diamondCode = :diamondCode AND c IN (:comics)")
    List<Comic> getComicsByDiamondCodeAndListOfComics(Pageable pg, @Param("diamondCode") String diamondCode, @Param("comics") List<Comic> comics);
}
