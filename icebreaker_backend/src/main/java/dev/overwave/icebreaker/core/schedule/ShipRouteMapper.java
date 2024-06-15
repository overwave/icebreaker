package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.api.navigation.PointAndTimestamp;
import dev.overwave.icebreaker.api.navigation.ShipRouteDto;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.route.Route;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShipRouteMapper {

    public ShipRouteDto toShipRouteDto(long id, RoutePredictionSegment prediction, Route route,
                                       ScheduledShip icebreaker) {
        List<PointAndTimestamp> routes = createPointAndTimestamp(route.interval().instant(), route.normalizedPoints());
        return new ShipRouteDto(
                id,
                prediction.convoy(),
                icebreaker.getShipId(),
                routes);
    }

    private List<PointAndTimestamp> createPointAndTimestamp(Instant startDate, List<Point> normalizedPoints) {
        List<PointAndTimestamp> routes = new ArrayList<>();
        routes.add(new PointAndTimestamp(normalizedPoints.getFirst(), startDate.getEpochSecond()));
        Instant instant = startDate;
        for (int i = 1; i < normalizedPoints.size(); i++) {
            Point point = normalizedPoints.get(i);
            instant = instant.plus(1, ChronoUnit.HOURS);
            routes.add(new PointAndTimestamp(point, instant.getEpochSecond()));
        }
        return routes;
    }
}
