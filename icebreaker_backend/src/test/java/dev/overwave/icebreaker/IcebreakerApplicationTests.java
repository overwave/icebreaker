package dev.overwave.icebreaker;

import dev.overwave.icebreaker.configuration.FunctionalTest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import net.sf.geographiclib.GeodesicMask;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.GeodeticCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

@RequiredArgsConstructor
@FunctionalTest
class IcebreakerApplicationTests {
    private final ApplicationContext applicationContext;


    double getDistance(double fromLat, double fromLon, double toLat, double toLon) {


        var calculator = GeodeticCalculator.create(CommonCRS.WGS84.universal(+90 /*Северное полушарие*/, 0));
        calculator.setStartGeographicPoint(fromLat, fromLon);
        calculator.setEndGeographicPoint(toLat, toLon);
//        System.out.println(calculator.getGeodesicDistance());
        return calculator.getGeodesicDistance();
    }

    @Test
    @SneakyThrows
    void contextLoads2() {
        Geodesic geod = Geodesic.WGS84;
        double lat1 = 70, lon1 = 20, lat2 = 76, lon2 = 50;
        GeodesicLine line = geod.InverseLine(lat1, lon1, lat2, lon2,
                GeodesicMask.DISTANCE_IN | GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        double ds0 = 500e3;     // Nominal distance between points = 500 km
        // The number of intervals
        int num = (int) (Math.ceil(line.Distance() / ds0));
        {
            // Slightly faster, use intervals of equal arc length
            double da = line.Arc() / num;
            for (int i = 0; i <= num; ++i) {
                GeodesicData g = line.ArcPosition(i * da,
                        GeodesicMask.LATITUDE |
                        GeodesicMask.LONGITUDE);
                System.out.println(i + " " + g.lat2 + " " + g.lon2);
            }
        }
    }

    @Test
    @SneakyThrows
    void contextLoads() {

//        CoordinateReferenceSystem crs = CommonCRS.WGS84.universal(40, 14);  // UTM zone for 40°N 14°E.
//
//        CoordinateReferenceSystem sourceCRS = CommonCRS.WGS84.spherical();
//        CoordinateReferenceSystem targetCRS = CommonCRS.WGS84.universal(40, 14);  // UTM zone for 40°N 14°E.
//        CoordinateOperation operation = CRS.findOperation(sourceCRS, targetCRS, null);
//        /*
//         * The above lines are costly and should be performed only once before to project many points.
//         * In this example, the operation that we got is valid for coordinates in geographic area from
//         * 12°E to 18°E (UTM zone 33) and 0°N to 84°N.
//         */
//        System.out.println("Domain of validity:");
//        System.out.println(CRS.getGeographicBoundingBox(operation));
//
//        DirectPosition ptSrc = new DirectPosition2D(40, 14);           // 40°N 14°E
//        DirectPosition ptDst = operation.getMathTransform().transform(ptSrc, null);
//
//        System.out.println("Source: " + ptSrc);
//        System.out.println("Target: " + ptDst);

        System.out.println(getDistance(70, 20, 73, 35));
        System.out.println(getDistance(73, 35, 76, 50));
        System.out.println(getDistance(70, 20, 73, 35) + getDistance(73, 35, 76, 50));
        System.out.println(getDistance(70, 20, 76, 50));

//        var calculator = GeodeticCalculator.create(CommonCRS.WGS84.universal(+90 /*Северное полушарие*/, 0));
//        var calculator = GeodeticCalculator.create(CommonCRS.WGS84.universal(71.5, 22));
//        var calculator = GeodeticCalculator.create(CommonCRS.WGS84.geographic());
//        calculator.setStartGeographicPoint(70, 20);
//        calculator.setEndGeographicPoint(73, 35);
//        System.out.printf("Result of geodetic calculation: %s%n", calculator);
//        System.out.println(calculator.getGeodesicDistance());
//        System.out.println(calculator.getGeodesicDistance() / 1852);

//        double d;
//        d = calculator.getRhumblineLength();
//        d -= calculator.getGeodesicDistance();
//        System.out.printf("The rhumbline is %1.2f %s longer%n", d, calculator.getDistanceUnit());
//
//        Shape path = calculator.createGeodesicPath2D(100);
//        System.out.printf("Java2D shape class for approximating this path: %s%n", path.getClass());
//
//
//
//
//        CoordinateReferenceSystem crs1 = CommonCRS.WGS84.universal(40, 10);     // 40°N 10°E
//        CoordinateReferenceSystem crs2 = CommonCRS.WGS84.universal(40, 20);     // 40°N 20°E
//
//        Envelope2D bbox1 = new Envelope2D(crs1, 500_000, 400_000, 100_000, 100_000);
//        Envelope2D bbox2 = new Envelope2D(crs2, 400_000, 500_000, 100_000, 100_000);
//        Envelope union = Envelopes.union(bbox1, bbox2);
//
//        System.out.println("First CRS:    " + crs1.getName());
//        System.out.println("Second CRS:   " + crs2.getName());
//        System.out.println("Selected CRS: " + union.getCoordinateReferenceSystem().getName());
//        System.out.println("Union result: " + union);
//
//        assertThat(applicationContext).isNotNull();
    }

}