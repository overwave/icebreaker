package dev.overwave.icebreaker.parser;

import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class XlsxParserTest {
    private final XlsxParser xlsxParser = new XlsxParser();

    @Test
    void testParseIntegralVelocityOfIce() {
        List<List<RawVelocity>> matrix = xlsxParser.parseIntegralVelocityOfIce("src/main/resources/IntegrVelocityTest.xlsx");
        assertThat(matrix).hasSize(2);
    }
}