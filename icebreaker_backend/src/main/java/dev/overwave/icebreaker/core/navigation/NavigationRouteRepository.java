package dev.overwave.icebreaker.core.navigation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NavigationRouteRepository extends JpaRepository<NavigationRoute, Long> {
    List<NavigationRoute> findAllByPoint1IdOrPoint2Id(long point1Id, long point2Id);
}
