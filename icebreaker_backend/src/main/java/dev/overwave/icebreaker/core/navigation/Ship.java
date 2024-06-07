package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.database.LongId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ship")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Ship extends LongId {
    private String name;
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "ice_class")
    private IceClass iceClass;
    private float speed;
    @JoinColumn(name = "is_icebreaker")
    private boolean isIcebreaker;
    //private Point location;
}
