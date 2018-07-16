/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.algorithms.aggregate;

import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.hierarchy.MeasureCalculator;
import ir.ac.ut.iis.person.hierarchy.UniformPPR;
import ir.ac.ut.iis.person.query.Query;
import ir.ac.ut.iis.csr.algorithms.aggregate.GraphValueSource.ScoreMergeStrategy;
import java.util.Collections;

/**
 *
 * @author shayan
 */
public class PPRValueSource extends SingleSourceValueSource {

    public PPRValueSource(Hierarchy<?> hier, ScoreMergeStrategy mergeStrategy) {
        super(hier, mergeStrategy);
    }

    public PPRValueSource(Hierarchy<?> hier, int graphId, ScoreMergeStrategy mergeStrategy) {
        super(hier, graphId, mergeStrategy);
    }

    @Override
    protected MeasureCalculator getMeasureCalculator(Query query, GraphNode searcher) {
        return new UniformPPR(query.getSearcher(), Collections.singleton(searcher), 1, Configs.pagerankAlpha);
    }

}
