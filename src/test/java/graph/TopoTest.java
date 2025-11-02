package graph;

import graph.common.*;
import graph.topo.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class TopoTest {

    @Test
    public void testValidOrder() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(0, 3, 1);

        Metrics m = new Metrics();
        KahnTopologicalSort topo = new KahnTopologicalSort(g, m);
        var order = topo.sort();

        assertTrue(order.indexOf(0) < order.indexOf(1));
 // 0 must come before 1 and 3
        assertTrue(order.indexOf(0) < order.indexOf(3));
    }

    @Test(expected = IllegalStateException.class)
    public void testCycleDetection() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Metrics m = new Metrics();
        new KahnTopologicalSort(g, m).sort(); // must throw exception
    }
}
