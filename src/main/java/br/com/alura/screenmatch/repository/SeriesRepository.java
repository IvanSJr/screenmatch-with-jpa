package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Category;
import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String title);

    List<Series> findByActorsContainingIgnoreCase(String authorName);

    List<Series> findTop5ByOrderByImdbRatingDesc();

    @Query(
        value = "SELECT s FROM Series AS s WHERE s.totalSeasons < :totalSeasons AND s.imdbRating >= :imdbRating"
    )
    List<Series> findByTotalSeasonsAndImdbRating(Integer totalSeasons, Double imdbRating);

    List<Series> findByGenre(Category genre);

    @Query(
        value = "SELECT e FROM Series s JOIN s.episodes e WHERE e.title ILIKE CONCAT('%', :textSearch ,'%')"
    )
    List<Episode> findEpisodesByText(String textSearch);

    @Query(
        value = "SELECT e FROM Series s JOIN s.episodes e WHERE s = :series ORDER BY e.imdbRating DESC LIMIT 5"
    )
    List<Episode> findTopFiveEpsToSeries(Series series);

    @Query(
            value = "SELECT " +
                    "e " +
                    "FROM Series s " +
                    "JOIN s.episodes e " +
                    "WHERE s = :series " +
                    "AND YEAR(e.releaseDate) >= :releaseDateYearLimit " +
                    "ORDER BY e.releaseDate DESC"
    )
    List<Episode> findEpsToSeriesByReleasedDate(Series series, Integer releaseDateYearLimit);
}
