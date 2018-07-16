/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.csr;

import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.HierarchyNode;
import ir.ac.ut.iis.person.hierarchy.PPRCalculator;
import ir.ac.ut.iis.csr.hierarchy.ConstantVectorPPR;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * کمترین میزان محافظه کاری را در انتخاب یک پابلیشر دارد٬ حتی اگر یک پابلیشر در
 * کلاستر جستجو کننده وجود داشته باشد فرض می کند که یک سند در کلاستر سرچر قرار
 * دارد.
 *
 * Sensivity is maximum
 *
 * @author M.A.Tavallaie
 */
public class CSRSimilarityTotalLevel {

    protected short currentFlag = 0;

    protected NextNodeResult nextNode(HierarchyNode currentNode, Set<GraphNode> publishers) {
        NextNodeResult rVale = new NextNodeResult(new TreeSet<>(), new TreeSet<>());
        for (GraphNode user : publishers) {
            HierarchyNode child = currentNode.getChild(user);

            if (child != null && child.getFlag() != 0 && child.getFlag() == currentFlag) {
                rVale.contained.add(user);
            } else {
                rVale.notContained.add(user);
            }
        }

        return rVale;
    }

    public void activeNode(GraphNode searcherUser) {
        currentFlag += 1;
        HierarchyNode currentNode = searcherUser.getHierarchyNode();
        while (currentNode != null) {
            currentNode.setFlag(currentFlag);
            currentNode = currentNode.getParent();
        }
    }

    /**
     * Log of link probability رندم سرفر هایی که اطلاعات پابلیشر را در شبکه پخش
     * می کردند با چه احتمالی اطلاعات مربوط به پابلیشر را به سرچر می دهند
     *
     * @param currentNode
     * @param searcher
     * @param publishers
     * @param finalScores
     * @return
     */
    public double[] CSR(HierarchyNode currentNode, GraphNode searcher, Map<GraphNode, double[]> publishers, Map<GraphNode, double[]> finalScores, Map<Integer, float[]> priors, Integer priorId) {
        NextNodeResult nextLevel = nextNode(currentNode, publishers.keySet());
        HierarchyNode nextNode = currentNode.getChild(searcher);

        double[] total = new double[currentNode.getNumberOfWeights()];
        for (GraphNode notContained : nextLevel.notContained) {
            double[] totalUserProb = publishers.get(notContained);
            if (nextNode != null) {
                final float[] selfPPR = currentNode.selfPPR(makePPRCalculator(priors, priorId, currentNode));
                final float[] userPPR = nextNode.userPPR(makePPRCalculator(priors, priorId, nextNode), notContained);
//                System.out.println("UserPPR: " + nextNode.getId() + " " + notContained.getId().getId() + ": " + userPPR[0]);

                for (int t = 0; t < totalUserProb.length; t++) {
                    totalUserProb[t] += Math.log(selfPPR[t]) + Math.log(userPPR[t]);
                }
            } else {
                final float[] userPPR = currentNode.userPPR(makePPRCalculator(priors, priorId, currentNode), notContained);
//                System.out.println("UserPPR: " + currentNode.getId() + " " + notContained.getId().getId() + ": " + userPPR[0]);
                for (int t = 0; t < totalUserProb.length; t++) {
                    totalUserProb[t] += Math.log(userPPR[t]);
                }
            }
            publishers.put(notContained, totalUserProb);
            double[] finalScore = new double[totalUserProb.length];
            for (int t = 0; t < totalUserProb.length; t++) {
                finalScore[t] = Math.pow(Math.E, totalUserProb[t]);
                total[t] += finalScore[t];
            }
            finalScores.put(notContained, finalScore);
        }
        if (nextLevel.contained.isEmpty()) {
            return total;
        }

        Map<GraphNode, double[]> containedPublishers = new HashMap<>();
        for (GraphNode contained : nextLevel.contained) {
            final double[] get = publishers.get(contained);
            final float[] selfPPR = currentNode.selfPPR(makePPRCalculator(priors, priorId, currentNode));
            double[] sc = new double[currentNode.getNumberOfWeights()];
            for (int t = 0; t < sc.length; t++) {
                sc[t] = get[t] + Math.log(selfPPR[t]);
            }
            containedPublishers.put(contained, sc);
        }
        final double[] CSR = this.CSR(nextNode, searcher, containedPublishers, finalScores, priors, priorId);
        for (int t = 0; t < total.length; t++) {
            CSR[t] += total[t];
        }
        return CSR;
    }

    protected PPRCalculator makePPRCalculator(Map<Integer, float[]> priors, int priorId, HierarchyNode currentNode) {
        PPRCalculator pprCalculator = null;
        if (priors != null) {
            pprCalculator = new ConstantVectorPPR(currentNode.getId(), currentNode.getNumberOfWeights(), priorId, currentNode.getUsers(), priors, Configs.pagerankAlpha);
        }
        return pprCalculator;
    }

    public static class NextNodeResult {

        public Set<GraphNode> contained;
        public Set<GraphNode> notContained;

        public NextNodeResult(Set<GraphNode> contained, Set<GraphNode> notContained) {
            this.contained = contained;
            this.notContained = notContained;
        }

    }
}
