import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
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
        return length(List.of(v), List.of(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        return ancestor(List.of(v), List.of(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in
    // w; -1 if no such path
    public int length(Iterable<Integer> vs, Iterable<Integer> ws) {
        return path(vs, ws).size() - 1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> vs, Iterable<Integer> ws) {
        return ancestorInPath(path(vs, ws));
    }

    private ArrayList<Integer> path(Iterable<Integer> vs, Iterable<Integer> ws) {
        for (Integer v : vs) {
            if (v == null || v < 0 || v >= digraph.V())
                throw new IllegalArgumentException();
        }
        for (Integer w : ws) {
            if (w == null || w < 0 || w >= digraph.V())
                throw new IllegalArgumentException();
        }

        ArrayList<Integer> result = new ArrayList<>();
        boolean[] markedUp = new boolean[digraph.V()];
        boolean[] markedDown = new boolean[digraph.V()];
        int[] from = new int[digraph.V()];
        for (int i = 0; i < from.length; i++)
            from[i] = -1;

        if (digraph.V() == 0)
            return result;

        Queue<NextVertex> queue = new Queue<>();
        for (int v : vs) {
            queue.enqueue(new NextVertex(v, true));
            markedUp[v] = true;
            markedDown[v] = true;
        }

        while (!queue.isEmpty()) {
            NextVertex curr = queue.dequeue();
            // if (curr.up)
            // System.out.println("> Dequeue [up] id=" + curr.id);
            // else
            // System.out.println("> Dequeue [down] id=" + curr.id);

            if (curr.up)
                markedUp[curr.id] = true;
            else
                markedDown[curr.id] = true;

            if (contains(ws, curr.id)) {
                // Destination reached
                Stack<Integer> stack = new Stack<>();

                stack.push(curr.id);

                int prev = from[curr.id];
                // System.out.println("===> prev for " + curr.id + " = " + prev);

                while (prev != -1) {
                    stack.push(prev);
                    // System.out.println("====> prev for " + prev + " = " + from[prev]);
                    prev = from[prev];
                }

                // System.out.println("PATH");
                for (int s : stack) {
                    result.add(s);
                    // System.out.print(s + " ");
                }
                // System.out.println();

                return result;
            }

            if (curr.up) {
                digraph.adj(curr.id).forEach(adj -> {
                    if (!markedUp[adj]) {
                        queue.enqueue(new NextVertex(adj, true));
                        if (from[adj] == -1)
                            from[adj] = curr.id;
                        // System.out.println(" > Enqueue [up] id=" + adj + " from=" + curr.id);
                    }
                });
            }

            reversed.adj(curr.id).forEach(adj -> {
                if (!markedUp[adj] && !markedDown[adj]) {
                    queue.enqueue(new NextVertex(adj, false));
                    if (from[adj] == -1)
                        from[adj] = curr.id;
                    // System.out.println(" > Enqueue [down] id=" + adj + " from=" + curr.id);
                }
            });

            // System.out.println();
        }

        return result;
    }

    private int ancestorInPath(ArrayList<Integer> path) {
        int size = path.size();

        if (size == 0)
            return -1;

        if (size == 1)
            return path.get(0);

        int first = path.get(0);
        int second = path.get(1);

        if (contains(reversed.adj(first), second))
            return first;

        int last = path.get(size - 1);
        int prev = path.get(size - 2);
        if (contains(reversed.adj(last), prev))
            return last;

        for (int i = 1; i < path.size() - 1; i++) {
            int before = path.get(i - 1);
            int curr = path.get(i);
            int after = path.get(i + 1);

            Iterable<Integer> adjacentsDown = reversed.adj(curr);
            boolean isAncestor = contains(adjacentsDown, before, after);

            // System.out
            // .println("> Checking if " + curr + " (i=" + i + ") is ancestor, before=" +
            // before + "after=" + after
            // + " :" + isAncestor);

            // System.out.print(" adjacents down:");
            // for (int adj : adjacentsDown) {
            // System.out.print(" " + adj);
            // }
            // System.out.println();

            if (isAncestor)
                return path.get(i);

        }

        // throw new RuntimeException("No ancestor found");

        return -2;
    }

    private boolean contains(Iterable<Integer> items, int item) {
        return StreamSupport.stream(items.spliterator(), false)
                .anyMatch(i -> item == i);
    }

    private boolean contains(Iterable<Integer> items, int item1, int item2) {
        return contains(items, item1) && contains(items, item2);
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