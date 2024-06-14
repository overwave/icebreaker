package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DefaultRouteMapper {
    public DefaultRouteStatic toDefaultRouteStatic(DefaultRoute defaultRoute) {
        VelocityInterval velocityInterval = defaultRoute.getVelocityInterval();
        return DefaultRouteStatic.builder()
                .id(defaultRoute.getId())
                .routeId(defaultRoute.getEdge().getId())
                .interval(new Interval(velocityInterval.getStartDate(),
                        Duration.between(velocityInterval.getStartDate(), velocityInterval.getEndDate())))
                .iceClassGroup(defaultRoute.getIceClassGroup())
                .movementType(defaultRoute.getMovementType())
                .travelTime(Duration.ofMinutes(defaultRoute.getTravelTimeMinutes()))
                .build();
    }
}
