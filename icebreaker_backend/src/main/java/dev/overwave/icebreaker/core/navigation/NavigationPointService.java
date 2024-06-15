package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import dev.overwave.icebreaker.core.exception.NavigationPointsInfoAlreadyExists;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.core.schedule.ContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavigationPointService {
    private final NavigationPointRepository navigationPointRepository;
    private final NavigationRouteRepository navigationRouteRepository;
    private final ContextHolder contextHolder;
    private final NavigationPointMapper mapper;

    public List<NavigationPointDto> getNavigationPoints() {
        List<NavigationPoint> points = navigationPointRepository.findAll();
        return points.stream()
                .map(mapper::toNavigationPointDto)
                .toList();
    }

    public void resetNavigationPoints(InputStream inputStream) {
        if (navigationPointRepository.count() > 0) {
            throw new NavigationPointsInfoAlreadyExists();
        }
        List<NavigationPoint> unsavedPoints = XlsxParser.parseNavigationPointsTable(inputStream);
        navigationPointRepository.deleteAll();
        navigationRouteRepository.deleteAll();
        List<NavigationPoint> points = navigationPointRepository.saveAllAndFlush(unsavedPoints);
        Map<Integer, NavigationPoint> pointByExternalId = points.stream()
                .collect(Collectors.toMap(NavigationPoint::getExternalId, Function.identity()));

        List<NavigationRoute> navigationRoutes =
                unsavedPoints.stream().flatMap(point -> point.getRoutes1().stream()).toList();
        for (NavigationRoute route : navigationRoutes) {
            route.setPoint1(pointByExternalId.get(route.getPoint1().getExternalId()));
            route.setPoint2(pointByExternalId.get(route.getPoint2().getExternalId()));
        }
        navigationRouteRepository.saveAllAndFlush(navigationRoutes);
        new Thread(contextHolder::readContext).start();
    }
}
