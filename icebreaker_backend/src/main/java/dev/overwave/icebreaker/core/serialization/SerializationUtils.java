package dev.overwave.icebreaker.core.serialization;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Edge;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.SparseList;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@UtilityClass
public class SerializationUtils {

    @SneakyThrows
    public void writeSpatial(List<SpatialVelocity> spatialVelocities, String path) {
        new File(path).getParentFile().mkdirs();
        try (DataOutputStream outputStream = new DataOutputStream(
                new LZ4FrameOutputStream(new FileOutputStream(path)))
        ) {
            outputStream.writeInt(spatialVelocities.size());
            Map<Interval, Integer> intervals = serializeIntervals(outputStream,
                    spatialVelocities.getFirst().velocities());
            for (SpatialVelocity spatialVelocity : spatialVelocities) {
                writePoint(outputStream, spatialVelocity.topLeft());
                writePoint(outputStream, spatialVelocity.topRight());
                writePoint(outputStream, spatialVelocity.bottomLeft());
                writePoint(outputStream, spatialVelocity.bottomRight());
                for (ContinuousVelocity velocity : spatialVelocity.velocities()) {
                    outputStream.writeFloat(velocity.velocity());
                    outputStream.writeByte(intervals.get(velocity.interval()));
                }
            }
        }
    }

    @SneakyThrows
    private Map<Interval, Integer> serializeIntervals(DataOutputStream outputStream,
                                                      List<ContinuousVelocity> velocities) {
        outputStream.writeByte(velocities.size());
        Map<Interval, Integer> intervals = new LinkedHashMap<>();
        for (ContinuousVelocity velocity : velocities) {
            Interval interval = velocity.interval();
            intervals.put(interval, intervals.size());
            outputStream.writeLong(interval.instant().getEpochSecond());
            outputStream.writeInt((int) interval.duration().getSeconds());
        }
        return intervals;
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
        try (DataInputStream inputStream = new DataInputStream(new LZ4FrameInputStream(new FileInputStream(path)))) {
            int spatialVelocitiesSize = inputStream.readInt();
            List<Interval> intervals = deserializeIntervals(inputStream);
            List<SpatialVelocity> spatialVelocities = new ArrayList<>(spatialVelocitiesSize);
            for (int i = 0; i < spatialVelocitiesSize; i++) {
                SpatialVelocity spatialVelocity = new SpatialVelocity(
                        readPoint(inputStream),
                        readPoint(inputStream),
                        readPoint(inputStream),
                        readPoint(inputStream),
                        new ArrayList<>(intervals.size())
                );
                for (int j = 0; j < intervals.size(); j++) {
                    spatialVelocity.velocities().add(new ContinuousVelocity(
                            inputStream.readFloat(),
                            intervals.get(inputStream.readByte())
                    ));
                }
                spatialVelocities.add(spatialVelocity);
            }
            return spatialVelocities;
        }
    }

    private static List<Interval> deserializeIntervals(DataInputStream inputStream) throws IOException {
        int intervalSize = inputStream.readByte();
        List<Interval> intervals = new ArrayList<>(intervalSize);
        for (int j = 0; j < intervalSize; j++) {
            intervals.add(new Interval(Instant.ofEpochSecond(inputStream.readLong()),
                    Duration.ofSeconds(inputStream.readInt())));
        }
        return intervals;
    }

    @SneakyThrows
    public void writeWeightedGraph(Graph graph, String path) {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(path))) {
            List<ContinuousVelocity> velocities = graph.getGraph().getFirst().getContent().stream()
                    .filter(node -> !node.edges().isEmpty()).findFirst().orElseThrow()
                    .edges().getFirst().velocities();
            Map<Interval, Integer> intervals = serializeIntervals(outputStream, velocities);
            Map<Point, Integer> points = serializePoints(outputStream, graph.getGraph());

            outputStream.writeInt(graph.getGraph().size());
            List<Edge> edgesToSerialize = new ArrayList<>();
            for (SparseList<Node> sparseList : graph.getGraph()) {
                outputStream.writeByte(sparseList.getSparseFactor());
                List<Node> nodes = sparseList.getContent();
                outputStream.writeInt(nodes.size());
                for (Node node : nodes) {
                    outputStream.writeInt(points.get(node.coordinates()));
                    for (Edge edge : node.edges()) {
                        if (edge.nodes().getKey().equals(node)) {
                            edgesToSerialize.add(edge);
                        }
                    }
                }
            }
            outputStream.writeInt(edgesToSerialize.size());
            for (Edge edge : edgesToSerialize) {
                Entry<Node, Node> leftRightNodes = edge.nodes();
                outputStream.writeInt(points.get(leftRightNodes.getKey().coordinates()));
                outputStream.writeInt(points.get(leftRightNodes.getValue().coordinates()));
                outputStream.writeFloat(edge.distance());

                for (ContinuousVelocity velocity : edge.velocities()) {
                    outputStream.writeFloat(velocity.velocity());
                    outputStream.writeByte(intervals.get(velocity.interval()));
                }
            }
        }
    }

    @SneakyThrows
    private Map<Point, Integer> serializePoints(DataOutputStream outputStream, List<SparseList<Node>> nodes) {
        Map<Point, Integer> points = new LinkedHashMap<>();
        for (SparseList<Node> sparseList : nodes) {
            for (Node node : sparseList.getContent()) {
                points.putIfAbsent(node.coordinates(), points.size());
            }
        }
        outputStream.writeInt(points.size());
        for (Point point : points.keySet()) {
            writePoint(outputStream, point);
        }
        return points;
    }

    @SneakyThrows
    public Graph readWeightedGraph(String path) {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(path))) {
            List<Interval> intervals = deserializeIntervals(inputStream);
            List<Point> points = deserializePoints(inputStream);
            Map<Integer, Node> nodesMap = new HashMap<>();

            int sparseListsSize = inputStream.readInt();
            List<SparseList<Node>> sparseLists = new ArrayList<>(sparseListsSize);
            for (int i = 0; i < sparseListsSize; i++) {
                int sparseFactor = inputStream.readByte();
                int nodesSize = inputStream.readInt();
                List<Node> nodes = new ArrayList<>(nodesSize);

                for (int j = 0; j < nodesSize; j++) {
                    int pointIndex = inputStream.readInt();
                    Node node = new Node(points.get(pointIndex), new ArrayList<>());
                    nodes.add(node);
                    nodesMap.put(pointIndex, node);
                }
                int edgesSize = inputStream.readInt();
                for (int j = 0; j < edgesSize; j++) {
                    Node left = nodesMap.get(inputStream.readInt());
                    Node right = nodesMap.get(inputStream.readInt());
                    float distance = inputStream.readFloat();

                    List<ContinuousVelocity> velocities = new ArrayList<>(intervals.size());
                    for (int k = 0; k < intervals.size(); k++) {
                        velocities.add(new ContinuousVelocity(
                                inputStream.readFloat(),
                                intervals.get(inputStream.readByte())));
                    }

                    Edge edge = new Edge(Map.entry(left, right), distance, velocities);
                    left.edges().add(edge);
                    right.edges().add(edge);
                }
                sparseLists.add(new SparseList<>(sparseFactor, nodes));
            }
            return new Graph(sparseLists);
        }
    }

    @SneakyThrows
    private List<Point> deserializePoints(DataInputStream inputStream) {
        int pointsSize = inputStream.readInt();
        List<Point> points = new ArrayList<>(pointsSize);
        for (int i = 0; i < pointsSize; i++) {
            points.add(readPoint(inputStream));
        }
        return points;
    }
}
