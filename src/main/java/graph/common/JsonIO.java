package graph.common;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;

public class JsonIO {
    /**
     * Reads a JSON file and constructs a Graph object.
     * Returns a container with Graph + source + weight_model.
     */
    public static LoadedGraph readGraph(String path) throws IOException {
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(new FileReader(path), JsonObject.class);

        boolean directed = obj.get("directed").getAsBoolean();
        int n = obj.get("n").getAsInt();
        Graph g = new Graph(n, directed);

        JsonArray arr = obj.getAsJsonArray("edges");
        for (JsonElement el : arr) {
            JsonObject e = el.getAsJsonObject();
            int u = e.get("u").getAsInt();
            int v = e.get("v").getAsInt();
            long w = e.get("w").getAsLong();
            g.addEdge(u, v, w);
        }

        int source = obj.has("source") ? obj.get("source").getAsInt() : 0;
        String weightModel = obj.has("weight_model")
                ? obj.get("weight_model").getAsString()
                : "edge";

        return new LoadedGraph(g, source, weightModel);
    }

    public static class LoadedGraph {
        public final Graph graph;
        public final int source;
        public final String weightModel;
        public LoadedGraph(Graph g, int s, String w) {
            this.graph = g;
            this.source = s;
            this.weightModel = w;
        }
    }
}
