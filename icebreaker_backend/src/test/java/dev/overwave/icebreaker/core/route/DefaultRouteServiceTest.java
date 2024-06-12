package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.configuration.FunctionalTest;
import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalRepository;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.navigation.NavigationPointService;
import dev.overwave.icebreaker.core.serialization.SerializationUtils;
import dev.overwave.icebreaker.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalTest
@RequiredArgsConstructor
public class DefaultRouteServiceTest {
    private final DefaultRouteService defaultRouteService;
    private final NavigationPointService navigationPointService;
    private final DefaultRouteRepository defaultRouteRepository;
    private final VelocityIntervalRepository velocityIntervalRepository;

    @Test
    void testCreateAllDefaultRoutes() {
        setup();
        defaultRouteService.createAllDefaultRoutes();
        Optional<DefaultRoute> defaultRoute = defaultRouteRepository.findById(1L);
        assertThat(defaultRoute).isPresent();
        System.out.println(defaultRoute.get());
    }


    private void setup() {
        Graph graph = SerializationUtils.readWeightedGraph("data/graph.lz4");
        List<VelocityInterval> intervals = graph.getFirstVelocities().stream()
                .map(ContinuousVelocity::interval)
                .map(interval -> new VelocityInterval(interval.instant(), interval.instant().plus(interval.duration())))
                .toList();
        velocityIntervalRepository.saveAll(intervals);
        navigationPointService.resetNavigationPoints(FileUtils.fromClassPath("/ГрафДанные.xlsx"));
    }
}
