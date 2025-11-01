package graph.common;

import java.util.*;

public class Graph {
    public final int n;
    public final boolean directed;
    public final List<List<Edge>> adj;
    public final List<Edge> edges = new ArrayList<>();

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v, long w) {
        Edge e = new Edge(u, v, w);
        adj.get(u).add(e);
        edges.add(e);
        if (!directed) {
            Edge rev = new Edge(v, u, w);
            adj.get(v).add(rev);
            edges.add(rev);
        }
    }
}
