package dev.overwave.icebreaker.core.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRouteRepository extends JpaRepository<ShipRouteEntity, Long> {
    List<ShipRouteEntity> findAllByNavigationRequestIdOrderById(long navigationRequestId);
}
