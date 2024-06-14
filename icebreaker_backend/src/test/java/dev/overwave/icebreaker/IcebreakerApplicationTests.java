package dev.overwave.icebreaker;

import dev.overwave.icebreaker.configuration.FunctionalTest;
import lombok.RequiredArgsConstructor;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import net.sf.geographiclib.GeodesicMask;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
@FunctionalTest
class IcebreakerApplicationTests {
    @Test
    void contextLoads() {
        double lat1 = 70, lon1 = 20, lat2 = 76, lon2 = 50;
        GeodesicLine line = Geodesic.WGS84.InverseLine(lat1, lon1, lat2, lon2,
                GeodesicMask.DISTANCE_IN | GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        float ratio = 2 / 7F;
        GeodesicData g = line.ArcPosition(line.Arc() * ratio, GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        System.out.println(g.lat2 + " " + g.lon2);
    }
}