package graph.common;

public class Metrics {
    public long startTime, endTime;
    public long dfsCalls = 0;
    public long dfsEdges = 0;
    public long kahnPushes = 0;
    public long kahnPops = 0;
    public long relaxations = 0;

    public void start() { startTime = System.nanoTime(); }
    public void stop() { endTime = System.nanoTime(); }
    public long durationMs() { return (endTime - startTime) / 1_000_000; }

    @Override
    public String toString() {
        return String.format(
                "time=%dms, dfsCalls=%d, dfsEdges=%d, kahnPushes=%d, kahnPops=%d, relax=%d",
                durationMs(), dfsCalls, dfsEdges, kahnPushes, kahnPops, relaxations
        );
    }
}
