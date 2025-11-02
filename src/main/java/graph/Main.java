package graph;

import graph.common.JsonIO;
import graph.common.Graph;
import graph.common.Metrics;
import graph.scc.CondensationGraph;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.dagsp.DAGShortestPaths;
import graph.dagsp.DAGLongestPath;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = (args != null && args.length > 0) ? args[0] : "data/tasks.json";

        JsonIO.LoadedGraph lg = JsonIO.readGraph(path);
        Graph graph = lg.graph;
        int source = lg.source;
        String weightModel = lg.weightModel;

        // 1. SCC
        Metrics m1 = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, m1);
        var comps = tarjan.run();
        System.out.println("SCCs: " + comps);
        System.out.println("Metrics SCC: " + m1);

        // 2. Condensation
        CondensationGraph cg = new CondensationGraph(graph, comps);
        System.out.println("Condensation DAG: nodes=" + cg.compCount + ", edges=" + cg.dag.edges.size());

        // 3. Topological sort
        Metrics m2 = new Metrics();
        var topoOrder = new KahnTopologicalSort(cg.dag, m2).sort();
        System.out.println("Topological order of components: " + topoOrder);
        System.out.println("Metrics Topo: " + m2);

        // 4. DAG Shortest Paths
        int srcComp = cg.compId[source];
        Metrics m3 = new Metrics();
        DAGShortestPaths dsp = new DAGShortestPaths(cg.dag, m3);
        long[] dist = dsp.shortestFrom(srcComp);
        System.out.println("Shortest distances from comp(" + srcComp + "): " + Arrays.toString(dist));
        System.out.println("Metrics Shortest: " + m3);

        // 5. DAG Longest Path
        Metrics m4 = new Metrics();
        DAGLongestPath dlp = new DAGLongestPath(cg.dag, m4);
        var res = dlp.longestFrom(srcComp);
        long maxDist = Long.MIN_VALUE;
        int endNode = -1;
        for (int i = 0; i < res.dist.length; i++) {
            if (res.dist[i] > maxDist) { maxDist = res.dist[i]; endNode = i; }
        }

        List<Integer> pathComp = new ArrayList<>();
        if (endNode != -1) {
            for (int cur = endNode; cur != -1; cur = res.parent[cur]) pathComp.add(cur);
            Collections.reverse(pathComp);
        }

        System.out.println("Critical path components: " + pathComp);
        System.out.println("Critical length: " + maxDist);
        System.out.println("Metrics Longest: " + m4);
    }
}
