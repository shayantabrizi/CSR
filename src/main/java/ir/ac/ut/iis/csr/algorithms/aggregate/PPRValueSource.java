/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.algorithms.aggregate;

import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.hierarchy.MeasureCalculator;
import ir.ac.ut.iis.person.hierarchy.UniformPPR;
import ir.ac.ut.iis.csr.algorithms.aggregate.GraphValueSource.ScoreMergeStrategy;
import java.util.Collections;

/**
 *
 * @author shayan
 */
public class PPRValueSource extends SingleSourceValueSource {

    double pageRankAlpha;

    public PPRValueSource(Hierarchy<?> hier, ScoreMergeStrategy mergeStrategy, double pageRankAlpha) {
        super(hier, mergeStrategy);
        this.pageRankAlpha = pageRankAlpha;
    }

    public PPRValueSource(Hierarchy<?> hier, int graphId, ScoreMergeStrategy mergeStrategy, double pageRankAlpha) {
        super(hier, graphId, mergeStrategy);
        this.pageRankAlpha = pageRankAlpha;
    }

    @Override
    protected MeasureCalculator getMeasureCalculator(GraphNode searcher) {
        return new UniformPPR(searcher.getId().getId(), Collections.singleton(searcher), 1, pageRankAlpha);
    }

    @Override
    public String getName() {
        return "PPR(" + super.getName() + ")";
    }

}
