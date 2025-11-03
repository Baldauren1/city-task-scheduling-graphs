package graph;

import graph.common.*;
import graph.scc.CondensationGraph;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.dagsp.DAGShortestPaths;
import graph.dagsp.DAGLongestPath;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Folder 'data' not found!");
            return;
        }

        File[] files = folder.listFiles((dir, name) ->
                name.startsWith("dataset_") && name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.err.println("No dataset_*.json files found in /data/");
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        System.out.println("Starting analysis for " + files.length + " datasets...");

        for (File f : files) {
            System.out.println("\n Processing " + f.getName());
            processGraph(f.getPath());
        }

        System.out.println("\nAll datasets processed successfully!");
        System.out.println("Results saved in /results/output_dataset_*.json and summary.csv");
    }

    private static void processGraph(String path) throws Exception {
        JsonIO.LoadedGraph lg = JsonIO.readGraph(path);
        Graph graph = lg.graph;
        int source = lg.source;

        // 1. SCC
        Metrics m1 = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, m1);
        var comps = tarjan.run();
        System.out.println("SCCs: " + comps);
        System.out.println("Metrics SCC: " + m1);

        //  2. Condensation
        CondensationGraph cg = new CondensationGraph(graph, comps);
        System.out.println("Condensation DAG: nodes=" + cg.compCount + ", edges=" + cg.dag.edges.size());

        //  3. Topological sort
        Metrics m2 = new Metrics();
        var topoOrder = new KahnTopologicalSort(cg.dag, m2).sort();
        System.out.println("Topological order of components: " + topoOrder);
        System.out.println("Metrics Topo: " + m2);

        //  4. DAG Shortest Paths
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
            if (res.dist[i] > maxDist) {
                maxDist = res.dist[i];
                endNode = i;
            }
        }

        List<Integer> pathComp = new ArrayList<>();
        if (endNode != -1) {
            for (int cur = endNode; cur != -1; cur = res.parent[cur]) {
                pathComp.add(cur);
            }
            Collections.reverse(pathComp);
        }

        System.out.println("Critical path components: " + pathComp);
        System.out.println("Critical length: " + maxDist);
        System.out.println("Metrics Longest: " + m4);

        // OUTPUTS
        try {
            new File("results").mkdirs();
            String base = path.substring(path.lastIndexOf('/') + 1).replace(".json", "");

            // JSON detailed result
            JsonResultWriter.write(
                    "results/output_" + base + ".json",
                    comps, topoOrder, dist, pathComp, maxDist,
                    m1, m2, m3, m4
            );

            // CSV summary
            CsvResultWriter.append(
                    "results/summary.csv",
                    base, graph.n, graph.edges.size(),
                    m1, m2, m3, m4
            );

            System.out.println("Saved: output_" + base + ".json + summary.csv");
        } catch (Exception e) {
            System.err.println("Error writing output for " + path + ": " + e.getMessage());
        }
    }
}
