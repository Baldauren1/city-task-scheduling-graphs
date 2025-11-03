package graph.topo;

import graph.common.Graph;
import graph.common.Metrics;
import java.util.*;

/**
 * Performs topological sorting using Kahn's algorithm.
 * Works only on acyclic directed graphs (DAGs).
 */
public class KahnTopologicalSort {
    private final Graph g;
    private final Metrics metrics;

    public KahnTopologicalSort(Graph g, Metrics m) {
        this.g = g; this.metrics = m;
    }

/** Returns a topological order of vertices or throws if a cycle exists. */
    public List<Integer> sort() {
        metrics.start();
        int n = g.n;
        int[] indeg = new int[n];
        for (var e : g.edges) indeg[e.v]++;
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);
        List<Integer> order = new ArrayList<>();

        while (!q.isEmpty()) {// Process queue in BFS-like manner
            metrics.kahnPops++;
            int u = q.remove();
            order.add(u);
            for (var e : g.adj.get(u)) {
                indeg[e.v]--;
                if (indeg[e.v] == 0) {
                    q.add(e.v);
                    metrics.kahnPushes++;
                }
            }
        }
        metrics.stop();
        if (order.size() != n)
            throw new IllegalStateException("Graph contains a cycle");
        return order;
    }
}
