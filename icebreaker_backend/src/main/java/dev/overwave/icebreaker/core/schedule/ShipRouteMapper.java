package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.api.navigation.PointAndTimestamp;
import dev.overwave.icebreaker.api.navigation.ShipRouteDto;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.route.Route;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShipRouteMapper {

    public ShipRouteDto toShipRouteDto(long id, RoutePredictionSegment prediction, Route route) {
        List<PointAndTimestamp> routes = createPointAndTimestamp(route.interval().instant(), route.normalizedPoints());
        return new ShipRouteDto(
                id,
                prediction.convoy(),
                "",
                routes);
    }

    public List<PointAndTimestamp> createPointAndTimestamp(Instant startDate, List<Point> normalizedPoints) {
        List<PointAndTimestamp> routes = new ArrayList<>();
        for (int i = 0; i < normalizedPoints.size(); i++) {
            long epochSecond = startDate.truncatedTo(ChronoUnit.HOURS).plus(Duration.ofHours(i)).getEpochSecond();
            routes.add(new PointAndTimestamp(normalizedPoints.get(i), epochSecond));
        }
        return routes;
    }
}
