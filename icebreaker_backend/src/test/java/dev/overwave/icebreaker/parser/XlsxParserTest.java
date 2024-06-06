package dev.overwave.icebreaker.parser;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class XlsxParserTest {
    private final XlsxParser xlsxParser = new XlsxParser();
    private final Instant date1 = Instant.parse("2020-03-03T00:00:00Z");
    private final Instant date2 = Instant.parse("2020-03-10T00:00:00Z");
    private final Interval interval1 = new Interval(date1, Duration.ofDays(7));
    private final Interval interval2 = new Interval(date2, Duration.ofDays(7));

    private final List<ContinuousVelocity> velocitiesA1 = List.of(
            new ContinuousVelocity(-10F, interval1),
            new ContinuousVelocity(15F, interval2));
    private final List<ContinuousVelocity> velocitiesA2 = List.of(
            new ContinuousVelocity(0F, interval1),
            new ContinuousVelocity(20F, interval2));
    private final List<ContinuousVelocity> velocitiesB1 = List.of(
            new ContinuousVelocity(21F, interval1),
            new ContinuousVelocity(-10F, interval2));
    private final List<ContinuousVelocity> velocitiesB2 = List.of(
            new ContinuousVelocity(10F, interval1),
            new ContinuousVelocity(16F, interval2));

    private final RawVelocity rawVelocityA1 = new RawVelocity(new Point(41.5F, 21.1F), velocitiesA1);
    private final RawVelocity rawVelocityA2 = new RawVelocity(new Point(44.8F, 23.2F), velocitiesA2);
    private final RawVelocity rawVelocityB1 = new RawVelocity(new Point(42.6F, 26.4F), velocitiesB1);
    private final RawVelocity rawVelocityB2 = new RawVelocity(new Point(43.7F, 24.5F), velocitiesB2);

    @Test
    void testParseIntegralVelocityOfIce() {
        List<List<RawVelocity>> matrix = xlsxParser.parseIntegralVelocityOfIce("src/test/resources/IntegrVelocityTest" +
                ".xlsx");

        assertThat(matrix).hasSize(2);
        assertThat(matrix.getFirst()).hasSize(2);
        assertThat(matrix.get(1)).hasSize(2);

        assertThat(matrix.getFirst().getFirst()).isEqualTo(rawVelocityA1);
        assertThat(matrix.getFirst().get(1)).isEqualTo(rawVelocityB1);
        assertThat(matrix.get(1).getFirst()).isEqualTo(rawVelocityA2);
        assertThat(matrix.get(1).get(1)).isEqualTo(rawVelocityB2);
    }

    @Test
    void testParseRosatomIntegrVelocity() {
        List<List<RawVelocity>> matrix = xlsxParser.parseIntegralVelocityOfIce("src/test/resources/IntegrVelocity" +
                ".xlsx");

        assertThat(matrix.getFirst().getFirst().velocities()).hasSize(14);
    }
}