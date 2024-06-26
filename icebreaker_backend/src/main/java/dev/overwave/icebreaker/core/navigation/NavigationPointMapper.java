package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import dev.overwave.icebreaker.core.geospatial.Point;
import org.springframework.stereotype.Component;

@Component
public class NavigationPointMapper {

    public NavigationPointDto toNavigationPointDto(NavigationPoint point) {
        return new NavigationPointDto(point.getId(), point.getName(), new Point(point.getLat(), point.getLon()));
    }

    public NavigationPointStatic toNavigationPointStatic(NavigationPoint point) {
        return NavigationPointStatic.builder()
                .id(point.getId())
                .name(point.getName())
                .point(point.getPoint())
                .routeIds(point.getRoutes().stream().map(NavigationRoute::getId).toList())
                .build();
    }
}
