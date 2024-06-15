package dev.overwave.icebreaker.core.schedule;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(of = "shipId")
public class ScheduledShip {
    private long shipId;
    private long currentNavigationPointId;
    private ScheduleStatus status;
    private boolean icebreaker;
    private long nextNavigationPointId;
    private Long finishNavigationPointId;
    private Instant actionEndEta;
    @Builder.Default
    private List<ConvoyRequest> convoyRequests = new ArrayList<>();
    @Builder.Default
    private List<ConfirmedRouteSegment> routeSegments = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (icebreaker) {
            builder.append("ICEB[");
        } else {
            builder.append("SHIP[");
        }
        builder.append(shipId).append("], ").append(status).append(", ")
                .append(currentNavigationPointId).append("->").append(nextNavigationPointId).append(", ETA: ")
                .append(actionEndEta).append(", segments: ").append(routeSegments.size());
        return builder.toString();
    }
}
