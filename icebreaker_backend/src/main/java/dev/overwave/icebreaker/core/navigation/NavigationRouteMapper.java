package dev.overwave.icebreaker.core.navigation;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NavigationRouteMapper {
    public NavigationRouteStatic toNavigationRouteStatic(NavigationRoute route) {
        return NavigationRouteStatic.builder()
                .id(route.getId())
                .pointIds(Map.entry(route.getPoint1().getId(), route.getPoint2().getId()))
                .build();
    }
}
