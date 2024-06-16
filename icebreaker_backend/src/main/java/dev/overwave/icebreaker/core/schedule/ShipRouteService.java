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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Map<Long, NavigationRequest> navigationRequests = navigationRequestRepository.findAll()
                .stream()
                .collect(Collectors.toMap(NavigationRequest::getId, nr -> nr));
        for (ScheduledShip ship : ships) {
            NavigationRequest navigationRequest = navigationRequests.get(ship.getRequestId());
            if (ship.getStatus() == ScheduleStatus.STUCK) {
                continue;
            }
            if (navigationRequest != null) {
                navigationRequest.setStatus(RequestStatus.APPROVED);
            }
            List<ConfirmedRouteSegment> routeSegments = ship.getRouteSegments();
            IcebreakerLocation icebreakerLocation = icebreakerLocationRepository.findByIcebreakerId(ship.getShipId());
            Instant previousEnd = ship.isIcebreaker() ?
                    icebreakerLocation.getStartDate() : navigationRequest.getStartDate();
            Ship shipEntity = ship.isIcebreaker() ?
                    icebreakerLocation.getIcebreaker() : navigationRequest.getShip();
            for (ConfirmedRouteSegment segment : routeSegments) {
                if (previousEnd.isBefore(segment.interval().start())) {
                    // add waiting
                    ShipRouteEntity waitRoute = ShipRouteEntity.builder()
                            .ship(shipEntity)
                            .navigationRequest(navigationRequest)
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
                        .navigationRequest(navigationRequest)
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
        }
        for (NavigationRequest navigationRequest : navigationRequests.values()) {
            if (navigationRequest.getStatus() != RequestStatus.APPROVED) {
                navigationRequest.setStatus(RequestStatus.REJECTED);
            }
        }
    }
}
