package graph.common;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Appends summarized algorithm metrics into a CSV file.
 * Used for performance analysis in the report.
 */
public class CsvResultWriter {

    public static void append(String path, String datasetName, int n, int e,
                              Metrics scc, Metrics topo, Metrics sp, Metrics lp) throws IOException {

        boolean append = new java.io.File(path).exists();
        try (FileWriter fw = new FileWriter(path, true)) {
            if (!append) {
                fw.write("dataset,nodes,edges,"
                        + "scc_time,topo_time,shortest_time,longest_time,"
                        + "dfsCalls,dfsEdges,relaxations\n");
            }
            fw.write(String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                    datasetName, n, e,
                    scc.durationMs(), topo.durationMs(), sp.durationMs(), lp.durationMs(),
                    scc.dfsCalls, scc.dfsEdges, lp.relaxations));
        }

        System.out.println("CSV updated -> " + path);
    }
}
