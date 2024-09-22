package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodeDTO;
import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.model.Category;
import br.com.alura.screenmatch.model.Series;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeriesService {

    @Autowired
    private SeriesRepository seriesRepository;

    @Transactional(readOnly = true)
    public List<SeriesDTO> findAll() {
       return this.convertSeriesToSeriesDTO(seriesRepository.findAll());
    }

    public List<SeriesDTO> findTopFiveSeries() {
        return this.convertSeriesToSeriesDTO(seriesRepository.findTop5ByOrderByImdbRatingDesc());
    }

    public List<SeriesDTO> findSeriesToReleasedDateEpisode() {
        return this.convertSeriesToSeriesDTO(seriesRepository.findSeriesToTopFiveEps());
    }

    public SeriesDTO findSeriesById(Long id) {
        Optional<Series> seriesOptional = seriesRepository.findById(id);
        if (seriesOptional.isPresent()) {
            Series series = seriesOptional.get();
            return new SeriesDTO(
                series.getId(),
                series.getTitle(),
                series.getTotalSeasons(),
                series.getImdbRating(),
                series.getGenre(),
                series.getActors(),
                series.getPoster(),
                series.getSummary()
            );
        }
        return null;
    }

    public List<SeriesDTO> findSeriesByGenre(String genre) {
        return this.convertSeriesToSeriesDTO(seriesRepository.findByGenre(Category.fromTranslate(genre)));
    }

    public List<EpisodeDTO> getEpisodesToAllSeasonsBySeriesId(Long id) {
        Optional<Series> seriesOptional = seriesRepository.findById(id);
        if (seriesOptional.isPresent()) {
            Series series = seriesOptional.get();
            return series.getEpisodes().stream().map(
                episode -> new EpisodeDTO(episode.getTitle(), episode.getSeason(), episode.getNumber())
            ).collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodeDTO> getTop5EpisodesToAllSeasonsBySeriesId(Long id) {
        return seriesRepository.findTopFiveEpsToSeries(id).stream().map(
                episode -> new EpisodeDTO(episode.getTitle(), episode.getSeason(), episode.getNumber())
        ).collect(Collectors.toList());
    }

    public List<EpisodeDTO> getEpisodesBySeasonsAndSeriesId(Long id, Long season) {
        return seriesRepository.findEpisodesToSeasonBySeriesId(id, season).stream().map(
            episode -> new EpisodeDTO(episode.getTitle(), episode.getSeason(), episode.getNumber())
        ).collect(Collectors.toList());
    }

    private List<SeriesDTO> convertSeriesToSeriesDTO(List<Series> series) {
        return series.stream()
            .map(s -> new SeriesDTO(
                s.getId(),
                s.getTitle(),
                s.getTotalSeasons(),
                s.getImdbRating(),
                s.getGenre(),
                s.getActors(),
                s.getPoster(),
                s.getSummary()
            )).collect(Collectors.toList());
    }
}
