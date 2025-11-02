package graph.dagsp;

import graph.common.Edge;
import graph.common.Graph;
import graph.common.Metrics;
import graph.topo.KahnTopologicalSort;
import java.util.*;

public class DAGShortestPaths {
    private final Graph dag;
    private final List<Integer> topo;
    private final Metrics metrics;

    public DAGShortestPaths(Graph dag, Metrics metrics) {
        this.dag = dag; this.metrics = metrics;
        KahnTopologicalSort k = new KahnTopologicalSort(dag, new Metrics());
        topo = k.sort();
    }

    public long[] shortestFrom(int src) {
        int n = dag.n;
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;
        metrics.start();
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
