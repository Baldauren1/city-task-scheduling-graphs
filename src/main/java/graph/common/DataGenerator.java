package graph.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Improved DataGenerator:
 * - Creates 9 datasets (small, medium, large)
 * - Controls density (sparse vs dense)
 * - Generates both cyclic and acyclic (DAG) graphs
 * - Prints ready-to-use summary for the report
 */
public class DataGenerator {

    private static final Random rnd = new Random();

    public static void main(String[] args) throws IOException {
        new java.io.File("data").mkdirs();
//For report
        System.out.println("------------------------------------------------------------");
        System.out.println(" Dataset | Vertices | Edges | Density | Type | Directed ");
        System.out.println("------------------------------------------------------------");

        // Small (6–10 nodes)
        generateDataset("data/dataset_small_1.json", 6, 0.2, true, false);
        generateDataset("data/dataset_small_2.json", 8, 0.4, true, true);
        generateDataset("data/dataset_small_3.json", 9, 0.3, true, false);

        // Medium (10–20 nodes)
        generateDataset("data/dataset_med_1.json", 12, 0.3, true, false);
        generateDataset("data/dataset_med_2.json", 15, 0.7, true, true);
        generateDataset("data/dataset_med_3.json", 18, 0.5, true, false);

        // Large (20–50 nodes)
        generateDataset("data/dataset_large_1.json", 22, 0.2, true, false);
        generateDataset("data/dataset_large_2.json", 30, 0.6, true, true);
        generateDataset("data/dataset_large_3.json", 45, 0.8, true, false);

        System.out.println("------------------------------------------------------------");
        System.out.println(" All datasets generated successfully.");
    }

    /**
     * Generates one dataset with given parameters.
     *
     * @param filename path to save JSON
     * @param n        number of nodes
     * @param density  fraction of possible edges (0.1–0.9)
     * @param directed true = directed graph
     * @param isDAG    true = acyclic, false = cyclic
     */
    public static void generateDataset(String filename, int n, double density, boolean directed, boolean isDAG) throws IOException {
        int maxEdges = directed ? n * (n - 1) : n * (n - 1) / 2;
        int edgeCount = Math.max(1, (int) (density * maxEdges));

        List<Map<String, Object>> edges = new ArrayList<>();
        Set<String> used = new HashSet<>();

        // Generate edges
        while (edges.size() < edgeCount) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u == v) continue;

            if (isDAG && u >= v) continue; // enforce acyclic order

            String key = u + "-" + v;
            if (used.contains(key)) continue;
            used.add(key);

            Map<String, Object> e = new LinkedHashMap<>();
            e.put("u", u);
            e.put("v", v);
            e.put("w", 1 + rnd.nextInt(9));
            edges.add(e);
        }

// Add guaranteed SCCs if cyclic
        if (!isDAG && n >= 6) {
            edges.add(Map.of("u", 0, "v", 1, "w", 2));
            edges.add(Map.of("u", 1, "v", 0, "w", 3));
            edges.add(Map.of("u", 2, "v", 3, "w", 1));
            edges.add(Map.of("u", 3, "v", 2, "w", 4));
        }

// Create JSON object
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("directed", directed);
        json.put("n", n);
        json.put("edges", edges);
        json.put("source", 0);
        json.put("weight_model", "edge");

// Save JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(filename)) {
            gson.toJson(json, fw);
        }

// Print summary line for the report
        System.out.printf(
                "%-18s | %8d | %5d | %7.2f | %-7s | %-9s%n",
                filename.replace("data/", ""),
                n,
                edges.size(),
                density,
                isDAG ? "DAG" : "Cyclic",
                directed ? "true" : "false"
        );
    }
}
