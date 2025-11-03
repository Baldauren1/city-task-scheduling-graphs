package graph.scc;

import graph.common.Edge;
import graph.common.Graph;
import java.util.*;

/**
 * Builds a condensation graph (DAG) from SCCs.
 * Each component becomes a node; edges connect different components.
 */
public class CondensationGraph {
    public final Graph dag;
    public final int[] compId;
    public final int compCount;

    public CondensationGraph(Graph g, List<List<Integer>> comps) {
        compCount = comps.size();
        dag = new Graph(compCount, true);
        compId = new int[g.n];
        for (int i = 0; i < compCount; i++) {// Assign component IDs to vertices
            for (int v : comps.get(i)) compId[v] = i;
        }

        Set<Long> seen = new HashSet<>(); // Add edges between components and no duplicates
        for (Edge e : g.edges) {
            int cu = compId[e.u];
            int cv = compId[e.v];
            if (cu != cv) {
                long key = (((long) cu) << 32) | (cv & 0xffffffffL);
                if (seen.add(key)) dag.addEdge(cu, cv, e.w);
            }
        }
    }
}
