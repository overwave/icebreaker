package dev.overwave.icebreaker.core.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import dev.overwave.icebreaker.configuration.FunctionalTest;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocityFactory;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.core.ship.IceClass;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalTest
@RequiredArgsConstructor
class RouterTest {
    private final NavigationPointService navigationPointService;
    private final ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void buildRoute() {
        navigationPointService.resetNavigationPoints(FileUtils.fromClassPath("/ГрафДанные.xlsx"));
        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocity.xlsx");
        List<SpatialVelocity> spatialVelocities = SpatialVelocityFactory.formSpatialVelocityGrid(matrix);
        Graph graph = GraphFactory.buildWeightedGraph(spatialVelocities);

        Map<String, Point> pointsByName = navigationPointService.getNavigationPoints().stream()
                .collect(Collectors.toMap(NavigationPointDto::name, NavigationPointDto::point));
        Point from = pointsByName.get("Карские ворота");
        Point to = pointsByName.get("Мыс Желания");

        Route route = Router.createRoute(from, to, Instant.now(), graph,
                new Ship("Плот", IceClass.ICE_2, 16, false, null, null)).orElseThrow();
        printRoute(route, "route_novaya_zemlya.json");

        from = pointsByName.get("Дудинка");
        to = pointsByName.get("Архангельск");
        Route route2 = Router.createRoute(from, to, Instant.now(), graph,
                new Ship("Кобаблище", IceClass.ARC_7, 13, false, null, null)).orElseThrow();
        printRoute(route2, "route_dudinka.json");

        Point from2 = new Point(69.5F, 33.75F);
        Point to2 = new Point(76.2F, 58.3F);
        Instant startDate = Instant.parse("2020-04-05T00:00:00Z");
        Ship ship = new Ship("Ship", IceClass.ARC_5, 16F, false, null, null);

        Optional<Route> route3 = Router.createRoute(from2, to2, startDate, graph, ship);
        assertThat(route3).isPresent();
        assertThat(route3.get().interval().instant()).isBefore(Instant.parse("2020-04-10T00:00:00Z"));

    }

    private void printRoute(Route route, String pathname) throws IOException {
        List<List<Float>> points = routeToPoints(route);
        Files.writeString(new File(pathname).toPath(), objectMapper.writeValueAsString(points),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    private List<List<Float>> routeToPoints(Route route) {
        return route.nodes().stream()
                .map(node -> List.of(node.coordinates().lat(), node.coordinates().lon()))
                .toList();
    }
}