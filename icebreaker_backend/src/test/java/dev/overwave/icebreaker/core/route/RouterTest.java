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
import dev.overwave.icebreaker.core.navigation.IceClass;
import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.navigation.Ship;
import dev.overwave.icebreaker.core.parser.XlsxParser;
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
import java.util.stream.Collectors;

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
                new Ship("Плот", IceClass.ICE_0_3, 16, false)).orElseThrow();
        printRoute(route, "route_novaya_zemlya.json");

        from = pointsByName.get("Дудинка");
        to = pointsByName.get("Архангельск");
        Route route2 = Router.createRoute(from, to, Instant.now(), graph,
                new Ship("Кобаблище", IceClass.ARC_7, 13, false)).orElseThrow();
        printRoute(route2, "route_dudinka.json");
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