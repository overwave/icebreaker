package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NavigationRoute extends LongId {

    @ManyToOne
    @JoinColumn(name = "point_id_1")
    private NavigationPoint point1;

    @ManyToOne
    @JoinColumn(name = "point_id_2")
    private NavigationPoint point2;

    private float rawDistance;
}
