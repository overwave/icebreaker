package dev.overwave.icebreaker.core.geospatial;

import java.util.ArrayList;
import java.util.List;

public class SpatialVelocityFactory {

    public List<SpatialVelocity> formSpatialVelocityNet(List<List<RawVelocity>> velocityMatrix) {
        List<SpatialVelocity> net = new ArrayList<SpatialVelocity>();
        for (int row = 0; row < velocityMatrix.size() - 1; row++) {
            for (int col = 0; col < velocityMatrix.get(row).size() - 1; col++) {
                Point topLeft = velocityMatrix.get(row).get(col).coordinates();
                List<ContinuousVelocity> velocities = velocityMatrix.get(row).get(col).velocities();
                Point topRight = velocityMatrix.get(row).get(col + 1).coordinates();
                Point bottomLeft = velocityMatrix.get(row + 1).get(col).coordinates();
                Point bottomRight = velocityMatrix.get(row + 1).get(col + 1).coordinates();
                SpatialVelocity spatialVelocity = new SpatialVelocity(topLeft, topRight, bottomLeft, bottomRight,
                        velocities);
                net.add(spatialVelocity);
            }
        }
        return net;
    }
}
