package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.navigation.NavigationRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShipRoute extends LongId {
    @ManyToOne
    @JoinColumn(name = "navigation_request_id")
    private NavigationRequest navigationRequest;

    @ManyToOne
    @JoinColumn(name = "start_point_id")
    private NavigationPoint startPoint;

    @ManyToOne
    @JoinColumn(name = "finish_point_id")
    private NavigationPoint finishPoint;

    private Instant startDate;
    private Instant endDate;
}
