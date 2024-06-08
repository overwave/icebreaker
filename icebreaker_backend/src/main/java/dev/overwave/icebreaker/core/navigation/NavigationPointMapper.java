package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import org.springframework.stereotype.Component;

@Component
public class NavigationPointMapper {

    public NavigationPointDto toNavigationPointDto(NavigationPoint point) {
        return new NavigationPointDto(point.getId(), point.getName(), point.getLat(), point.getLon());
    }
}
