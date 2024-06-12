package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.api.navigation.VelocityIntervalDto;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.core.serialization.SerializationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
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
        // парсим данные о ледовой проходимости
        List<List<RawVelocity>> unsavedRawVelocities = XlsxParser.parseIntegralVelocityTable(inputStream);
        RawVelocity first = unsavedRawVelocities.getFirst().getFirst();
        // получаем список всех новых интервалов и обновляем данные в бд
        List<VelocityInterval> unsavedVelocityIntervals = first.velocities().stream()
                .map(ContinuousVelocity::interval)
                .map(interval -> VelocityInterval.builder()
                        .startDate(interval.instant())
                        .endDate(interval.instant().plus(interval.duration()))
                        .build())
                .toList();
        // пишем в файл новые данные о ледовой проходимости в виде сетки
        List<SpatialVelocity> spatialVelocities = SpatialVelocityFactory.formSpatialVelocityGrid(unsavedRawVelocities);
        SerializationUtils.writeSpatial(spatialVelocities, "data/spatial_velocities.l4z");

        Graph graph = GraphFactory.buildWeightedGraph(spatialVelocities);
        SerializationUtils.writeWeightedGraph(graph, "data/graph.l4z");

        velocityIntervalRepository.saveAllAndFlush(unsavedVelocityIntervals);
    }
}
