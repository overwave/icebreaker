package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.ship.Ship;
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
public class IcebreakerLocation extends LongId {
    @ManyToOne
    @JoinColumn(name = "icebreaker_id")
    private Ship icebreaker;

    @ManyToOne
    @JoinColumn(name = "point_id")
    private NavigationPoint startPoint;

    private Instant startDate;
}
