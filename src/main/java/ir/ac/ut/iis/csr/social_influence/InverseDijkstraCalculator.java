/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.social_influence;

import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.MeasureCalculator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shayan
 *
 * Code is based on the Dijkstra code at
 * https://www.geeksforgeeks.org/greedy-algorithms-set-6-dijkstras-shortest-path-algorithm/
 * retrieved on 23 Jun, 2018.
 *
 */
public class InverseDijkstraCalculator implements MeasureCalculator {

    private final GraphNode sourceNode;
    private final boolean considerWeights;
    private final double gamma;
    private final double alpha;

    public InverseDijkstraCalculator(GraphNode sourceNode, boolean considerWeights, double alpha, double gamma) {
        this.sourceNode = sourceNode;
        this.considerWeights = considerWeights;
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    public String toString() {
        return "Dijkstra";
    }

    @Override
    public int hashCode() {
        return sourceNode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InverseDijkstraCalculator other = (InverseDijkstraCalculator) obj;
        return sourceNode == other.sourceNode;
    }

    @Override
    public float[] calc(int numOfWeights, GraphNode node, Iterable<GraphNode> parent, int parentSize, short level) {
        float[] get = node.getMeasure(this);
        if (get != null) {
            return get;
        }

        FibonacciHeap<GraphNode> fh = new FibonacciHeap<>();
        Map<GraphNode, FibonacciHeap.Entry<GraphNode>> entryMap = new HashMap<>();

        for (GraphNode u : parent) {
            entryMap.put(u, fh.enqueue(u, Double.POSITIVE_INFINITY));
            if (u.getTmpArray() == null) {
                u.setTmpArray(new float[1]);
            }
            u.getTmpArray()[0] = 0;
        }

        // Distance of source vertex from itself is always 0
        fh.decreaseKey(entryMap.get(sourceNode), 0);

        // Find shortest path for all vertices
        for (int count = 0; count < parentSize - 1; count++) {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            FibonacciHeap.Entry<GraphNode> u = fh.dequeueMin();

            // Mark the picked vertex as processed
            u.getValue().getTmpArray()[0] = 1;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (GraphNode.HierarchicalEdge n : u.getValue().getEdges()) {
                if (n.hierarchyThreshold < level) {
                    break;
                }
                GraphNode otherSide = n.getOtherSide(u.getValue());
                FibonacciHeap.Entry<GraphNode> otherSideEntry = entryMap.get(otherSide);
                if (otherSide.getTmpArray()[0] == 0
                        && u.getPriority() != Double.POSITIVE_INFINITY
                        && u.getPriority() + (considerWeights ? (1.f / (gamma + n.getWeight()[0])) : 1) < otherSideEntry.getPriority()) {
                    fh.decreaseKey(otherSideEntry, u.getPriority() + (considerWeights ? (1.f / (gamma + n.getWeight()[0])) : 1));
                }
            }
        }

        double max = 0;
        for (GraphNode u : parent) {
            if (!u.equals(sourceNode)) {
                max = Math.max(max, 1 / (entryMap.get(u).getPriority()));
            }
        }
        for (GraphNode u : parent) {
            u.getMeasure().put(this, new float[]{(float) ((1 / (entryMap.get(u).getPriority())) / max)});
        }
        sourceNode.getMeasure().put(this, new float[]{(float) alpha});
        System.out.println("Dijkstra: " + this + " " + node.getMeasure(this)[0]);
        return node.getMeasure(this);
    }

    @Override
    public int getSeedsId() {
        return sourceNode.getId().getId();
    }

}
