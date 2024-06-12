package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.navigation.NavigationRoute;
import dev.overwave.icebreaker.core.ship.IceClassGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "default_route")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DefaultRoute extends LongId {
    @ManyToOne
    @JoinColumn(name = "navigation_route_id")
    private NavigationRoute edge;

    @ManyToOne
    @JoinColumn(name = "velocity_interval_id")
    private VelocityInterval velocityInterval;

    @Enumerated(EnumType.STRING)
    private IceClassGroup iceClassGroup;

    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    private long travelTimeMinutes;

    private float distance;

    private boolean possible;

    private String nodes;
}
