package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.api.navigation.VelocityIntervalDto;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VelocityIntervalService {
    private final VelocityIntervalRepository velocityIntervalRepository;
    private final VelocityIntervalMapper velocityIntervalMapper;

    public List<VelocityIntervalDto> getVelocityIntervals() {
        List<VelocityInterval> intervals = velocityIntervalRepository.findAll();
        return intervals.stream()
                .map(velocityIntervalMapper::toVelocityIntervalDto)
                .toList();
    }

    public void resetIntegralVelocities(InputStream inputStream) {
        //TODO
        List<List<RawVelocity>> unsavedRawVelocities = XlsxParser.parseIntegralVelocityTable(inputStream);
        RawVelocity first = unsavedRawVelocities.getFirst().getFirst();

        List<VelocityInterval> unsavedVelocityIntervals = new ArrayList<>();
        for (ContinuousVelocity continuousVelocity : first.velocities()) {
            Interval interval = continuousVelocity.interval();
            unsavedVelocityIntervals.add(
                    VelocityInterval.builder()
                            .startDate(interval.instant())
                            .endDate(interval.instant().plus(interval.duration()))
                            .build());
        }
        velocityIntervalRepository.deleteAll();
        List<VelocityInterval> points = velocityIntervalRepository.saveAllAndFlush(unsavedVelocityIntervals);
    }
}
