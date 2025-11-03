# Assignment 4 — City Task Scheduling and Dependency Analysis (SCC, Topological Sort, Shortest Paths in DAGs)
### 1. Objective
The main goal of this assignment was to combine two algorithmic topics — Strongly Connected Components (SCC) and Shortest Paths in Directed Acyclic Graphs (DAGs) — into one practical scenario that simulates how tasks are scheduled in a smart city or campus.

In this project, each node represents a maintenance or analytical task (like camera repair, street cleaning, or sensor check), and edges represent dependencies between these tasks.
Some dependencies form cycles (where tasks depend on each other), while others are acyclic.
So first, I detect and group cyclic dependencies using SCC algorithms, and then plan the optimal order and timing for the acyclic part using topological sort and shortest path algorithms.
---

### 2. Project Structure
````
city-task-scheduling-graphs/
 ├── data/
 │    ├── dataset_small_1.json … dataset_large_3.json
 ├── results/
 │    ├── output_dataset_*.json
 │    └── summary.csv
 ├── src/
 │   ├── main/java/graph/common/
 │   │    ├── Edge.java
 │   │    ├── Graph.java
 │   │    ├── Metrics.java
 │   │    ├── JsonIO.java
 │   │    ├── JsonResultWriter.java
 │   │    ├── CsvResultWriter.java
 │   │    └── DataGenerator.java
 │   ├── main/java/graph/scc/
 │   │    ├── TarjanSCC.java
 │   │    └── CondensationGraph.java
 │   ├── main/java/graph/topo/
 │   │    └── TopoSort.java
 │   ├── main/java/graph/dagsp/
 │   │    ├── DAGShortestPaths.java
 │   │    └── DAGLongestPath.java
 │   └── main/java/
 │        └── Main.java
 ├── pom.xml
 └── README.md
````
### 3. Dataset Summary
I generated nine test graphs in total (small, medium, and large) using a custom class **DataGenerator.java**.
Each dataset is stored in /data/ as a JSON file with directed, weighted edges.
The weights represent the duration or cost of each dependency.

| Dataset              | Vertices | Edges | Density | Type   | Directed |
| -------------------- | -------- | ----- | ------- | ------ | -------- |
| dataset_small_1.json | 6        | 10    | 0.20    | Cyclic | true     |
| dataset_small_2.json | 8        | 22    | 0.40    | DAG    | true     |
| dataset_small_3.json | 9        | 25    | 0.30    | Cyclic | true     |
| dataset_med_1.json   | 12       | 43    | 0.30    | Cyclic | true     |
| dataset_med_2.json   | 15       | 104   | 0.50    | DAG    | true     |
| dataset_med_3.json   | 18       | 126   | 0.40    | Cyclic | true     |
| dataset_large_1.json | 22       | 119   | 0.25    | Cyclic | true     |
| dataset_large_2.json | 30       | 429   | 0.50    | DAG    | true     |
| dataset_large_3.json | 45       | 1390  | 0.70    | Cyclic | true     |

All graphs use the edge-weight model, meaning the weight is attached to each edge rather than each node.

Each file is in JSON format:
````
{
    "directed": true,
    "n": 6,
    "edges": [{"u": 3,"v": 4,"w": 7 }, ... ],
    "source": 0,
    "weight_model": "edge"
}
````

### 4. Implemented Algorithms
#### 4.1 Strongly Connected Components (Tarjan)
I used Tarjan’s algorithm to find all strongly connected components (SCCs).
This helps to detect cycles and group them together before further analysis.
Then, using CondensationGraph.java, I build a new DAG where each SCC becomes a single node.

#### 4.2 Topological Sorting (Kahn’s Algorithm)
Once the condensation DAG is ready, I run Kahn’s algorithm to find a valid topological order of components.
This order shows which groups of tasks can be executed first.
All queue operations are tracked through the Metrics class.

#### 4.3 Shortest and Longest Paths in DAGs
- For the acyclic graph, I implemented two dynamic programming algorithms:
- DAGShortestPaths.java for the minimal distance or minimal total duration; 
- DAGLongestPath.java for finding the critical path, the longest sequence of dependent tasks.

### 5. Outputs
Every dataset produces two outputs:
`results/output_dataset_X.json` — full algorithm results (SCCs, topological order, paths, metrics).
`results/summary.csv` — summary table for quick comparison across datasets.

For each dataset, the program generated:
- JSON files in results/output_data/

Containing:
- List of SCCs
- Condensation DAG (implied through topological_order)
- Shortest distances (DAG-SP)
- Critical path components and length
- Detailed metrics for each phase (SCC, Topo, Shortest, Longest)

CSV file — results/summary.csv
Summarizes all graphs with:
- Vertex and edge count
- SCC count
- Critical path length
- Algorithmic metrics (dfsCalls, relaxations, etc.)
````
{
  "shortest_distances": [
    0, ...
  ],
  "scc_list": [[1,2,4,3,0],], ...
  "topological_order": [
    0, ...],
  "metrics": {
    "SCC": "time=0ms, dfsCalls=6, dfsEdges=10, kahnPushes=0, kahnPops=0, relax=0",
    "Shortest": ... ,
    "Longest": ... ,
    "Topo": ... 
    },
  "scc_count": 2,
  "critical_path": [ 0 ],
  "critical_length": 0
}
````

### 6. Analysis and Discussion
#### 1. Structural Observations

Each dataset was processed through four main stages — SCC detection, topological sorting, DAG shortest paths, and critical path (longest) detection.
Based on the JSON outputs, we can clearly see that only a few datasets produced more than one SCC `dataset_small_1` and `dataset_med_1`, while most of the others formed a single large SCC, meaning they were strongly connected cyclic graphs.

For example:

`dataset_large_1.json` -> scc_count = 1
`dataset_med_1.json` -> scc_count = 2
`dataset_small_1.json` -> scc_count = 2

This directly affects whether a valid topological order and critical path can be found:
- When scc_count > 1, the condensation graph becomes a DAG, and topological order is possible.
- When scc_count = 1, the graph is fully cyclic, so the topological order degenerates to a single node, and the critical path is 0.

#### 2. SCC / Topological Structure

The small and medium datasets (e.g. dataset_small_1 and dataset_med_1) contained 2 SCCs, producing short condensation DAGs with only two nodes.
That’s why their "topological_order" fields contain only [0, 1] or [1, 0].

In contrast, larger graphs (dataset_large_1, dataset_large_3) were completely cyclic with "scc_count": 1,
indicating dense interconnections — typical for large, random, directed graphs with high edge density.

#### 3. Critical Path and DAG-SP Behavior

In all cyclic graphs, the "critical_length" value is 0.
This is expected: when the entire graph is a single SCC, there is no linear dependency chain (no DAG structure), and therefore no meaningful "task scheduling" interpretation.

Only the acyclic datasets (*_2) showed positive critical path lengths:

`dataset_small_2` -> critical_length = 20
`dataset_med_2` -> critical_length = 87
`dataset_large_2` -> critical_length = 368

These three datasets correspond to graphs generated in DAG mode during data generation.
As the graph size increases, the critical path length grows proportionally — reflecting a deeper dependency chain across more nodes.

#### 4. Performance Metrics
All four algorithms (TarjanSCC, CondensationGraph, TopologicalSort, DAGShortest/LongestPaths) executed within 0–1 ms, even on the largest datasets.
This confirms that:
- Tarjan’s SCC runs in O(V + E) time, efficiently handling thousands of edges. 
- Kahn’s topological sorting is negligible in runtime once the graph is acyclic. 
- Shortest/Longest path computations scale linearly with the number of edges.

Metrics like dfsCalls, dfsEdges, and relaxations also grow in proportion to graph size —
e.g. dfsEdges = 10 for small graphs and 429 for medium–large ones — showing expected linear scaling.

#### 5. Structural and Practical Insights
   | Type                            | Structural Property                   | Critical Path | Practical Meaning                                |
   | ------------------------------- | ------------------------------------- | ------------- | ------------------------------------------------ |
   | **Cyclic Graphs (SCC = 1)**     | Fully connected; no topological order | 0             | Represents feedback systems, not schedulable     |
   | **Partially Acyclic (SCC = 2)** | Two weakly connected components       | 0             | Minimal scheduling potential                     |
   | **DAGs (acyclic)**              | Clear dependency chains               | 20–368        | Suitable for task scheduling or project planning |


#### 6. Summary Interpretation
Overall, the analysis shows:
- 6 graphs were cyclic (unschedulable, single SCC). 
- 3 graphs were acyclic (valid DAGs) with non-zero critical paths. 
- Critical path length grows with graph size and complexity. 
- All algorithms perform efficiently, even on dense graphs. 
- Graph density tends to merge components, reducing DAG depth and eliminating critical paths.

These results match the expected theoretical behavior:
denser directed graphs tend to become cyclic, while sparser ones preserve acyclic structure, enabling topological scheduling and path-based analysis.

#### 7. Reflection
During the experiments, I noticed that even small changes in density drastically affect whether the graph is cyclic or acyclic.
This was especially visible in the “_2” datasets, which were intentionally generated as DAGs — they produced meaningful critical path results, while all others immediately collapsed into single SCCs.
It clearly demonstrates how real-world systems (like task schedulers or workflows) depend heavily on maintaining acyclicity to remain predictable and analyzable.

### 7. Conclusions
Based on both the theoretical background and experimental results, I can conclude the following:
- Tarjan’s SCC algorithm works extremely efficiently — it scales linearly with the number of vertices and edges and correctly detects all cyclic components.
- I clearly observed that graph density affects structure: when the graph becomes denser, almost all nodes merge into one big SCC, while sparse graphs remain acyclic (DAGs).
- Topological sorting only works properly for DAGs. In cyclic graphs, it degenerates because there’s no valid order of dependencies.
- The critical path length increases with the graph size — this shows how deeper or more complex the dependency structure becomes.
- All algorithms ran very fast in practice — less than 1 millisecond even on the largest dataset (45 vertices and 1390 edges).

From a practical point of view:
- SCC detection is useful for analyzing large feedback systems or cyclic dependencies.
- DAG shortest and longest path algorithms are better suited for project planning, task scheduling, or dependency analysis, where the graph must be acyclic.
- To make such analysis easier, it’s better to keep the graph less dense, since high density usually destroys the DAG structure.

### 8. Implementation Notes
During development, several issues were encountered and resolved:
- Path errors in JSON writer were fixed by ensuring directory creation before saving results.
- Unicode escapes in metrics (\u003d) were solved by disabling HTML escaping in Gson.
- Dataset generator was adjusted to enforce acyclicity for the “_2” datasets.
- Output validation was done by cross-checking JSON and CSV summaries.
- The modular project structure — with clean separation between common, dagsp, and scc packages — made testing and debugging straightforward.

9. References
- Tarjan, R.E. Depth-First Search and Linear Graph Algorithms (SIAM Journal, 1972)

The original paper describing Tarjan’s algorithm for finding strongly connected components in linear time. Helped me understand how recursion and stack tracking are used to identify SCCs efficiently.
- GeeksforGeeks – Strongly Connected Components (Tarjan Algorithm)

Used to verify the implementation logic and test cases for Tarjan’s algorithm. Provided a clear explanation of discovery and low-link values.
- Baeldung – Working with Graphs in Java

Helpful modern tutorial on implementing graphs using Java collections. I adapted their representation style using adjacency lists and List<Edge> structures for all algorithms in this project.
- Kahn, A.B. – Topological Sorting of Large Networks (Communications of the ACM, 1962)

The foundational reference for Kahn’s topological sorting algorithm. It helped me understand how queue-based ordering ensures correct dependency resolution in DAGs.
- MIT OpenCourseWare (6.006) – Graph Representations and SCC Algorithms

Provided theoretical insight into graph representations, strongly connected components, and topological order concepts.
- Oracle Java Documentation – java.util and Collections Framework

Used for reference when working with Java’s Map, List, and PriorityQueue classes, as well as stream operations and collections performance details.