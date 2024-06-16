package dev.overwave.icebreaker.api.navigation;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ShipRouteDto(
        long id,
        boolean convoy,
        String icebreaker,
        // начало интервала ледовой проходимости
        LocalDate iceFlotationData,
        List<PointAndTimestamp> routes
) {

}
