/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.social_influence;

import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.hierarchy.MeasureCalculator;
import ir.ac.ut.iis.person.query.Query;
import ir.ac.ut.iis.csr.algorithms.aggregate.SingleSourceValueSource;

/**
 *
 * @author shayan
 */
public class SocialInfluenceValueSource extends SingleSourceValueSource {

    private final boolean considerWeights;
    private final double alpha;
    private final double gamma;

    public SocialInfluenceValueSource(Hierarchy<?> hier, ScoreMergeStrategy mergeStrategy, boolean considerWeights, double alpha, double gamma) {
        super(hier, mergeStrategy);
        this.considerWeights = considerWeights;
        this.alpha = alpha;
        this.gamma = gamma;
    }

    @Override
    protected MeasureCalculator getMeasureCalculator(Query query, GraphNode searcher) {
        return new InverseDijkstraCalculator(searcher, considerWeights, alpha, gamma);
    }

    @Override
    public String getName() {
        return "SocialInfluence-" + super.getName() + "-" + considerWeights + "-" + alpha + "-" + gamma;
    }

}
