package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class EdmondsKarpAlgoMaxFlow extends AbstractAlgoMaxFlow {

    public EdmondsKarpAlgoMaxFlow(ConstraintValidator constraintValidator, RosterScorer rosterScorer) {
        super(constraintValidator, rosterScorer);
    }

    @Override
    protected int[][] computeMaxFlow(int[][] capacity, int totalNodes) {
        int[][] flow = new int[totalNodes][totalNodes];
        int source = 0;
        int sink = totalNodes - 1;

        while (true) {
            int[] parent = new int[totalNodes];
            Arrays.fill(parent, -1);

            boolean found = bfs(capacity, flow, source, sink, totalNodes, parent);
            if (!found) break;

            int bottleneck = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                bottleneck = Math.min(bottleneck, capacity[u][v] - flow[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += bottleneck;
                flow[v][u] -= bottleneck;
            }
        }

        return flow;
    }

    private boolean bfs(int[][] capacity, int[][] flow,
                        int source, int sink, int totalNodes, int[] parent) {
        boolean[] visited = new boolean[totalNodes];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < totalNodes; v++) {
                if (!visited[v] && capacity[u][v] - flow[u][v] > 0) {
                    parent[v] = u;
                    if (v == sink) return true;
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return false;
    }
}
