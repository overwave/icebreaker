package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.core.exception.ShipNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    Optional<Ship> findById(long id);

    default Ship findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new ShipNotFoundException(id));
    }

    List<Ship> findAllByUserId(Long id);
}
