package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NavigationPointService {
    private final NavigationPointRepository navigationPointRepository;
    private final NavigationPointMapper mapper;
    private final XlsxParser xlsxParser;

    public List<NavigationPointDto> getNavigationPoints() {
        List<NavigationPoint> points = navigationPointRepository.findAll();
        return points.stream()
                .map(mapper::toNavigationPointDto)
                .toList();
    }

    public void resetNavigationPoints(File file) {
        List<NavigationPoint> points = xlsxParser.parseNavigationPointsTable(file);
        navigationPointRepository.deleteAll();
        navigationPointRepository.saveAll(points);
    }
}
