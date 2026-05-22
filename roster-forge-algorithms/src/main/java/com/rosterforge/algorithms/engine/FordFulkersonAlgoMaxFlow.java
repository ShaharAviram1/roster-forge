package com.rosterforge.algorithms.engine;

import com.rosterforge.algorithms.scoring.RosterScorer;
import com.rosterforge.algorithms.validation.ConstraintValidator;

import java.util.Arrays;

public class FordFulkersonAlgoMaxFlow extends AbstractAlgoMaxFlow {

    public FordFulkersonAlgoMaxFlow(ConstraintValidator constraintValidator, RosterScorer rosterScorer) {
        super(constraintValidator, rosterScorer);
    }

    // Single-pass draft mode: violations are reported for manager review rather than retried.
    @Override
    protected boolean useIterativeConstraintEnforcement() {
        return false;
    }

    @Override
    protected int[][] computeMaxFlow(int[][] capacity, int totalNodes) {
        int[][] flow = new int[totalNodes][totalNodes];
        int source = 0;
        int sink = totalNodes - 1;

        while (true) {
            int[] parent = new int[totalNodes];
            Arrays.fill(parent, -1);

            boolean found = dfs(capacity, flow, source, sink, totalNodes, new boolean[totalNodes], parent);
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

    private boolean dfs(int[][] capacity, int[][] flow,
                        int u, int sink, int totalNodes,
                        boolean[] visited, int[] parent) {
        if (u == sink) return true;
        visited[u] = true;

        for (int v = 0; v < totalNodes; v++) {
            if (!visited[v] && capacity[u][v] - flow[u][v] > 0) {
                parent[v] = u;
                if (dfs(capacity, flow, v, sink, totalNodes, visited, parent)) return true;
            }
        }
        return false;
    }
}
