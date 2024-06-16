package dev.overwave.icebreaker.api.navigation;

import lombok.Builder;

import java.util.List;

@Builder
public record ShipRouteDto(
        long id,
        boolean convoy,
        String icebreaker,
        List<PointAndTimestamp> routes
) {

}
