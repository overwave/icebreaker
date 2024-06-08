package dev.overwave.icebreaker.core.map;

import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocityFactory;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

class VelocityLayerGeneratorTest {

    @Test
    @Disabled // requires 20+ Gb ram
    void generateMapTiles() {
        List<List<RawVelocity>> matrix = XlsxParser.parseIntegralVelocityTable("/IntegrVelocity.xlsx");
        List<SpatialVelocity> spatialVelocities = SpatialVelocityFactory.formSpatialVelocityGrid(matrix);
        VelocityLayerGenerator.generateMapTiles(spatialVelocities);
    }
}