package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.ReferencePointDto;
import org.springframework.stereotype.Component;

@Component
public class ReferencePointMapper {

    public ReferencePointDto toReferencePointDto(ReferencePoint point) {
        return new ReferencePointDto(point.getName(), point.getLat(), point.getLon());
    }
}
