package graph.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Final version of DataGenerator:
 * Creates 9 datasets (small, medium, large)
 * Uses both cyclic and acyclic (DAG) graphs
 * Supports density control (sparse vs dense)
 * Prevents infinite loops with iteration limits
 */
public class DataGenerator {
    private static final Random rnd = new Random();

    public static void main(String[] args) throws IOException {
        new java.io.File("data").mkdirs();
//For report -------
        System.out.println("------------------------------------------------------------");
        System.out.println(" Dataset | Vertices | Edges | Density | Type | Directed ");
        System.out.println("------------------------------------------------------------");

        // SMALL (6–10 nodes)
        generateDataset("data/dataset_small_1.json", 6, 0.2, true, false); // cyclic, sparse
        generateDataset("data/dataset_small_2.json", 8, 0.4, true, true);  // DAG, medium
        generateDataset("data/dataset_small_3.json", 9, 0.3, true, false); // cyclic, medium

        // MEDIUM (10–20 nodes)
        generateDataset("data/dataset_med_1.json", 12, 0.3, true, false);  // cyclic
        generateDataset("data/dataset_med_2.json", 15, 0.5, true, true);   // DAG, balanced
        generateDataset("data/dataset_med_3.json", 18, 0.4, true, false);  // cyclic

        // LARGE (20–50 nodes)
        generateDataset("data/dataset_large_1.json", 22, 0.25, true, false); // cyclic, sparse
        generateDataset("data/dataset_large_2.json", 30, 0.5, true, true);   // DAG, dense
        generateDataset("data/dataset_large_3.json", 45, 0.7, true, false);  // cyclic, dense

        System.out.println("------------------------------------------------------------");
        System.out.println("All datasets generated successfully.");
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
        int targetEdges = Math.max(1, (int) (density * maxEdges));

        List<Map<String, Object>> edges = new ArrayList<>();
        Set<String> used = new HashSet<>();

        int maxAttempts = n * n * 5; // safety limit to prevent infinite loop
        int attempts = 0;

        while (edges.size() < targetEdges && attempts < maxAttempts) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            attempts++;

            if (u == v) continue;
            if (isDAG && u >= v) continue; // enforce acyclic property

            String key = u + "-" + v;
            if (used.contains(key)) continue;
            used.add(key);

            Map<String, Object> e = new LinkedHashMap<>();
            e.put("u", u);
            e.put("v", v);
            e.put("w", 1 + rnd.nextInt(9));
            edges.add(e);
        }

        if (edges.size() < targetEdges) {
            System.out.printf("Warning: only %d edges generated (target was %d) for %s%n",
                    edges.size(), targetEdges, filename);
        }

// Add small guaranteed SCCs for cyclic graphs
        if (!isDAG && n >= 6) {
            edges.add(Map.of("u", 0, "v", 1, "w", 2));
            edges.add(Map.of("u", 1, "v", 0, "w", 3));
            edges.add(Map.of("u", 2, "v", 3, "w", 1));
            edges.add(Map.of("u", 3, "v", 2, "w", 4));
        }

// Build JSON object
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

// Print concise summary (for report table)
        System.out.printf(
                "%-20s | %8d | %5d | %7.2f | %-7s | %-9s%n",
                filename.replace("data/", ""),
                n,
                edges.size(),
                density,
                isDAG ? "DAG" : "Cyclic",
                directed ? "true" : "false"
        );
    }
}
