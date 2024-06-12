package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.navigation.NavigationRequest;
import dev.overwave.icebreaker.core.navigation.NavigationRequestRepository;
import dev.overwave.icebreaker.core.navigation.RequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final NavigationRequestRepository navigationRequestRepository;
    private final IcebreakerLocationRepository icebreakerLocationRepository;

    public void createSchedule() {
        List<IcebreakerLocation> icebreakerLocations = icebreakerLocationRepository.findAll();
        List<NavigationRequest> pendingRequests = navigationRequestRepository.findAllByStatus(RequestStatus.PENDING);

        for (NavigationRequest pendingRequest : pendingRequests) {
            Optional<DefaultRoute> defaultRoute = defaultRoute(pendingRequest);
        }
//                .stream()
//                .sorted(Comparator.comparing(NavigationRequest::getStartDate))
//                .toList();

//        while (!pendingRequests.isEmpty()) {
//
//        }
//
//        Map<Instant, List<NavigationRequest>> navigationRequestsByStart = pendingRequests.stream()
//                .collect(Collectors.groupingBy(NavigationRequest::getStartDate));
    }

    private Optional<DefaultRoute> defaultRoute(NavigationRequest pendingRequest) {
        NavigationPoint startPoint = pendingRequest.getStartPoint();
        NavigationPoint finishPoint = pendingRequest.getFinishPoint();

        PriorityQueue<Entry<NavigationPoint, Integer>> queue =
                new PriorityQueue<>(Comparator.comparingInt(Entry::getValue));
        queue.add(Map.entry(startPoint, 0));

        Map<NavigationPoint, RouteSegment> routeSegments = new HashMap<>();
        routeSegments.put(startPoint, new RouteSegment(0, null));

        while (!queue.isEmpty()) {
            NavigationPoint current = queue.poll().getKey();

            if (startPoint.equals(finishPoint)) {
                return Optional.of(buildRoute(pendingRequest.getStartDate(), routeSegments, current));
            }
        }
        return Optional.empty();
    }

    private DefaultRoute buildRoute(Instant startDate, Map<NavigationPoint, RouteSegment> routeSegments,
                                    NavigationPoint endPoint) {
        return new DefaultRoute(null, null, 0);
    }

    private record RouteSegment(
            long duration,
            NavigationPoint previous
    ) {
    }

    private record DefaultRoute(
            Interval interval,
            List<NavigationPoint> navigationPoints,
            float distance
    ) {
    }
}

