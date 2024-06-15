package dev.overwave.icebreaker.core.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvoyRequest {
    private ScheduledShip ship;
    private ScheduledShip icebreaker;
    private RoutePredictionSegment routeSegment;

    @Override
    public String toString() {
        return "ConvoyRequest[ship=%d, ibr=%d, from=%s to %s]".formatted(ship.getShipId(),
                icebreaker != null ? icebreaker.getShipId() : null,
                routeSegment.from().name(), routeSegment.to().name());
    }
}
