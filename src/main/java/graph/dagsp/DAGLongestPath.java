package graph.dagsp;

import graph.common.Edge;
import graph.common.Graph;
import graph.common.Metrics;
import graph.topo.KahnTopologicalSort;
import java.util.*;

public class DAGLongestPath {
    private final Graph dag;
    private final List<Integer> topo;
    private final Metrics metrics;

    public DAGLongestPath(Graph dag, Metrics m) {
        this.dag = dag; this.metrics = m;
        topo = new KahnTopologicalSort(dag, new Metrics()).sort();
    }

    public Result longestFrom(int src) {
        int n = dag.n;
        long NEG_INF = Long.MIN_VALUE / 4;
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG_INF);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        metrics.start();
        for (int u : topo) {
            if (dist[u] == NEG_INF) continue;
            for (Edge e : dag.adj.get(u)) {
                if (dist[e.v] < dist[u] + e.w) {
                    dist[e.v] = dist[u] + e.w;
                    parent[e.v] = u;
                    metrics.relaxations++;
                }
            }
        }
        metrics.stop();
        return new Result(dist, parent);
    }

    public static class Result {
        public final long[] dist;
        public final int[] parent;
        public Result(long[] d, int[] p) { this.dist = d; this.parent = p; }
    }
}
