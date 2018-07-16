/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.hierarchy;

import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.PPRCalculator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author shayan
 */
public class ConstantVectorPPR extends PPRCalculator {

    private ArrayList<MyEntry> priors;
    private double sumOfPriors[];
    private final Iterable<GraphNode> users;
    private final Map<Integer, float[]> basePrior;
    private final int uniqueId;
    private final int numOfWeights;

    public ConstantVectorPPR(int topicNodeId, int numOfWeights, int uniqueId, Iterable<GraphNode> users, Map<Integer, float[]> basePrior, double alpha) {
        super(topicNodeId, alpha);
        this.users = users;
        this.basePrior = basePrior;
        this.uniqueId = uniqueId;
        this.numOfWeights = numOfWeights;
    }

    @Override
    public void updatePPRs(float[] zeroDegrees, double alpha) {
        if (priors == null) {
            if (sumOfPriors == null) {
                sumOfPriors = new double[numOfWeights];
            }
            priors = new ArrayList<>();
            for (GraphNode u : users) {
                final float[] get = basePrior.get(u.getId().getId());
                for (int i = 0; i < numOfWeights; i++) {
                    sumOfPriors[i] += get[i];
                }
                priors.add(new MyEntry(u, get));
            }
        }
        for (MyEntry u : priors) {
            for (int i = 0; i < numOfWeights; i++) {
                u.graphNode.getTmpArray()[i] = (float) (u.graphNode.getTmpArray()[i] + (alpha + zeroDegrees[i]) * u.prior[i] / sumOfPriors[i]);
            }
        }
    }

    @Override
    public String toString() {
        return topicNodeId + "_" + uniqueId + "_" + numOfWeights;
    }

    @Override
    public int hashCode() {
        if (numOfWeights > 1) {
            return Objects.hash(topicNodeId , 1);
        } else {
            return Objects.hash(topicNodeId, uniqueId);
        }
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
        final ConstantVectorPPR other = (ConstantVectorPPR) obj;
        if (topicNodeId != other.topicNodeId) {
            return false;
        }

        if (numOfWeights != other.numOfWeights) {
            return false;
        }

        if (numOfWeights == 1) {
            if (uniqueId != other.uniqueId) {
                return false;
            }
        }
        return true;
    }

    public static class MyEntry {

        GraphNode graphNode;
        float[] prior;

        public MyEntry(GraphNode graphNode, float[] prior) {
            this.graphNode = graphNode;
            this.prior = prior;
        }

    }

}
