package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.database.LongId;
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
@Table(name = "navigation_request")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NavigationRequest extends LongId {
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private Ship ship;

    @ManyToOne
    @JoinColumn(name = "start_point_id")
    private NavigationPoint startPoint;

    @ManyToOne
    @JoinColumn(name = "finish_point_id")
    private NavigationPoint finishPoint;

    private Instant startDate;
}
