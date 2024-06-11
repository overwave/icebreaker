package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.Entity;
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
public class VelocityInterval extends LongId {
    private Instant startDate;
    private Instant endDate;
}
