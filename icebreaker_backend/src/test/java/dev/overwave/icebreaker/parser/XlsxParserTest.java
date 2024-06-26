package dev.overwave.icebreaker.parser;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.parser.ScheduleSegment;
import dev.overwave.icebreaker.core.parser.ShipSchedule;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class XlsxParserTest {
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
    void testCreateFileWithGanttDiagram() {
        LocalDate firstDate = LocalDate.of(2024, 3, 3);
        LocalDate lastDate = LocalDate.of(2024, 3, 11);
        String ship1 = "Кораблик Белка";
        List<ScheduleSegment> ship1Segments = List.of(
                new ScheduleSegment(
                        "Носик Родивона",
                        "Жопка Бури",
                        firstDate,
                        LocalDate.of(2024, 3, 4)),
                new ScheduleSegment(
                        "Жопка Бури",
                        "Компуктерное кресло",
                        LocalDate.of(2024, 3, 5),
                        LocalDate.of(2024, 3, 6))
        );
        ShipSchedule schedule1 = new ShipSchedule(ship1, ship1Segments);

        String ship2 = "Кораблик Родивон";
        List<ScheduleSegment> ship2Segments = List.of(
                new ScheduleSegment(
                        "Холодильник",
                        "Кроватка",
                        LocalDate.of(2024, 3, 7),
                        LocalDate.of(2024, 3, 7))

        );
        ShipSchedule schedule2 = new ShipSchedule(ship2, ship2Segments);

        String ship3 = "Кораблик Лизуня";
        List<ScheduleSegment> ship3Segments = List.of(
                new ScheduleSegment(
                        "ПВЗ Озон",
                        "Этлон кофи",
                        LocalDate.of(2024, 3, 10),
                        lastDate)

        );
        ShipSchedule schedule3 = new ShipSchedule(ship3, ship3Segments);

        XlsxParser.createFileWithGanttDiagram(List.of(schedule1, schedule2, schedule3), firstDate, lastDate);
    }

    @Test
    void testParseIntegralVelocityTable() {
        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocityTest.xlsx");

        assertThat(matrix).hasSize(2);
        assertThat(matrix.getFirst()).hasSize(2);
        assertThat(matrix.get(1)).hasSize(2);

        assertThat(matrix.getFirst().getFirst()).isEqualTo(rawVelocityA1);
        assertThat(matrix.getFirst().get(1)).isEqualTo(rawVelocityB1);
        assertThat(matrix.get(1).getFirst()).isEqualTo(rawVelocityA2);
        assertThat(matrix.get(1).get(1)).isEqualTo(rawVelocityB2);
    }

    @Test
    void testParseRosatomIntegrVelocityTable() {
        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocity.xlsx");

        RawVelocity first = matrix.getFirst().getFirst();
        assertThat(first.velocities()).hasSize(14);
        Interval interval1 = first.velocities().getFirst().interval();
        Interval interval2 = first.velocities().get(1).interval();
        assertThat(interval1.instant().isBefore(interval2.instant())).isTrue();
        assertThat(interval1.instant().plus(interval1.duration())).isEqualTo(interval2.instant());
    }

    @Test
    void testParseNavigationPointsTable() {
        List<NavigationPoint> points = XlsxParser.parseNavigationPointsTable(FileUtils.fromClassPath("/ГрафДанные.xlsx"));

        assertThat(points).hasSize(47);
    }
}