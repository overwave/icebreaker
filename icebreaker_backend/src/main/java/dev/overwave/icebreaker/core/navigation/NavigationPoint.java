package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NavigationPoint extends LongId {

    private int externalId;

    private String name;

    private float lat;

    private float lon;

    @OneToMany(mappedBy = "point1")
    private List<NavigationRoute> routes1;

    @OneToMany(mappedBy = "point2")
    private List<NavigationRoute> routes2;
}
