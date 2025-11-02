package graph.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/*
 *Utility for generating random graph datasets for testing algorithms.
 **/

public class DataGenerator {

    //Generates a random directed weighted graph and saves it as JSON.
    public static void generate(String filename, int n, int edgeCount, boolean directed, int source) throws IOException {
        Random rnd = new Random();
        Map<String, Object> json = new LinkedHashMap<>();

        json.put("directed", directed);
        json.put("n", n);
        json.put("source", source);
        json.put("weight_model", "edge");

        List<Map<String, Object>> edges = new ArrayList<>();
        Set<String> used = new HashSet<>();

        while (edges.size() < edgeCount) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u == v) continue;
            String key = u + "-" + v;
            if (used.contains(key)) continue;
            used.add(key);

            Map<String, Object> e = new LinkedHashMap<>();
            e.put("u", u);
            e.put("v", v);
            e.put("w", 1 + rnd.nextInt(9)); // weights 1–9
            edges.add(e);
        }

        json.put("edges", edges);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(filename)) {
            gson.toJson(json, fw);
        }

        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + edgeCount + ")");
    }

    /**
     * Generates 9 datasets of varying sizes.
     */
    public static void main(String[] args) throws IOException {
        new java.io.File("data").mkdirs();

        // SMALL (6–10 nodes)
        generate("data/dataset_small_1.json", 6, 8, true, 0);
        generate("data/dataset_small_2.json", 8, 10, true, 0);
        generate("data/dataset_small_3.json", 9, 12, true, 2);

        // MEDIUM (10–20 nodes)
        generate("data/dataset_med_1.json", 12, 20, true, 3);
        generate("data/dataset_med_2.json", 15, 25, true, 4);
        generate("data/dataset_med_3.json", 18, 30, true, 5);

        // LARGE (20–50 nodes)
        generate("data/dataset_large_1.json", 22, 40, true, 0);
        generate("data/dataset_large_2.json", 30, 60, true, 0);
        generate("data/dataset_large_3.json", 45, 100, true, 0);
    }
}
