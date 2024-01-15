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

        Queue<NextVertex> queue = new Queue<>();
        markedUp[v] = true;
        markedDown[v] = true;

        digraph.adj(v).forEach(adj -> addNext(adj, v, true, from, queue));
        reversed.adj(v).forEach(adj -> addNext(adj, v, false, from, queue));

        while (!queue.isEmpty()) {
            NextVertex curr = queue.dequeue();

            if (curr.up) {
                markedUp[curr.id] = true;
                markedDown[curr.id] = true;
            } else
                markedDown[curr.id] = true;

            if (curr.id == w) {
                int pathLen = 1;
                int pos = curr.id;
                while (from[pos] != v) {
                    pos = from[pos];
                    pathLen += 1;
                }

                return pathLen;
            }

            digraph.adj(curr.id).forEach(adj -> {
                if (!markedUp[adj])
                    addNext(adj, curr.id, curr.up, from, queue);
            });

            reversed.adj(curr.id).forEach(adj -> {
                if (!markedDown[adj])
                    addNext(adj, curr.id, curr.up, from, queue);
            });
        }

        return -1;
    }

    private void addNext(int nextId, int currId, boolean currUp, int[] from, Queue<NextVertex> queue) {
        from[nextId] = currId;
        queue.enqueue(new NextVertex(nextId, currUp));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        boolean[] markedUp = new boolean[digraph.V()];
        boolean[] markedDown = new boolean[digraph.V()];
        int[] from = new int[digraph.V()];
        if (digraph.V() == 0)
            return -1;

        if (v == w)
            return 0;

        Queue<NextVertex> queue = new Queue<>();
        markedUp[v] = true;
        markedDown[v] = true;

        digraph.adj(v).forEach(adj -> addNext(adj, v, true, from, queue));
        reversed.adj(v).forEach(adj -> addNext(adj, v, false, from, queue));

        while (!queue.isEmpty()) {
            NextVertex curr = queue.dequeue();

            if (curr.up) {
                markedUp[curr.id] = true;
                markedDown[curr.id] = true;
            } else
                markedDown[curr.id] = true;

            if (curr.id == w) {
                return detectAncestor(w, from);
            }

            digraph.adj(curr.id).forEach(adj -> {
                if (!markedUp[adj])
                    addNext(adj, curr.id, curr.up, from, queue);
            });

            reversed.adj(curr.id).forEach(adj -> {
                if (!markedDown[adj])
                    addNext(adj, curr.id, curr.up, from, queue);
            });
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