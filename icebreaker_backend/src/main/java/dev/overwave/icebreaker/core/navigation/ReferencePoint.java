package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "reference_point")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReferencePoint extends LongId {
    private String name;
    private float lat;
    private float lon;
}
