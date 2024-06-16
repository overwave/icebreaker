package dev.overwave.icebreaker.core.lock;


import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Lock extends LongId {
    @Enumerated(EnumType.STRING)
    private LockStatus status;

    private Instant updatedAt;
}
