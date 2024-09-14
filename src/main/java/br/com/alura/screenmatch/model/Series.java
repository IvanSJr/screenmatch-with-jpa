package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.ChatGPTApiConsumerService;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.OptionalDouble;

public class Series {
    private String title;
    private Integer totalSeasons;
    private Double imdbRating;
    private Category genre;
    private String actors;
    private String poster;
    private String summary;

    public Series(SeriesData seriesData) {
        this.title = seriesData.title();
        this.totalSeasons = seriesData.totalSeasons();
        this.imdbRating = OptionalDouble.of(
            Double.valueOf(seriesData.imdbRating())
        ).orElse(0.0);
        genre = Category.fromString(seriesData.genre().split(",")[0].trim());
        this.actors = seriesData.actors();
        this.poster = seriesData.poster();
        this.summary = ChatGPTApiConsumerService.getTranslateToChatGPT(seriesData.summary()).trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }

    public void setTotalSeasons(Integer totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Category getGenre() {
        return genre;
    }

    public void setGenre(Category genre) {
        this.genre = genre;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "genre=" + genre +
            ", title='" + title + '\'' +
            ", totalSeasons=" + totalSeasons +
            ", imdbRating=" + imdbRating +
            ", actors='" + actors + '\'' +
            ", poster='" + poster + '\'' +
            ", summary='" + summary + '\'';
    }
}
