package graph.scc;

import graph.common.Graph;
import graph.common.Metrics;
import java.util.*;

/**
 * Tarjan's algorithm for finding strongly connected components (SCCs).
 * Runs in O(V + E) time using DFS and a stack.
 */
public class TarjanSCC {
    private final Graph g;
    private final int n;
    private final int[] index, lowlink;
    private final boolean[] onStack;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private int idx = 0;
    private final List<List<Integer>> components = new ArrayList<>();
    private final Metrics metrics;

    public TarjanSCC(Graph g, Metrics m) {
        this.g = g; this.metrics = m; this.n = g.n;
        index = new int[n]; Arrays.fill(index, -1);
        lowlink = new int[n];
        onStack = new boolean[n];
    }

/** Executes the Tarjan SCC algorithm and returns a list of components. */
    public List<List<Integer>> run() {
        metrics.start();
        for (int v = 0; v < n; v++) {
            if (index[v] == -1) dfs(v);
        }
        metrics.stop();
        return components;
    }

    /** Recursive DFS helper for discovering SCCs. */
    private void dfs(int v) {
        metrics.dfsCalls++;
        index[v] = idx;
        lowlink[v] = idx;
        idx++;
        stack.push(v);
        onStack[v] = true;

        for (var e : g.adj.get(v)) {
            metrics.dfsEdges++;
            int w = e.v;
            if (index[w] == -1) {
                dfs(w);
                lowlink[v] = Math.min(lowlink[v], lowlink[w]);
            } else if (onStack[w]) {
                lowlink[v] = Math.min(lowlink[v], index[w]);
            }
        }

        if (lowlink[v] == index[v]) {// Root node -> extract new SCC
            List<Integer> comp = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                comp.add(w);
            } while (w != v);
            components.add(comp);
        }
    }
}
