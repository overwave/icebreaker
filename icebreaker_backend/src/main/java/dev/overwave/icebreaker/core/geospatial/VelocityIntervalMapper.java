package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.api.navigation.VelocityIntervalDto;
import org.springframework.stereotype.Component;

@Component
public class VelocityIntervalMapper {

    public VelocityIntervalDto toVelocityIntervalDto(VelocityInterval interval) {
        return new VelocityIntervalDto(
                interval.getStartDate(),
                interval.getEndDate());
    }
}
