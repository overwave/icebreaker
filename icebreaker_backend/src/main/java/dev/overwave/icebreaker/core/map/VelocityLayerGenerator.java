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
    private static final int THREADS = 128;

    @SneakyThrows
    public void generateMapTiles(List<SpatialVelocity> spatialVelocities) {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
        List<ContinuousVelocity> velocities = spatialVelocities.getFirst().velocities();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (int dateIndex = 0; dateIndex < velocities.size(); dateIndex++) {
            LocalDate date = velocities.get(dateIndex).interval().instant().atOffset(ZoneOffset.UTC).toLocalDate();
            for (int zoom = ZOOM_FROM; zoom <= ZOOM_TO; zoom++) {
                // Y - вся карта в ширину
                Map.Entry<Integer, Integer> from = Mercator.pointToMercator(new Point(MAX_LATITUDE, -180), zoom - 8);
                Map.Entry<Integer, Integer> to = Mercator.pointToMercator(new Point(MIN_LATITUDE, 179.999F), zoom - 8);

                for (int x = from.getKey(); x <= to.getKey(); x++) {
                    for (int y = from.getValue(); y <= to.getValue(); y++) {
                        BufferedImage tile = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D graphics = tile.createGraphics();

                        int finalDateIndex = dateIndex;
                        int finalZoom = zoom;
                        long finalX = x;
                        long finalY = y;
                        if (isEmptyArea(x * TILE_SIZE, y * TILE_SIZE, zoom)) {
                            continue;
                        }
                        futures.add(CompletableFuture.runAsync(() -> drawTile(spatialVelocities, finalDateIndex,
                                graphics, finalZoom, finalX, finalY, tile, date), threadPool));
                        if (futures.size() > THREADS) {
                            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
                            futures.clear();
                        }
                    }
                }
            }
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
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
                graphics.setColor(new Color(20, 255, 194, 77));
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

            // draw beyond 180deg
            for (int xPoint : xPoints) {
                int offset = 2 << zoom + 7;
                if (xPoint > offset) {
                    int[] xPointsLeft = new int[]{xPoints[0] - offset, xPoints[1] - offset,
                            xPoints[2] - offset, xPoints[3] - offset,};
                    graphics.fillPolygon(xPointsLeft, yPoints, 4);
                    break;
                }
            }
        }
        if (isDirty(tile)) {
            File file = new File(TILE_GENERATION_PATH.formatted(date, zoom, x, y));
            file.mkdirs();
            ImageIO.write(tile, "png", file);
            System.out.printf(TILE_GENERATION_PATH, date, zoom, x, y);
            System.out.println();
        }
    }

    private boolean isEmptyArea(int tileX, int tileY, int zoom) {
        int zoomTileSize = TILE_SIZE << zoom;
        float normalizedY = (float) tileY / zoomTileSize;
        if (normalizedY > 0.33F) {
            // Убрал зону ниже Архангельска
            return true;
        }
        float normalizedXStart = (float) tileX / zoomTileSize;
        float normalizedXEnd = (float) (tileX + TILE_SIZE) / zoomTileSize;
        // Убрал зону Америки
        return normalizedXStart > 0.07F && normalizedXEnd < 0.52F;
    }

    private boolean isDirty(BufferedImage image) {
        int[] data = image.getData().getPixels(0, 0, TILE_SIZE, TILE_SIZE, (int[]) null);
        for (int color : data) {
            if (color != 0) {
                return true;
            }
        }
        return false;
    }
}
