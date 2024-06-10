package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VelocityInterval extends LongId {
    private Instant startDate;
    private Instant endDate;
}
