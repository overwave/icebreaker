package dev.overwave.icebreaker.core.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.core.navigation.NavigationPointRepository;
import dev.overwave.icebreaker.core.navigation.NavigationRequest;
import dev.overwave.icebreaker.core.navigation.NavigationRequestRepository;
import dev.overwave.icebreaker.core.navigation.RequestStatus;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipRouteService {
    private final IcebreakerLocationRepository icebreakerLocationRepository;
    private final NavigationPointRepository navigationPointRepository;
    private final NavigationRequestRepository navigationRequestRepository;
    private final ShipRouteRepository shipRouteRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void saveRoutes(List<ScheduledShip> ships) {
        for (ScheduledShip ship : ships) {
            Optional<NavigationRequest> navigationRequestO = navigationRequestRepository.findById(ship.getRequestId());
            if (ship.getStatus() == ScheduleStatus.STUCK) {
                navigationRequestO.ifPresent(nr -> nr.setStatus(RequestStatus.REJECTED));
                continue;
            }
            List<ConfirmedRouteSegment> routeSegments = ship.getRouteSegments();
            IcebreakerLocation icebreakerLocation = icebreakerLocationRepository.findByIcebreakerId(ship.getShipId());
            Instant previousEnd = ship.isIcebreaker() ?
                    icebreakerLocation.getStartDate() : navigationRequestO.orElseThrow().getStartDate();
            Ship shipEntity = ship.isIcebreaker() ?
                    icebreakerLocation.getIcebreaker() : navigationRequestO.orElseThrow().getShip();
            for (ConfirmedRouteSegment segment : routeSegments) {
                if (previousEnd.isBefore(segment.interval().start())) {
                    // add waiting
                    ShipRouteEntity waitRoute = ShipRouteEntity.builder()
                            .ship(shipEntity)
                            .navigationRequest(navigationRequestO.orElse(null))
                            .startPoint(navigationPointRepository.findByIdOrThrow(segment.from().id()))
                            .finishPoint(navigationPointRepository.findByIdOrThrow(segment.from().id()))
                            .startDate(previousEnd)
                            .endDate(segment.interval().start())
                            .points("[]")
                            .companions("[]")
                            .build();
                    shipRouteRepository.save(waitRoute);
                }

                List<Long> otherShipIds = Optional.ofNullable(segment.otherShips()).stream()
                        .flatMap(List::stream).map(ShipStatic::id).toList();
                ShipRouteEntity shipRoute = ShipRouteEntity.builder()
                        .ship(shipEntity)
                        .navigationRequest(navigationRequestO.orElse(null))
                        .startPoint(navigationPointRepository.findByIdOrThrow(segment.from().id()))
                        .finishPoint(navigationPointRepository.findByIdOrThrow(segment.to().id()))
                        .startDate(segment.interval().start())
                        .endDate(segment.interval().end())
                        .points(objectMapper.writeValueAsString(segment.points()))
                        .companions(objectMapper.writeValueAsString(otherShipIds))
                        .build();
                shipRouteRepository.save(shipRoute);
                previousEnd = segment.interval().end();
            }
            navigationRequestO.ifPresent(nr -> nr.setStatus(RequestStatus.APPROVED));
        }
    }
}
