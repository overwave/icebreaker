package dev.overwave.icebreaker.graph;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Edge;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocityFactory;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.graph.SparseList;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class GraphFactoryTest {

    @Test
    @SneakyThrows
    void testGraphGeneration() {
        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocity.xlsx");
        List<SpatialVelocity> spatialVelocities = SpatialVelocityFactory.formSpatialVelocityGrid(matrix);

        Graph graph = GraphFactory.buildWeightedGraph(spatialVelocities);
        List<SparseList<Node>> sparseLists = graph.getGraph();
        List<ContinuousVelocity> vvv = sparseLists.getFirst().getContent().get(100).edges().getFirst().velocities();
        for (int vv = 0; vv < vvv.size(); vv++) {
            BufferedImage image = ImageIO.read(fromClassPath());
            Graphics2D graphics2D = image.createGraphics();
            for (SparseList<Node> nodes : sparseLists) {
                for (Node node : nodes.getContent()) {
                    for (Edge edge : node.edges()) {
                        float v = edge.velocities() == null ? -10 : edge.velocities().get(vv).velocity();
                        if (v < 0) {
                            graphics2D.setColor(Color.GRAY);
                        } else if (v < 10) {
                            graphics2D.setColor(Color.RED);
                        } else if (v < 14.5F) {
                            graphics2D.setColor(Color.ORANGE);
                        } else if (v < 19.5F) {
                            graphics2D.setColor(Color.YELLOW);
                        } else {
                            graphics2D.setColor(Color.GREEN);
                        }

                        Entry<Double, Double> pointLeft =
                                Mercator.pointToMercatorNormalized(edge.nodes().getKey().coordinates());
                        Entry<Double, Double> pointRight =
                                Mercator.pointToMercatorNormalized(edge.nodes().getValue().coordinates());
                        graphics2D.drawLine(
                                (int) (pointLeft.getKey() * image.getWidth()),
                                (int) (pointLeft.getValue() * image.getHeight()),
                                (int) (pointRight.getKey() * image.getWidth()),
                                (int) (pointRight.getValue() * image.getHeight())
                        );
                    }
                }
            }

            image = image.getSubimage(3158, 66, 3147, 3203);
            LocalDate date = vvv.get(vv).interval().instant().atOffset(ZoneOffset.UTC).toLocalDate();
            ImageIO.write(image, "PNG", new File("mercator_edges_velocity_" + date + ".png"));
        }
    }

    private static InputStream fromClassPath() {
        return GraphFactoryTest.class.getResourceAsStream("/mercator.png");
    }

    @Test
    @SneakyThrows
    void renderUniformGrid() {
        BufferedImage image = ImageIO.read(fromClassPath());
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(Color.YELLOW);
        graphics2D.setStroke(new BasicStroke(3));

        // рисуем узлы графа
        Graph graph = GraphFactory.buildWeightedGraph(List.of());
        List<SparseList<Node>> sparseLists = graph.getGraph();
        for (SparseList<Node> nodes : sparseLists) {
            for (Node node : nodes.getContent()) {
                Entry<Double, Double> point = Mercator.pointToMercatorNormalized(node.coordinates());
                int x = (int) (point.getKey() * image.getWidth());
                int y = (int) (point.getValue() * image.getHeight());
                graphics2D.drawLine(x, y, x, y);
            }
        }

        // рисуем рёбра графа
        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.setStroke(new BasicStroke(1));
        for (SparseList<Node> nodes : sparseLists) {
            for (Node node : nodes.getContent()) {
                for (Edge edge : node.edges()) {
                    Entry<Double, Double> pointLeft =
                            Mercator.pointToMercatorNormalized(edge.nodes().getKey().coordinates());
                    Entry<Double, Double> pointRight =
                            Mercator.pointToMercatorNormalized(edge.nodes().getValue().coordinates());
                    graphics2D.drawLine(
                            (int) (pointLeft.getKey() * image.getWidth()),
                            (int) (pointLeft.getValue() * image.getHeight()),
                            (int) (pointRight.getKey() * image.getWidth()),
                            (int) (pointRight.getValue() * image.getHeight())
                    );
                }
            }
        }

        // рисуем Лондон просто так
        Entry<Double, Double> point = Mercator.pointToMercatorNormalized(new Point(51.506873F, -0.181732F));
        int x = (int) (point.getKey() * image.getWidth());
        int y = (int) (point.getValue() * image.getHeight());
        graphics2D.drawLine(x, y, x, y);

        ImageIO.write(image, "PNG", new File("mercator_nodes.png"));
    }

    @Test
    @SneakyThrows
    void renderRosatom() {
        BufferedImage image = ImageIO.read(fromClassPath());
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setStroke(new BasicStroke(15));
        float maxLat = 0;
        float minLat = 90;

        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocity.xlsx");
        for (List<RawVelocity> velocities : matrix) {
            for (RawVelocity velocity : velocities) {
                Entry<Double, Double> point = Mercator.pointToMercatorNormalized(velocity.coordinates());
                int x = (int) (point.getKey() * image.getWidth()) % image.getWidth();
                int y = (int) (point.getValue() * image.getHeight()) % image.getHeight();
                if (velocity.velocities().stream().anyMatch(v -> v.velocity() >= 10)) {
                    maxLat = Math.max(maxLat, velocity.coordinates().lat());
                    minLat = Math.min(minLat, velocity.coordinates().lat());
                }
                float v = velocity.velocities().getFirst().velocity();

                if (v < 0) {
                    graphics2D.setColor(Color.GRAY);
                } else if (v < 10) {
                    graphics2D.setColor(Color.RED);
                } else if (v < 14.5F) {
                    graphics2D.setColor(Color.ORANGE);
                } else if (v < 19.5F) {
                    graphics2D.setColor(Color.YELLOW);
                } else {
                    graphics2D.setColor(Color.GREEN);
                }
                graphics2D.drawLine(x, y, x, y);
            }
        }

        ImageIO.write(image, "PNG", new File("mercator_velocity_nodes.png"));
    }

    @Test
    @SneakyThrows
    void renderRosatom2() {
        BufferedImage image = ImageIO.read(fromClassPath());
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setStroke(new BasicStroke(0));
        graphics2D.setBackground(Color.BLUE);

        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocity.xlsx");
        List<SpatialVelocity> spatialVelocities = SpatialVelocityFactory.formSpatialVelocityGrid(matrix);
        for (SpatialVelocity velocity : spatialVelocities) {
            float v = velocity.velocities().getFirst().velocity();
            if (v < 0) {
                graphics2D.setColor(Color.GRAY);
            } else if (v < 10) {
                graphics2D.setColor(Color.RED);
            } else if (v < 14.5F) {
                graphics2D.setColor(Color.ORANGE);
            } else if (v < 19.5F) {
                graphics2D.setColor(Color.YELLOW);
            } else {
                graphics2D.setColor(Color.GREEN);
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
            graphics2D.fillPolygon(x, y, 4);
            x[0] -= image.getHeight();
            x[1] -= image.getHeight();
            x[2] -= image.getHeight();
            x[3] -= image.getHeight();
            graphics2D.fillPolygon(x, y, 4);
        }

        ImageIO.write(image, "PNG", new File("mercator_velocity_spatial.png"));
    }

    @Test
    void testMercatorProjection() {
        Map.Entry<Double, Double> point = Mercator.pointToMercatorNormalized(new Point(51.506873F, -0.181732F));
        System.out.println(point);
    }
}
