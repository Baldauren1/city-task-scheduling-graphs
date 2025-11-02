package graph.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Writes algorithm results into a structured JSON file.
 * Used for storing detailed outputs of SCC, Topo, and DAG-SP.
 */
public class JsonResultWriter {

    public static void write(String path,
                             List<List<Integer>> sccs,
                             List<Integer> topo,
                             long[] shortest,
                             List<Integer> longestPath,
                             long longestLen,
                             Metrics m1, Metrics m2, Metrics m3, Metrics m4) throws IOException {

        Map<String, Object> root = new HashMap<>();

        root.put("scc_count", sccs.size());
        root.put("scc_list", sccs);
        root.put("topological_order", topo);
        root.put("shortest_distances", shortest);
        root.put("critical_path", longestPath);
        root.put("critical_length", longestLen);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("SCC", m1.toString());
        metrics.put("Topo", m2.toString());
        metrics.put("Shortest", m3.toString());
        metrics.put("Longest", m4.toString());
        root.put("metrics", metrics);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(root, fw);
        }

        System.out.println("JSON saved → " + path);
    }
}
