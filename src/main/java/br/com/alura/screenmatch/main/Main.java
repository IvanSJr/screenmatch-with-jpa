package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SeriesRepository;
import br.com.alura.screenmatch.service.APIConsumerService;
import br.com.alura.screenmatch.service.DataConverterService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Main {

    private static final String API_URL = "https://www.omdbapi.com/?t=";
    private static final String API_KEY_VALUE = System.getenv("API_KEY_VALUE");
    private static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Scanner scanner = new Scanner(System.in);
    private final APIConsumerService apiConsumerService = new APIConsumerService();
    private final DataConverterService dataConverterService = new DataConverterService();
    private final SeriesRepository seriesRepository;
    private Optional<Series> foundSeries = Optional.empty();

    public Main(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public void showMenu() {
        int option = -1;
        while (option != 0) {
            String menu = """
                1 - Cadastrar série por título
                2 - Cadastrar episódios de uma série
                3 - Listar séries cadastradas
                4 - Listar top 5 séries cadastradas
                5 - Buscar séries por autor
                6 - Buscar série por título
                7 - Buscar séries por gênero
                8 - Buscar séries por temporadas máxima e IMDB mínimo
                9 - Buscar episódios por trecho
                10 - Buscar top 5 episódios por série
                0 - Sair
                """;

            System.out.println(menu);
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida");
                continue;
            }

            switch (option) {
                case 1 -> saveSeries();
                case 2 -> saveEpisodesToSeries();
                case 3 -> findAllSeriesFound();
                case 4 -> findTopFiveSeries();
                case 5 -> findSeriesByAuthor();
                case 6 -> findSeriesByTitle();
                case 7 -> findSeriesByGenre();
                case 8 -> findSeriesByTotalSeasonsAndImdbRating();
                case 9 -> findAllEpisodesByText();
                case 10 -> findTopFiveEpisodesToSeries();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private void findAllEpisodesByText() {
        System.out.println("Digite o trecho do episódio que quer buscar:");
        var textSearch = scanner.nextLine();
        List<Episode> episodes = seriesRepository.findEpisodesByText(textSearch);
        episodes.forEach(Main::printOutEpisodesAllData);
    }

    private void findTopFiveSeries() {
        List<Series> series = seriesRepository.findTop5ByOrderByImdbRatingDesc();
        listSeries(series);
    }

    private void findAllSeriesFound() {
        listSeries(seriesRepository.findAll());
    }

    private void saveSeries() {
        try {
            SeriesData seriesData = getSeriesData();
            if (seriesData == null || seriesData.title() == null) {
                System.out.println("Série não encontrada!");
                return;
            }
            Series savedSeries = seriesRepository.save(new Series(seriesData));
            printOutSeriesAllData(savedSeries);
        } catch (Exception e) {
            System.out.println("Erro ao salvar série: " + e.getMessage());
        }
    }

    private SeriesData getSeriesData() {
        System.out.println("Digite o nome da série para busca");
        var name = scanner.nextLine();
        try {
            var json = apiConsumerService.getData(API_URL + name.replace(" ", "+") + API_KEY_VALUE);
            return dataConverterService.getSeriesData(json, SeriesData.class);
        } catch (Exception e) {
            System.out.println("Erro ao buscar dados da série: " + e.getMessage());
            return null;
        }
    }

    private void saveEpisodesToSeries(){
        this.findAllSeriesFound();
        System.out.println("\nEscolha uma serie pelo nome: ");
        var seriesName = scanner.nextLine();

        Optional<Series> optionalSeriesFound = seriesRepository.findByTitleContainingIgnoreCase(seriesName);
        if (optionalSeriesFound.isPresent()) {
            var series = optionalSeriesFound.get();
            List<SeasonData> seasons = new ArrayList<>();

            for (int i = 1; i <= series.getTotalSeasons(); i++) {
                var json = apiConsumerService.getData(API_URL + series.getTitle().replace(" ", "+") + "&season=" + i + API_KEY_VALUE);
                SeasonData seasonData = dataConverterService.getSeriesData(json, SeasonData.class);
                seasons.add(seasonData);
            }

            List<Episode> episodes = seasons.stream().flatMap(
                season -> season.listEpisodes().stream().map(
                    episode -> new Episode(episode.episode(), episode)
                )
            ).toList();
            series.setEpisodes(episodes);
            Series savedSeries = seriesRepository.save(series);
            List<Episode> episodeList = savedSeries.getEpisodes();
            episodeList.forEach(Main::printOutEpisodesAllData);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void findSeriesByTitle() {
        System.out.println("\nEscolha uma serie pelo nome: ");
        var seriesName = scanner.nextLine();
        this.foundSeries = seriesRepository.findByTitleContainingIgnoreCase(seriesName);
        if (foundSeries.isPresent()) {
            var series = foundSeries.get();
            printOutSeriesAllData(series);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void findTopFiveEpisodesToSeries() {
        findSeriesByTitle();
        if (this.foundSeries.isPresent()) {
            Series series = this.foundSeries.get();
            if (series.getEpisodes().isEmpty()) {
                System.out.println("Não temos episódios cadastrados");
            } else {
                List<Episode> topFiveEpisodesToSeries = seriesRepository.findTopFiveEpsToSeries(series);
                topFiveEpisodesToSeries.forEach(Main::printOutEpisodesAllData);
            }
        }
    }

    private void findSeriesByAuthor() {
        System.out.println("\nEscolha uma serie pelo nome do autor: ");
        var authorName = scanner.nextLine();
        List<Series> seriesFound = seriesRepository.findByActorsContainingIgnoreCase(authorName);
        if (!seriesFound.isEmpty()) {
            System.out.println("Series em que " + authorName + " trabalhou: ");
            listSeries(seriesFound);
        } else {
            System.out.println("Nenhuma série encontrado com o autor:" + authorName);
        }
    }

    private void findSeriesByGenre() {
        System.out.println("\nEscolha uma serie pelo nome do genero: ");
        var genreName = scanner.nextLine();
        Category category = Category.fromTranslate(genreName);
        List<Series> seriesFound = seriesRepository.findByGenre(category);
        if (!seriesFound.isEmpty()) {
            System.out.println("Series do género " + genreName + ": ");
            listSeries(seriesFound);
        } else {
            System.out.println("Nenhuma série encontrado com o género:" + genreName);
        }
    }

    private void findSeriesByTotalSeasonsAndImdbRating() {
        System.out.println("\nEscolha uma serie pelo número máximo de temporadas: ");
        var totalSeasons = scanner.nextInt();

        System.out.println("\nEscolha uma serie a partir de uma avaliação: ");
        var imdbRating = scanner.nextDouble();
        List<Series> seriesFound = seriesRepository.findByTotalSeasonsAndImdbRating(totalSeasons, imdbRating);
        if (!seriesFound.isEmpty()) {
            System.out.println("Series: ");
            listSeries(seriesFound);
        } else {
            System.out.println("Nenhuma série encontrada");
        }
    }

    private static void listSeries(List<Series> seriesFound) {
        seriesFound.forEach(Main::printOutSeriesAllData);
    }

    private static void printOutSeriesAllData(Series savedSeries) {
        System.out.println(
            "Título: " + savedSeries.getTitle() +
                "\nAvaliação: " + savedSeries.getImdbRating() +
                "\nTotal de temporadas: " + savedSeries.getTotalSeasons() +
                "\nGênero: " + savedSeries.getGenre() +
                "\nAtores: " + savedSeries.getActors() +
                "\nResumo: " + savedSeries.getSummary() + "\n"
        );
    }

    private static void printOutEpisodesAllData(Episode episode) {
        System.out.println("Série: " + episode.getSeries().getTitle());
        System.out.println("Titulo: " + episode.getTitle());
        System.out.println("Temporada: " + episode.getSeason());
        System.out.println("Episódio de número: " + episode.getNumber());
        System.out.println("Avaliação: " + episode.getImdbRating());
        if (episode.getReleaseDate() != null) {
            System.out.println("Data de lançamento: " + parseDateToLocalFormat(episode.getReleaseDate().toString()));
        } else {
            System.out.println("Data de lançamento: " + "N/A");
        }
        System.out.println("\n");
    }

    public static String parseDateToLocalFormat(String dataString) {
        try {
            LocalDate data = LocalDate.parse(dataString, inputFormatter);
            return data.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Formato de data inválido!";
        }
    }
}
