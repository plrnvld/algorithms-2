import java.util.stream.StreamSupport;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph digraph;
    private final Digraph reversed;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        digraph = G;
        reversed = G.reverse();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        boolean[] markedUp = new boolean[digraph.V()];
        boolean[] markedDown = new boolean[digraph.V()];
        int[] from = new int[digraph.V()];
        if (digraph.V() == 0)
            return -1;

        if (v == w)
            return 0;

        int len = 1;
        Queue<NextVertex> queue = new Queue<>();
        markedUp[v] = true;
        markedDown[v] = true;

        for (int adj : digraph.adj(v)) {
            from[adj] = v;
            System.out.println("> Enqueue " + adj + " (len = " + len + ", up)");
            queue.enqueue(new NextVertex(adj, true));
        }

        for (int adj : reversed.adj(v)) {
            from[adj] = v;
            System.out.println("> Enqueue " + adj + " (len = " + len + ", down)");
            queue.enqueue(new NextVertex(adj, false));
        }

        while (!queue.isEmpty()) {
            NextVertex curr = queue.dequeue();
            System.out.println(">>> Dequeue " + curr.id + ", up=" + curr.up + ", from=" + from[curr.id]);

            if (curr.up) {
                markedUp[curr.id] = true;
                markedDown[curr.id] = true;
            }
            else
                markedDown[curr.id] = true;

            if (curr.id == w) {
                return len;
            }

            if (curr.up) {
                for (int upAdj : digraph.adj(curr.id)) {
                    System.out.println("      Adjacent up: " + upAdj);
                    if (!markedUp[upAdj]) {
                        from[upAdj] = curr.id;
                        System.out.println("> (curr.up = " + curr.up + ") Enqueue " + upAdj + " (len = " + len + ")");
                        queue.enqueue(new NextVertex(upAdj, true));
                    }
                }
            }

            for (int downAdj : reversed.adj(curr.id)) {
                System.out.println("      Adjacent down: " + downAdj);
                if (!markedUp[downAdj] && !markedUp[downAdj]) {
                    from[downAdj] = curr.id;
                    System.out.println("> (curr.up = " + curr.up + ") Enqueue " + downAdj + " (len = " + len + ")");
                    queue.enqueue(new NextVertex(downAdj, curr.up));
                }
            }

            len += 1;
        }

        return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        boolean[] marked = new boolean[digraph.V()];
        int[] from = new int[digraph.V()];
        if (digraph.V() == 0)
            return -1;

        if (v == w)
            return v;

        Queue<NextVertex> queue = new Queue<>();
        marked[v] = true;

        for (int adj : digraph.adj(v)) {
            from[adj] = v;
            queue.enqueue(new NextVertex(adj, true));
        }

        for (int adj : reversed.adj(v)) {
            from[adj] = v;
            queue.enqueue(new NextVertex(adj, false));
        }

        while (!queue.isEmpty()) {
            NextVertex next = queue.dequeue();
            marked[next.id] = true;
            if (next.id == w) {
                return detectAncestor(w, from);
            }

            if (next.up) {
                for (int upAdj : digraph.adj(next.id)) {
                    // System.out.println(" Adjacent up: " + upAdj);
                    if (!marked[next.id]) {
                        from[upAdj] = next.id;
                        queue.enqueue(new NextVertex(upAdj, true));
                    }
                }
            }

            for (int downAdj : reversed.adj(next.id)) {
                // System.out.println(" Adjacent down: " + downAdj);
                if (!marked[next.id]) {
                    from[downAdj] = next.id;
                    queue.enqueue(new NextVertex(downAdj, false));
                }
            }
        }

        return -1;
    }

    private int detectAncestor(int w, int[] from) {
        int curr = w;
        while (true) {
            int prev = from[curr];

            Iterable<Integer> higher = digraph.adj(curr);

            boolean prevIsHigher = StreamSupport.stream(higher.spliterator(), false)
                    .anyMatch(h -> h == prev);

            if (!prevIsHigher)
                return curr;

            curr = prev;
        }
    }

    // length of shortest ancestral path between any vertex in v and any vertex in
    // w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return -1;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private class NextVertex {
        int id;
        boolean up;

        NextVertex(int id, boolean up) {
            this.id = id;
            this.up = up;
        }
    }
}