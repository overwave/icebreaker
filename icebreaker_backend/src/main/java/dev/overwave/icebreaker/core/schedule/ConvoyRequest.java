package dev.overwave.icebreaker.core.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvoyRequest {
    private ScheduledShip ship;
    private ScheduledShip icebreaker;
    private RoutePredictionSegment routeSegment;
}
