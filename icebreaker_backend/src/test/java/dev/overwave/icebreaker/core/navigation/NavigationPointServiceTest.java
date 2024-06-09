package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationPointDto;
import dev.overwave.icebreaker.configuration.FunctionalTest;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalTest
@RequiredArgsConstructor
class NavigationPointServiceTest {
    private final NavigationPointService navigationPointService;

    @Test
    void getNavigationPoints() {
        navigationPointService.resetNavigationPoints(FileUtils.fromClassPath("/ГрафДанные.xlsx"));

        List<NavigationPointDto> points = navigationPointService.getNavigationPoints();
        assertThat(points).hasSize(47);
        assertThat(points.getFirst()).usingRecursiveAssertion().ignoringFields("id")
                .isEqualTo(new NavigationPointDto(1, "Бухта Север и Диксон", new Point(73.1F, 80F)));
    }

    @Test
    void resetNavigationPoints() {
        navigationPointService.resetNavigationPoints(FileUtils.fromClassPath("/ГрафДанные.xlsx"));
    }
}