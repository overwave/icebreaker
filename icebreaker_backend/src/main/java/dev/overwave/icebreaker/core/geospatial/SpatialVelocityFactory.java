package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.core.util.GeometryUtils;
import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class SpatialVelocityFactory {

    private static final float LAND_INTEGRAL_VELOCITY = -10;
    private static final float CLEAR_WATER_INTEGRAL_VELOCITY = 20;

    public List<SpatialVelocity> formSpatialVelocityGrid(List<List<RawVelocity>> velocityMatrix) {
        List<List<Point>> impassableAreas = Arrays.stream(CartographyLibrary.IMPASSABLE_AREAS)
                .map(SpatialVelocityFactory::parseRulerPoints).toList();
        List<List<Point>> passableOverrideAreas = Arrays.stream(CartographyLibrary.PASSABLE_AREAS_OVERRIDE)
                .map(SpatialVelocityFactory::parseRulerPoints).toList();
        List<List<Point>> passableAreaIfWater = Arrays.stream(CartographyLibrary.PASSABLE_AREAS_IF_WATER)
                .map(SpatialVelocityFactory::parseRulerPoints).toList();

        List<SpatialVelocity> grid = new ArrayList<>(velocityMatrix.size() * velocityMatrix.getFirst().size());
        for (int row = 0; row < velocityMatrix.size() - 1; row++) {
            for (int col = 0; col < velocityMatrix.get(row).size() - 1; col++) {
                Point topLeft = velocityMatrix.get(row).get(col).coordinates();
                Point topRight = velocityMatrix.get(row).get(col + 1).coordinates();
                Point bottomRight = velocityMatrix.get(row + 1).get(col + 1).coordinates();
                Point bottomLeft = velocityMatrix.get(row + 1).get(col).coordinates();
                List<ContinuousVelocity> velocities = velocityMatrix.get(row).get(col).velocities();
                if (impassableAreas.stream().anyMatch(poly ->
                        containsMostPoints(poly, topLeft, topRight, bottomRight, bottomLeft))) {
                    velocities = markImpassable(velocities);
                }
                if (passableOverrideAreas.stream().anyMatch(poly ->
                        containsMostPoints(poly, topLeft, topRight, bottomRight, bottomLeft))) {
                    velocities = markPassable(velocities);
                }
                if (passableAreaIfWater.stream().anyMatch(poly ->
                        containsMostPoints(poly, topLeft, topRight, bottomRight, bottomLeft))) {
                    velocities = markPassableIfWasWater(velocities);
                }
                SpatialVelocity spatialVelocity = SpatialVelocity.builder()
                        .topLeft(topLeft)
                        .topRight(topRight)
                        .bottomLeft(bottomLeft)
                        .bottomRight(bottomRight)
                        .velocities(velocities)
                        .build();
                grid.add(spatialVelocity);
            }
        }
        return grid;
    }

    private List<ContinuousVelocity> markImpassable(List<ContinuousVelocity> velocities) {
        return velocities.stream().map(vel -> new ContinuousVelocity(LAND_INTEGRAL_VELOCITY, vel.interval())).toList();
    }

    private List<ContinuousVelocity> markPassable(List<ContinuousVelocity> velocities) {
        return velocities.stream()
                .map(vel -> new ContinuousVelocity(CLEAR_WATER_INTEGRAL_VELOCITY, vel.interval())).toList();
    }

    private List<ContinuousVelocity> markPassableIfWasWater(List<ContinuousVelocity> velocities) {
        return velocities.stream()
                .map(vel -> new ContinuousVelocity(vel.velocity() >= 0 ? CLEAR_WATER_INTEGRAL_VELOCITY : vel.velocity(),
                        vel.interval())).toList();
    }

    private boolean containsMostPoints(List<Point> polygon, Point... points) {
        List<Point> pointsList = simplifyPolygon(points);
        int contained = 0;
        for (Point point : pointsList) {
            if (GeometryUtils.polygonContains(polygon, point)) {
                contained++;
            }
        }
        return contained + 1 >= pointsList.size();
    }

    private static List<Point> simplifyPolygon(Point[] points) {
        // works only for 4-edged triangles and lon!
        for (int i = 0; i < points.length + 3; i++) {
            if (points[i % points.length].lon() == points[(i + 1) % points.length].lon() &&
                points[(i + 1) % points.length].lon() == points[(i + 2) % points.length].lon()) {
                return List.of(
                        points[i % points.length],
                        points[(i + 2) % points.length],
                        points[(i + 3) % points.length]);
            }
        }
        return List.of(points);
    }

    public List<Point> parseRulerPoints(String url) {
        String rulerPoints = url.substring(url.indexOf("&rl=") + 4, url.indexOf("&z="));
        String decoded = URLDecoder.decode(rulerPoints, StandardCharsets.UTF_8);
        String[] points = decoded.split("~");
        List<Point> result = new ArrayList<>();
        float xDelta = 0;
        float yDelta = 0;
        for (String point : points) {
            String[] xAndY = point.split(",");
            xDelta += Float.parseFloat(xAndY[1]);
            yDelta += Float.parseFloat(xAndY[0]);
            result.add(new Point(xDelta, yDelta));
        }
        return result;
    }
}
