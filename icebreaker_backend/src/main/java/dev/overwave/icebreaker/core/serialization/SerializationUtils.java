package dev.overwave.icebreaker.core.serialization;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class SerializationUtils {

    @SneakyThrows
    public void writeSpatial(List<SpatialVelocity> spatialVelocities, String path) {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path))) {
            outputStream.writeInt(spatialVelocities.size());
            outputStream.writeShort(spatialVelocities.getFirst().velocities().size());
            Map<Interval, Integer> intervals = new LinkedHashMap<>();
            for (ContinuousVelocity velocity : spatialVelocities.getFirst().velocities()) {
                Interval interval = velocity.interval();
                intervals.put(interval, intervals.size());
                outputStream.writeLong(interval.instant().getEpochSecond());
                outputStream.writeLong(interval.duration().getSeconds());
            }
            for (SpatialVelocity spatialVelocity : spatialVelocities) {
                writePoint(outputStream, spatialVelocity.topLeft());
                writePoint(outputStream, spatialVelocity.topRight());
                writePoint(outputStream, spatialVelocity.bottomLeft());
                writePoint(outputStream, spatialVelocity.bottomRight());
                for (ContinuousVelocity velocity : spatialVelocity.velocities()) {
                    outputStream.writeFloat(velocity.velocity());
                    outputStream.writeShort(intervals.get(velocity.interval()));
                }
            }
        }
    }

    @SneakyThrows
    private void writePoint(DataOutputStream outputStream, Point point) {
        outputStream.writeFloat(point.lat());
        outputStream.writeFloat(point.lon());
    }

    @SneakyThrows
    private Point readPoint(DataInputStream inputStream) {
        return new Point(inputStream.readFloat(), inputStream.readFloat());
    }

    @SneakyThrows
    public List<SpatialVelocity> readSpatial(String path) {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(path))) {
            int spatialVelocitiesSize = inputStream.readInt();
            int intervalSize = inputStream.readShort();
            List<Interval> intervals = new ArrayList<>(intervalSize);
            for (int j = 0; j < intervalSize; j++) {
                intervals.add(new Interval(Instant.ofEpochSecond(inputStream.readLong()),
                        Duration.ofSeconds(inputStream.readLong())));
            }
            List<SpatialVelocity> spatialVelocities = new ArrayList<>(spatialVelocitiesSize);
            for (int i = 0; i < spatialVelocitiesSize; i++) {
                SpatialVelocity spatialVelocity = new SpatialVelocity(
                        readPoint(inputStream),
                        readPoint(inputStream),
                        readPoint(inputStream),
                        readPoint(inputStream),
                        new ArrayList<>(intervalSize)
                );
                for (int j = 0; j < intervalSize; j++) {
                    spatialVelocity.velocities().add(new ContinuousVelocity(
                            inputStream.readFloat(),
                            intervals.get(inputStream.readShort())
                    ));
                }
                spatialVelocities.add(spatialVelocity);
            }
            return spatialVelocities;
        }
    }
}
