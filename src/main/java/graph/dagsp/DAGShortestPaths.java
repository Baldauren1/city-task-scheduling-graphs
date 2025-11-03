package graph.dagsp;

import graph.common.Edge;
import graph.common.Graph;
import graph.common.Metrics;
import graph.topo.KahnTopologicalSort;
import java.util.*;

/** Computes single-source shortest paths in a Directed Acyclic Graph (DAG).
 */
public class DAGShortestPaths {
    private final Graph dag;
    private final List<Integer> topo;
    private final Metrics metrics;

    /**
     * Initializes the shortest-path solver with a given DAG and metrics tracker.
     *
     * @param dag a directed acyclic graph
     * @param metrics shared metrics collector for performance statistics
     */
    public DAGShortestPaths(Graph dag, Metrics metrics) {
        this.dag = dag; this.metrics = metrics;
// Compute topological order once before relaxation
        KahnTopologicalSort k = new KahnTopologicalSort(dag, new Metrics());
        topo = k.sort();
    }

    /**
     * Computes the shortest distances from a single source node to all others.
     */
    public long[] shortestFrom(int src) {
        int n = dag.n;
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        metrics.start();

 // Relax edges in topological order each edge only once
        for (int u : topo) {
            if (dist[u] == INF) continue;
            for (Edge e : dag.adj.get(u)) {
                metrics.relaxations++;
                if (dist[e.v] > dist[u] + e.w)
                    dist[e.v] = dist[u] + e.w;
            }
        }
        metrics.stop();
        return dist;
    }
}
