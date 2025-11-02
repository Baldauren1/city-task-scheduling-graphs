package graph;

import graph.common.*;
import graph.dagsp.*;
import graph.topo.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class DagSpTest {

    @Test
    public void testShortestAndLongest() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(0, 3, 4);

        Metrics m1 = new Metrics();
        DAGShortestPaths dsp = new DAGShortestPaths(g, m1);
        long[] dist = dsp.shortestFrom(0);

        assertEquals(0, dist[0]);
        assertEquals(1, dist[1]);
        assertEquals(3, dist[2]);
        assertEquals(4, dist[3]);

        Metrics m2 = new Metrics();
        DAGLongestPath dlp = new DAGLongestPath(g, m2);
        var res = dlp.longestFrom(0);

        assertEquals(0, res.dist[0]);
        assertEquals(1, res.dist[1]);
        assertEquals(3, res.dist[2]);
        assertEquals(4, res.dist[3]);
    }
}
