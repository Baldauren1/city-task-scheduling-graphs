package graph;

import graph.common.*;
import graph.scc.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class SccTest {

    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Metrics m = new Metrics();
        TarjanSCC t = new TarjanSCC(g, m);
        var comps = t.run();

        assertEquals(1, comps.size());
        assertEquals(3, comps.get(0).size());
    }

    @Test
    public void testTwoComponents() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 2, 1);

        Metrics m = new Metrics();
        TarjanSCC t = new TarjanSCC(g, m);
        var comps = t.run();

        assertEquals(2, comps.size());
    }

    @Test
    public void testAcyclic() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        TarjanSCC t = new TarjanSCC(g, m);
        var comps = t.run();

        assertEquals(4, comps.size());
    }
}
