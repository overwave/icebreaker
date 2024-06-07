package dev.overwave.icebreaker.core.map;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.overwave.icebreaker.core.graph.GraphFactory.MAX_LATITUDE;
import static dev.overwave.icebreaker.core.graph.GraphFactory.MIN_LATITUDE;

@UtilityClass
public class VelocityLayerGenerator {
    private static final String TILE_GENERATION_PATH = "tiles_generation/%s/%d/%d-%d.jpg";
    private static final int TILE_SIZE = 256;

    private static final int ZOOM_FROM = 3;
    private static final int ZOOM_TO = 9;

    @SneakyThrows
    public void generateMapTiles(List<SpatialVelocity> spatialVelocities) {
        ExecutorService threadPool = Executors.newFixedThreadPool(128);
        List<ContinuousVelocity> velocities = spatialVelocities.getFirst().velocities();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (int dateIndex = 0; dateIndex < velocities.size(); dateIndex++) {
            LocalDate date = velocities.get(dateIndex).interval().instant().atOffset(ZoneOffset.UTC).toLocalDate();
            for (int zoom = ZOOM_FROM; zoom <= ZOOM_TO; zoom++) {
                // Y - вся карта в ширину
                Map.Entry<Long, Long> from = Mercator.pointToMercator(new Point(MAX_LATITUDE, -180), zoom - 8);
                Map.Entry<Long, Long> to = Mercator.pointToMercator(new Point(MIN_LATITUDE, 179.999F), zoom - 8);

                for (long x = from.getKey(); x <= to.getKey(); x++) {
                    for (long y = from.getValue(); y <= to.getValue(); y++) {
                        BufferedImage tile = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D graphics = tile.createGraphics();

                        int finalDateIndex = dateIndex;
                        int finalZoom = zoom;
                        long finalX = x;
                        long finalY = y;
                        Thread.sleep(1);
                        futures.add(CompletableFuture.runAsync(() -> drawTile(spatialVelocities, finalDateIndex,
                                graphics, finalZoom, finalX, finalY, tile, date), threadPool));
                    }
                }
            }
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        allFutures.get();
    }

    @SneakyThrows
    private void drawTile(List<SpatialVelocity> spatialVelocities, int dateIndex, Graphics2D graphics,
                          int zoom, long x, long y, BufferedImage tile, LocalDate date) {
        for (SpatialVelocity spatialVelocity : spatialVelocities) {
            if (spatialVelocity.velocities() == null) {
                continue;
            }
            ContinuousVelocity continuousVelocity = spatialVelocity.velocities().get(dateIndex);
            float velocity = continuousVelocity.velocity();
            if (velocity < 0) {
                continue;
            } else if (velocity < 10) {
                graphics.setColor(new Color(255, 77, 77, 77));
            } else if (velocity < 14.5F) {
                graphics.setColor(new Color(255, 128, 0, 77));
            } else if (velocity < 19.5F) {
                graphics.setColor(new Color(255, 255, 0, 77));
            } else {
                continue;
            }

            int[] xPoints = new int[]{
                    (int) (Mercator.pointToMercator(spatialVelocity.topLeft(), zoom).getKey() - x * TILE_SIZE),
                    (int) (Mercator.pointToMercator(spatialVelocity.topRight(), zoom).getKey() - x * TILE_SIZE),
                    (int) (Mercator.pointToMercator(spatialVelocity.bottomRight(), zoom).getKey() - x * TILE_SIZE),
                    (int) (Mercator.pointToMercator(spatialVelocity.bottomLeft(), zoom).getKey() - x * TILE_SIZE),
            };
            int[] yPoints = new int[]{
                    (int) (Mercator.pointToMercator(spatialVelocity.topLeft(), zoom).getValue() - y * TILE_SIZE),
                    (int) (Mercator.pointToMercator(spatialVelocity.topRight(), zoom).getValue() - y * TILE_SIZE),
                    (int) (Mercator.pointToMercator(spatialVelocity.bottomRight(), zoom).getValue() - y * TILE_SIZE),
                    (int) (Mercator.pointToMercator(spatialVelocity.bottomLeft(), zoom).getValue() - y * TILE_SIZE),
            };
            graphics.fillPolygon(xPoints, yPoints, 4);
        }
        if (isDirty(tile)) {
            File file = new File(TILE_GENERATION_PATH.formatted(date, zoom, x, y));
            file.mkdirs();
            ImageIO.write(tile, "png", file);
            System.out.printf(TILE_GENERATION_PATH, date, zoom, x, y);
            System.out.println();
        }
    }

    private boolean isDirty(BufferedImage image) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int k = 0; k < image.getHeight(); k++) {
                if (image.getRGB(i, k) != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
