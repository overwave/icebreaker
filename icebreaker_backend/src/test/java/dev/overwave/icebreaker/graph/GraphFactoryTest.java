package dev.overwave.icebreaker.graph;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.graph.SparseList;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphFactoryTest {
    private final List<ContinuousVelocity> defaultVelocity =
            List.of(new ContinuousVelocity(20F, new Interval(Instant.now(), Duration.ofDays(1))));

    @Test
    void testGraphGeneration() {
        List<SpatialVelocity> mesh = List.of();
//                List.of(new SpatialVelocity(
//                new Point(69.621502F, 39.471258F),
//                new Point(69.564347F, 41.723242F),
//                new Point(68.516434F, 39.123224F),
//                new Point(68.516434F, 41.538989F),
//                defaultVelocity
//        ));
        long before = System.currentTimeMillis();
        Graph graph = GraphFactory.buildWeightedGraph(mesh);
        System.out.printf("Elapsed %d millis%n", System.currentTimeMillis() - before);

        List<SparseList<Node>> sparseLists = graph.getGraph();
        assertThat(sparseLists).hasSize(140);
        assertThat(sparseLists.getFirst().getSparseFactor()).isEqualTo(0);
        assertThat(sparseLists.getLast().getSparseFactor()).isEqualTo(2);
        assertThat(sparseLists.getFirst().getContent()).hasSize(502)
                .first()
                .extracting(Node::coordinates, node -> node.edges().size())
                .containsExactly(new Point(60, 20), 5);
        assertThat(sparseLists.get(3).getContent().getFirst())
                .extracting(Node::coordinates, node -> node.edges().size())
                .containsExactly(new Point(60.538834F, 20), 9);
    }
}
