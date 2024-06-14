package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.exception.NavigationRequestNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NavigationRequestRepository extends JpaRepository<NavigationRequest, Long> {
    List<NavigationRequest> findAllByStatus(RequestStatus status);

    Optional<NavigationRequest> findById(long id);

    default NavigationRequest findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new NavigationRequestNotFoundException(id));
    }
}
