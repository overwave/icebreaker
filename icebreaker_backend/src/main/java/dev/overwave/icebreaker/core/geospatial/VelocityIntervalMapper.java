package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.api.navigation.VelocityIntervalDto;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class VelocityIntervalMapper {

    public VelocityIntervalDto toVelocityIntervalDto(VelocityInterval interval) {
        return new VelocityIntervalDto(
                interval.getStartDate(),
                interval.getEndDate());
    }

    public VelocityIntervalStatic toVelocityIntervalStatic(VelocityInterval velocityInterval) {
        Duration duration = Duration.between(velocityInterval.getStartDate(), velocityInterval.getEndDate());
        Interval interval = new Interval(velocityInterval.getStartDate(), duration);
        return VelocityIntervalStatic.builder()
                .id(velocityInterval.getId())
                .interval(interval)
                .build();
    }
}
