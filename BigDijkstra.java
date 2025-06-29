import java.util.Scanner;
import java.io.*;

class Heap {
    private int[] a;
    private int[] hPos;
    private int[] dist;
    private int N;

    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void siftUp(int k) {
        int v = a[k];
        while (k > 1 && dist[v] < dist[a[k / 2]]) {
            a[k] = a[k / 2];
            hPos[a[k]] = k;
            k = k / 2;
        }
        a[k] = v;
        hPos[v] = k;
    }

    public void siftDown(int k) {
        int v = a[k];
        int j;
        while (2 * k <= N) {
            j = 2 * k;
            if (j < N && dist[a[j]] > dist[a[j + 1]]) j++;
            if (dist[v] <= dist[a[j]]) break;
            a[k] = a[j];
            hPos[a[k]] = k;
            k = j;
        }
        a[k] = v;
        hPos[v] = k;
    }

    public void insert(int x) {
        a[++N] = x;
        siftUp(N);
    }

    public int remove() {
        int v = a[1];
        hPos[v] = 0;
        a[N + 1] = 0;
        a[1] = a[N--];
        siftDown(1);
        return v;
    }
}

class BigDijkstra {
    static class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    private int V, E;
    private Node[] adj;
    private Node z;

    public BigDijkstra(String graphFile) throws IOException {
        int u, v, e, wgt;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +";
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("ERROR: The file is empty or improperly formatted.");
        }
        System.out.println("Reading first line: " + line);

        String[] parts = line.split(splits);
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        z = new Node();
        z.next = z;

        adj = new Node[V + 1];
        for (v = 1; v <= V; ++v)
            adj[v] = z;

        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            if (line == null) {
                System.out.println("Warning: Expected " + E + " edges, but only found " + (e - 1) + " edges in the file.");
                break;
            }
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            Node newNode = new Node();
            newNode.vert = v;
            newNode.wgt = wgt;
            newNode.next = adj[u];
            adj[u] = newNode;

            newNode = new Node();
            newNode.vert = u;
            newNode.wgt = wgt;
            newNode.next = adj[v];
            adj[v] = newNode;
        }
        if (e != E) {
            System.out.println("File contains fewer edges than expected. Only " + (e - 1) + " edges were processed.");
        }
    }

    
    public void SPT_Dijkstra(int s) {
        int[] dist = new int[V + 1];
        int[] parent = new int[V + 1];
        int[] hPos = new int[V + 1];

        for (int v = 1; v <= V; v++) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
        }

        Heap pq = new Heap(V, dist, hPos);
        dist[s] = 0;
        pq.insert(s);

        System.out.println("Starting Dijkstra's Algorithm from vertex: " + s);

        while (!pq.isEmpty()) {
            int v = pq.remove();
            System.out.println("Visiting: " + v + ", Parent: " + parent[v] + ", Dist: " + dist[v]);

            for (Node uNode = adj[v]; uNode != z; uNode = uNode.next) {
                int u = uNode.vert;
                int d = uNode.wgt;

                if (dist[v] + d < dist[u]) {
                    dist[u] = dist[v] + d;
                    if (hPos[u] == 0) {
                        pq.insert(u);
                    } else {
                        pq.siftUp(hPos[u]);
                    }
                    parent[u] = v;
                }
            }
        }

        System.out.println("\nShortest Path Tree (SPT) Parent Array: ");
        for (int i = 1; i <= V; i++) {
            System.out.println("Vertex: " + i + ", Parent: " + parent[i] + ", Dist: " + dist[i]);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter graph file name: ");
        String graphFile = sc.nextLine();
        System.out.print("Enter starting vertex (as number): ");
        int startVertex = sc.nextInt();

        // Start measuring memory before graph creation
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Run garbage collector to get cleaner measurement
        long beforeUsedMem = runtime.totalMemory() - runtime.freeMemory();

        // Create graph
        BigDijkstra g = new BigDijkstra(graphFile);

        // Measure memory after graph creation (optional)
        long afterGraphMem = runtime.totalMemory() - runtime.freeMemory();

        // Start timer
        long startTime = System.nanoTime();

        // Run Dijkstra
        g.SPT_Dijkstra(startVertex);

        // Stop timer
        long endTime = System.nanoTime();

        // End memory
        long afterUsedMem = runtime.totalMemory() - runtime.freeMemory();

        // Print results
        System.out.println("\n--- Performance Metrics ---");
        System.out.printf("Dijkstra Execution Time: %.3f ms\n", (endTime - startTime) / 1e6);
        System.out.printf("Memory Used Before Graph: %.2f MB\n", beforeUsedMem / (1024.0 * 1024));
        System.out.printf("Memory Used After Graph Creation: %.2f MB\n", afterGraphMem / (1024.0 * 1024));
        System.out.printf("Memory Used After Dijkstra: %.2f MB\n", afterUsedMem / (1024.0 * 1024));
    }
}
