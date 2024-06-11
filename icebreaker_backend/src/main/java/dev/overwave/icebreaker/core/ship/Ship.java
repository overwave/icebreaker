package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.navigation.NavigationRequest;
import dev.overwave.icebreaker.core.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Ship extends LongId {

    private String name;

    @Enumerated(EnumType.STRING)
    private IceClass iceClass;

    private float speed;

    private boolean icebreaker;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "ship")
    private List<NavigationRequest> navigationRequests;
}
