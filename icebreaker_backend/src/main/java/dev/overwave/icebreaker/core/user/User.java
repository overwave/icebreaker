package dev.overwave.icebreaker.core.user;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.ship.Ship;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "user_")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends LongId {

    private String login;

    private String password;

    private String name;

    @Type(StringArrayType.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "roles", columnDefinition = "TEXT[]")
    private UserRole[] roles;

    @OneToMany(mappedBy = "user")
    private List<Ship> ships;


    public List<UserRole> getRoles() {
        return List.of(roles);
    }
}