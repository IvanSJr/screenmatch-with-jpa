package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodeDTO;
import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @GetMapping
    public ResponseEntity<List<SeriesDTO>> getAllSeries() {
        return ResponseEntity.ok().body(seriesService.findAll());
    }

    @GetMapping("/top5")
    public ResponseEntity<List<SeriesDTO>> getTop5Series() {
        return ResponseEntity.ok().body(seriesService.findTopFiveSeries());
    }

    @GetMapping("/lancamentos")
    public ResponseEntity<List<SeriesDTO>> getSeriesByReleaseDate() {
        return ResponseEntity.ok().body(seriesService.findSeriesToReleasedDateEpisode());
    }

    @GetMapping("/categoria/{genre}")
    public ResponseEntity<List<SeriesDTO>> getSeriesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok().body(seriesService.findSeriesByGenre(genre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesDTO> getSeriesByReleaseDate(@PathVariable Long id) {
        return ResponseEntity.ok().body(seriesService.findSeriesById(id));
    }

    @GetMapping("/{id}/temporadas/todas")
    public ResponseEntity<List<EpisodeDTO>> getEpisodesBySeriesId(@PathVariable Long id) {
        return ResponseEntity.ok().body(seriesService.getEpisodesToAllSeasonsBySeriesId(id));
    }

    @GetMapping("/{id}/temporadas/top")
    public ResponseEntity<List<EpisodeDTO>> getTop5EpisodesBySeriesId(@PathVariable Long id) {
        return ResponseEntity.ok().body(seriesService.getTop5EpisodesToAllSeasonsBySeriesId(id));
    }

    @GetMapping("/{id}/temporadas/{season}")
    public ResponseEntity<List<EpisodeDTO>> getEpisodesBySeriesId(@PathVariable Long id, @PathVariable Long season) {
        return ResponseEntity.ok().body(seriesService.getEpisodesBySeasonsAndSeriesId(id, season));
    }
}
