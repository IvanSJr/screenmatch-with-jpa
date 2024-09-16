package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.Series;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.repository.SeriesRepository;
import br.com.alura.screenmatch.service.APIConsumerService;
import br.com.alura.screenmatch.service.DataConverterService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Main {

    private final String API_URL = "https://www.omdbapi.com/?t=";
    private static final String API_KEY_VALUE = System.getenv("API_KEY_VALUE");
    private final Scanner scanner = new Scanner(System.in);
    private final APIConsumerService apiConsumerService = new APIConsumerService();
    private final DataConverterService dataConverterService = new DataConverterService();
    private final SeriesRepository seriesRepository;

    private List<Series> seriesFound = new ArrayList<>();

    public Main(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public void showMenu() {
        var option = -1;
        while(option != 0) {
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                0 - Sair                                 
                """;

            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    findSeries();
                    break;
                case 2:
                    findEpisodesToSeries();
                    break;
                case 3:
                    listAllSeriesFound();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void listAllSeriesFound() {
        this.seriesFound = seriesRepository.findAll();
        seriesFound.stream().sorted(
            Comparator.comparing(Series::getGenre)
        ).forEach(
            System.out::println
        );
    }

    private void findSeries() {
        SeriesData seriesData = getDataSerie();
        seriesRepository.save(new Series(seriesData));
        System.out.println(seriesData);
    }

    private SeriesData getDataSerie() {
        System.out.println("Digite o nome da série para busca");
        var name = scanner.nextLine();
        var json = apiConsumerService.getData(API_URL + name.replace(" ", "+") + API_KEY_VALUE);
        return dataConverterService.obterDados(json, SeriesData.class);
    }

    private void findEpisodesToSeries(){
        this.listAllSeriesFound();
        System.out.println("\nEscolha uma serie pelo nome: ");
        var seriesName = scanner.nextLine();

        Optional<Series> optionalSeriesFound = this.seriesFound.stream().filter(
                series -> series.getTitle().toLowerCase().contains(seriesName.toLowerCase())
        ).findFirst();
        if (optionalSeriesFound.isPresent()) {
            var series = optionalSeriesFound.get();
            List<SeasonData> seasons = new ArrayList<>();

            for (int i = 1; i <= series.getTotalSeasons(); i++) {
                var json = apiConsumerService.getData(API_URL + series.getTitle().replace(" ", "+") + "&season=" + i + API_KEY_VALUE);
                SeasonData seasonData = dataConverterService.obterDados(json, SeasonData.class);
                seasons.add(seasonData);
            }

            List<Episode> episodes = seasons.stream().flatMap(
                season -> season.listEpisodes().stream().map(
                    episode -> new Episode(episode.episode(), episode)
                )
            ).toList();
            series.setEpisodes(episodes);
            seriesRepository.save(series);
        } else {
            System.out.println("Série não encontrada!");
        }
    }
}