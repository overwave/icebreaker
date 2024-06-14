package dev.overwave.icebreaker.core.schedule;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class ScheduledShip {
    private long shipId;
    private long currentNavigationPointId;
    private ScheduleStatus status;
    private boolean icebreaker;
    private long nextNavigationPointId;
    private Long finishNavigationPointId;
    private Instant actionEndEta;
    private List<ConvoyRequest> convoyRequests;
}
