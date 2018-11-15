/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.algorithms.aggregate;

import ir.ac.ut.iis.person.algorithms.aggregate.MyValueSource;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.query.Query;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;

/**
 *
 * @author shayan
 */
public abstract class GraphValueSource extends MyValueSource {

    protected final ScoreMergeStrategy mergeStrategy;
    protected final Hierarchy<?> hier;
    protected int graphId;
    protected boolean isStatic;

    public GraphValueSource(Hierarchy<?> hier, int graphId, ScoreMergeStrategy mergeStrategy) {
        this(hier, mergeStrategy);
        this.isStatic = true;
        this.graphId = graphId;
    }

    public GraphValueSource(Hierarchy<?> hier, ScoreMergeStrategy mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
        this.hier = hier;
        isStatic = false;
    }

    @Override
    public abstract void initialize(Query query);

    public void setGraphId(int graphId) {
        if (isStatic) {
            throw new RuntimeException();
        }
        this.graphId = graphId;
    }
    protected boolean check;

    @Override
    public abstract FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException;

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum ScoreMergeStrategy {
        SUM,
        AVG,
        MAX,
        FIRST_ONLY
    }

    public static double[] mergeScores(ScoreMergeStrategy mergeStrategy, Collection<double[]> scores, double[] firstScore) {
        double[] score = new double[firstScore.length];
        switch (mergeStrategy) {
            case FIRST_ONLY:
                return firstScore;
            case SUM:
                for (double[] d : scores) {
                    for (int t = 0; t < firstScore.length; t++) {
                        score[t] += d[t];
                    }
                }
                return score;
            case AVG:
                int count = 0;
                for (double[] d : scores) {
                    for (int t = 0; t < firstScore.length; t++) {
                        score[t] += d[t];
                    }
                    count++;
                }
                for (int t = 0; t < firstScore.length; t++) {
                    score[t] /= count;
                }
                return score;
            case MAX:
                for (double[] d : scores) {
                    for (int t = 0; t < firstScore.length; t++) {
                        score[t] = Math.max(score[t], d[t]);
                    }
                }
                return score;
            default:
                throw new RuntimeException();
        }
    }

    public Hierarchy<?> getHierarchy() {
        return hier;
    }

    @Override
    public String getName() {
        return mergeStrategy.name();
    }

}
