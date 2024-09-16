package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "tb_episodes")
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer season;
    private String title;
    private Integer number;
    private Double imdbRating;
    private LocalDate releaseDate;

    @ManyToOne
    private Series series;

    public Episode() {
    }

    public Episode(Long id, Integer season, String title, Integer number, Double imdbRating,
                   LocalDate releaseDate, Series series) {
        this.id = id;
        this.season = season;
        this.title = title;
        this.number = number;
        this.imdbRating = imdbRating;
        this.releaseDate = releaseDate;
        this.series = series;
    }

    public Episode(Integer season, EpisodeData episodeData) {
        this.season = season;
        this.title = episodeData.title();
        this.number = episodeData.episode();

        try {
            this.imdbRating = Double.valueOf(episodeData.imdbRating());
        } catch (NumberFormatException ex) {
            this.imdbRating = 0.0;
        }

        try {
            this.releaseDate = LocalDate.parse(episodeData.releaseDate());
        } catch (DateTimeParseException ex) {
            this.releaseDate = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "id=" + id +
                ", season=" + season +
                ", title='" + title + '\'' +
                ", number=" + number +
                ", imdbRating=" + imdbRating +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
