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

    private final double pageRankAlpha;
    public Map<Integer, Map<Integer, Float>> map;

    public CSRSimilarityTotalLevel() {
        this(Configs.pagerankAlpha);
    }

    public CSRSimilarityTotalLevel(double pageRankAlpha) {
        this.pageRankAlpha = pageRankAlpha;
    }

    protected NextNodeResult nextNode(HierarchyNode currentNode, Set<GraphNode> publishers, HierarchyNode searcherNode) {
        NextNodeResult rVale = new NextNodeResult(new TreeSet<>(), new TreeSet<>());
        for (GraphNode user : publishers) {
            HierarchyNode child = currentNode.getChild(user);

            if (child != null && child.isEqualToOrAncestorOf(searcherNode)) {
                rVale.contained.add(user);
            } else {
                rVale.notContained.add(user);
            }
        }

        return rVale;
    }

    /**
     * Log of link probability رندم سرفر هایی که اطلاعات پابلیشر را در شبکه پخش
     * می کردند با چه احتمالی اطلاعات مربوط به پابلیشر را به سرچر می دهند
     *
     * @param currentNode
     * @param searcher
     * @param publishers
     * @param finalScores
     * @param priors
     * @param priorId
     * @return
     */
    public void CSR(HierarchyNode currentNode, GraphNode searcher, Map<GraphNode, Double> publishers, Map<GraphNode, Double> finalScores, Map<Integer, float[]> priors, Integer priorId) {
        NextNodeResult nextLevel = nextNode(currentNode, publishers.keySet(), searcher.getHierarchyNode());
        HierarchyNode nextNode = currentNode.getChild(searcher);

        double[] total = new double[currentNode.getNumberOfWeights()];
        for (GraphNode notContained : nextLevel.notContained) {
            double totalUserProb = publishers.get(notContained);
            if (nextNode != null) {
                final float selfPPR = currentNode.selfPPR(makePPRCalculator(priors, priorId, currentNode), pageRankAlpha);
                float userPPR = -1;
                if (map != null) {
                    final Float val = map.get(notContained.getId().getId()).get(nextNode.getId());
                    if (val != null) {
                        userPPR = val;
                    }
                }
                if (userPPR == -1) {
                    userPPR = nextNode.userPPR(makePPRCalculator(priors, priorId, nextNode), pageRankAlpha, notContained);
                }

//                System.out.println("UserPPR: " + nextNode.getId() + " " + notContained.getId().getId() + ": " + userPPR[0]);
                totalUserProb += Math.log(selfPPR) + Math.log(userPPR);
            } else {
                float userPPR = -1;
                if (map != null) {
                    final Float val = map.get(notContained.getId().getId()).get(currentNode.getId());
                    if (val != null) {
                        userPPR = val;
                    }
                }
                if (userPPR == -1) {
                    userPPR = currentNode.userPPR(makePPRCalculator(priors, priorId, currentNode), pageRankAlpha, notContained);
                }
//                System.out.println("UserPPR: " + currentNode.getId() + " " + notContained.getId().getId() + ": " + userPPR[0]);
                totalUserProb += Math.log(userPPR);
            }
            publishers.put(notContained, totalUserProb);
            finalScores.put(notContained, Math.pow(Math.E, totalUserProb));
        }
        if (nextLevel.contained.isEmpty()) {
            return;
        }

        Map<GraphNode, Double> containedPublishers = new HashMap<>();
        for (GraphNode contained : nextLevel.contained) {
            final double get = publishers.get(contained);
            final float selfPPR = currentNode.selfPPR(makePPRCalculator(priors, priorId, currentNode), pageRankAlpha);
            double sc;
            sc = get + Math.log(selfPPR);
            containedPublishers.put(contained, sc);
        }
        this.CSR(nextNode, searcher, containedPublishers, finalScores, priors, priorId);
    }

    protected PPRCalculator makePPRCalculator(Map<Integer, float[]> priors, int priorId, HierarchyNode currentNode) {
        PPRCalculator pprCalculator = null;
        if (priors != null) {
            pprCalculator = new ConstantVectorPPR(currentNode.getId(), currentNode.getNumberOfWeights(), priorId, currentNode.getUsers(), priors, pageRankAlpha);
        }
        return pprCalculator;
    }

    public double getPageRankAlpha() {
        return pageRankAlpha;
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
