package dev.overwave.icebreaker.api.navigation;

import java.util.List;

public record ShipRouteDto(
        long id,
        boolean convoy,
        Long icebreaker, // id сопровождающего ледокола
        List<PointAndTimestamp> routes
) {

}
