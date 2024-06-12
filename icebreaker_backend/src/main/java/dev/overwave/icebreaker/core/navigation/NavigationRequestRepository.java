package dev.overwave.icebreaker.core.navigation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NavigationRequestRepository extends JpaRepository<NavigationRequest, Long> {
    List<NavigationRequest> findAllByStatus(RequestStatus status);
}
