package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.navigation.NavigationRequest;
import dev.overwave.icebreaker.core.ship.Ship;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "ship_route")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShipRouteEntity extends LongId {

    @Nullable
    @ManyToOne
    @JoinColumn(name = "navigation_request_id")
    private NavigationRequest navigationRequest;

    @ManyToOne
    @JoinColumn(name = "start_point_id")
    private NavigationPoint startPoint;

    @ManyToOne
    @JoinColumn(name = "finish_point_id")
    private NavigationPoint finishPoint;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

    private Instant startDate;

    private Instant endDate;

    private String points;

    private String companions;
}
