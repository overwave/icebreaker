package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.exception.NavigationPointNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NavigationPointRepository extends JpaRepository<NavigationPoint, Long> {
    Optional<NavigationPoint> findById(long id);

    default NavigationPoint findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new NavigationPointNotFoundException(id));
    }
}
