package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "navigation_request")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NavigationRequest extends LongId {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_id", nullable = false)
    @ToString.Exclude
    private Ship ship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_point_id", nullable = false)
    @ToString.Exclude
    private ReferencePoint startPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finish_point_id", nullable = false)
    @ToString.Exclude
    private ReferencePoint finishPoint;

    @JoinColumn(name = "start_date")
    private LocalDateTime startDate;
}
