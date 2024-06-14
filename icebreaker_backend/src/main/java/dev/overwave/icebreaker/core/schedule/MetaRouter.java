package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalStatic;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRouteStatic;
import dev.overwave.icebreaker.core.route.DefaultRouteStatic;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.SequencedCollection;

@Service
@RequiredArgsConstructor
public class MetaRouter {
    public List<RoutePredictionSegment> createRoute(NavigationPointStatic from, NavigationPointStatic to,
                                                    ShipStatic ship, Instant startAt, MetaRouteContext context) {
        PriorityQueue<Entry<NavigationPointStatic, Duration>> queue = new PriorityQueue<>(Entry.comparingByValue());
        queue.add(Map.entry(from, Duration.ZERO));

        Map<NavigationPointStatic, RouteSegment> routeSegments = new HashMap<>();
        routeSegments.put(from, new RouteSegment(Duration.ZERO, null, -1, false));

        while (!queue.isEmpty()) {
            NavigationPointStatic current = queue.poll().getKey();
            if (current.equals(to)) {
                return buildRoute(startAt, routeSegments, current, context);
            }

            for (long routeId : current.routeIds()) {
                Duration travelTime = routeSegments.get(current).duration();

                DefaultRouteStatic defaultRoute = findDefaultRoute(context, routeId, startAt.plus(travelTime), ship);
                if (defaultRoute == null) {
                    continue;
                }

                int routeTravelMinutes = (int) (defaultRoute.travelTime().toMinutes() * ship.getRelativeSpeed());
                Duration segmentDuration = travelTime.plusMinutes(routeTravelMinutes);

                NavigationPointStatic nextPoint = getOtherPoint(current, routeId, context);
                RouteSegment nextSegment = routeSegments.get(nextPoint);

                if (nextSegment == null || nextSegment.duration().compareTo(segmentDuration) > 0) {
                    queue.add(Map.entry(nextPoint, segmentDuration));
                    routeSegments.put(nextPoint, new RouteSegment(segmentDuration, current, routeId,
                            defaultRoute.movementType() == MovementType.FOLLOWING));
                }
            }
        }
        return List.of();
    }

    private List<RoutePredictionSegment> buildRoute(Instant startDate,
                                                    Map<NavigationPointStatic, RouteSegment> routeSegments,
                                                    NavigationPointStatic point, MetaRouteContext context) {
        List<RoutePredictionSegment> routes = new LinkedList<>();
//        Duration duration = routeSegments.get(point).duration();
        NavigationPointStatic cursor = point;
        while (cursor != null) {
            RouteSegment routeSegment = routeSegments.get(cursor);
            NavigationPointStatic previous = routeSegment.previous();
            if (previous != null) {
                routes.add(new RoutePredictionSegment(previous, cursor, routeSegment.icebreaker(),
                        new Interval(null, routeSegment.duration())));
//                duration = duration.plus(routeSegment.duration());
            }
            cursor = previous;
        }
        List<RoutePredictionSegment> directRoute = new ArrayList<>();
        Duration previous = Duration.ZERO;
        for (RoutePredictionSegment route : routes.reversed()) {
            Duration routeDuration = route.interval().duration();
            RoutePredictionSegment routePredictionSegment = new RoutePredictionSegment(route.from(), route.to(),
                    route.convoy(), new Interval(startDate.plus(previous), routeDuration.minus(previous)));
            directRoute.add(routePredictionSegment);
            previous = route.interval().duration();
        }
        return directRoute;
    }

    private NavigationPointStatic getOtherPoint(NavigationPointStatic thisPoint,
                                                long routeId, MetaRouteContext context) {
        NavigationRouteStatic route = context.routes().get(routeId);
        long nextPointId = route.getOther(thisPoint.id());
        return context.points().get(nextPointId);
    }

    private DefaultRouteStatic findDefaultRoute(MetaRouteContext context, long routeId,
                                                Instant instant, ShipStatic ship) {
        Instant reducedInstant = getReducedInstant(context, instant);
        List<DefaultRouteStatic> defaultRoutes = context.defaultRoutesByRouteId().get(routeId);
        if (defaultRoutes == null) {
            return null;
        }
        for (DefaultRouteStatic defaultRoute : defaultRoutes) {
            if (!defaultRoute.interval().contains(reducedInstant)) {
                continue;
            }
            if (defaultRoute.iceClassGroup() != ship.iceClass().getGroup()) {
                continue;
            }
            return defaultRoute;
        }
        return null;
    }

    private Instant getReducedInstant(MetaRouteContext context, Instant instant) {
        SequencedCollection<VelocityIntervalStatic> intervals =
                (SequencedCollection<VelocityIntervalStatic>) context.velocities().values();
        if (instant.compareTo(intervals.getFirst().interval().start()) <= 0) {
            return intervals.getFirst().interval().start();
        }
        if (instant.compareTo(intervals.getLast().interval().end()) >= 0) {
            return intervals.getLast().interval().start(); // start indeed
        }
        return instant;
    }

    private record RouteSegment(
            Duration duration,
            NavigationPointStatic previous,
            long routeId,
            boolean icebreaker) {
    }
}
