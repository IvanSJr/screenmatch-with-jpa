package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.ChatGPTApiConsumerService;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "tb_series")
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(name = "total_seasons")
    private Integer totalSeasons;

    @Column(name = "imdb_rating")
    private Double imdbRating;

    @Enumerated(EnumType.STRING)
    private Category genre;

    private String actors;
    private String poster;

    @Column(name = "summary", length = 2048)
    private String summary;

    @OneToMany(mappedBy = "series", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Episode> episodes = new ArrayList<>();

    public Series() {
    }

    public Series(Long id, String title, Integer totalSeasons, Double imdbRating, Category genre, String actors,
                  String poster, String summary, List<Episode> episodes) {
        this.id = id;
        this.title = title;
        this.totalSeasons = totalSeasons;
        this.imdbRating = imdbRating;
        this.genre = genre;
        this.actors = actors;
        this.poster = poster;
        this.summary = summary;
        this.episodes = episodes;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        episodes.forEach(
            episode -> episode.setSeries(this)
        );
        this.episodes = episodes;
    }

    @Override
    public String toString() {
        return "Series{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", totalSeasons=" + totalSeasons +
                ", imdbRating=" + imdbRating +
                ", genre=" + genre +
                ", actors='" + actors + '\'' +
                ", poster='" + poster + '\'' +
                ", summary='" + summary + '\'' +
                ", episodes=" + episodes +
                '}';
    }
}
