package dev.overwave.icebreaker.core.geospatial;

import java.util.ArrayList;
import java.util.List;

public class SpatialVelocityFactory {

    public List<SpatialVelocity> formSpatialVelocityGrid(List<List<RawVelocity>> velocityMatrix) {
        List<SpatialVelocity> grid = new ArrayList<>(velocityMatrix.size() * velocityMatrix.getFirst().size());
        for (int row = 0; row < velocityMatrix.size() - 1; row++) {
            for (int col = 0; col < velocityMatrix.get(row).size() - 1; col++) {
                Point topLeft = velocityMatrix.get(row).get(col).coordinates();
                Point topRight = velocityMatrix.get(row).get(col + 1).coordinates();
                Point bottomRight = velocityMatrix.get(row + 1).get(col + 1).coordinates();
                Point bottomLeft = velocityMatrix.get(row + 1).get(col).coordinates();
                List<ContinuousVelocity> velocities = velocityMatrix.get(row).get(col).velocities();
                SpatialVelocity spatialVelocity = SpatialVelocity.builder()
                        .topLeft(topLeft)
                        .topRight(topRight)
                        .bottomLeft(bottomLeft)
                        .bottomRight(bottomRight)
                        .velocities(velocities)
                        .build();
                grid.add(spatialVelocity);
            }
        }
        return grid;
    }
}
