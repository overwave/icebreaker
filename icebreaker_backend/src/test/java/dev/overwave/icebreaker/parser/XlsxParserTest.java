package dev.overwave.icebreaker.parser;

import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.core.util.RawVelocity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XlsxParserTest {
private final XlsxParser xlsxParser = new XlsxParser();
    @Test
    void testParseIntegralVelocityOfIce() {
        List<List<RawVelocity>> matrix = xlsxParser.parseIntegralVelocityOfIce("src/main/resources/IntegrVelocityTest.xlsx");
        assertEquals(2, matrix.size());
    }
}