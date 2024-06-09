package dev.overwave.icebreaker.core.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import dev.overwave.icebreaker.configuration.FunctionalTest;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocityFactory;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.map.Mercator;
import dev.overwave.icebreaker.core.navigation.IceClass;
import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.navigation.Ship;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.util.FileUtils;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Route route = Router.createRoute(from, to, Instant.now(), graph, new Ship("Плот", IceClass.ICE_0_3, 16, false)).orElseThrow();

        System.out.println(route.distance());
        Instant timeFrom = route.interval().instant();
        System.out.println(timeFrom + " - " + timeFrom.plus(route.interval().duration()));

        BufferedImage image = ImageIO.read(FileUtils.fromClassPath("/mercator.png"));
        Graphics2D graphics = image.createGraphics();

        {
            for (SpatialVelocity velocity : spatialVelocities) {
                    float v = velocity.velocities().getFirst().velocity();
                    if (v < 0) {
                        graphics.setColor(new Color(128, 128, 128, 77));
                    } else if (v < 10) {
                        graphics.setColor(new Color(255, 77, 77, 77));
                    } else if (v < 14.5F) {
                        graphics.setColor(new Color(255, 128, 0, 77));
                    } else if (v < 19.5F) {
                        graphics.setColor(new Color(255, 255, 0, 77));
                    }

                    List<Entry<Double, Double>> coords = Stream.of(velocity.topLeft(), velocity.topRight(),
                                    velocity.bottomRight(), velocity.bottomLeft())
                            .map(Mercator::pointToMercatorNormalized)
                            .toList();
                    int[] x = new int[]{
                            (int) (coords.get(0).getKey() * image.getHeight()),
                            (int) (coords.get(1).getKey() * image.getHeight()),
                            (int) (coords.get(2).getKey() * image.getHeight()),
                            (int) (coords.get(3).getKey() * image.getHeight()),
                    };
                    int[] y = new int[]{
                            (int) (coords.get(0).getValue() * image.getWidth()),
                            (int) (coords.get(1).getValue() * image.getWidth()),
                            (int) (coords.get(2).getValue() * image.getWidth()),
                            (int) (coords.get(3).getValue() * image.getWidth()),
                    };
                    graphics.fillPolygon(x, y, 4);
            }
        }

        List<List<Float>> points = new ArrayList<>();
        for (int i = 0, nodesSize = route.nodes().size(); i < nodesSize; i++) {

            Node node = route.nodes().get(i);
            Entry<Double, Double> point = Mercator.pointToMercatorNormalized(node.coordinates());
            points.add(List.of(node.coordinates().lat(), node.coordinates().lon()));

            if (i > 0) {
                graphics.setColor(Color.GREEN);
                graphics.setStroke(new BasicStroke(1));
                Node nodeBefore = route.nodes().get(i - 1);
                Entry<Double, Double> pointBefore = Mercator.pointToMercatorNormalized(nodeBefore.coordinates());
                graphics.drawLine(
                        (int) (point.getKey() * image.getWidth()),
                        (int) (point.getValue() * image.getHeight()),
                        (int) (pointBefore.getKey() * image.getWidth()),
                        (int) (pointBefore.getValue() * image.getHeight())
                );
            }
        }
        Files.writeString(new File("route.json").toPath(), objectMapper.writeValueAsString(points),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        image = image.getSubimage(3158, 66, 3147, 3203);
        ImageIO.write(image, "PNG", new File("mercator_test_route_with_layer.png"));
    }
}