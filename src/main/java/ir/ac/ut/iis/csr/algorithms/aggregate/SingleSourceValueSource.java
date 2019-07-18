/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.algorithms.aggregate;

import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.hierarchy.MeasureCalculator;
import ir.ac.ut.iis.person.query.Query;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;

/**
 *
 * @author shayan
 */
public abstract class SingleSourceValueSource extends GraphValueSource {

    private Query queryCache;
    private MeasureCalculator measureCalculator;

    public SingleSourceValueSource(Hierarchy<?> hier, GraphValueSource.ScoreMergeStrategy mergeStrategy) {
        super(hier, mergeStrategy);
    }

    public SingleSourceValueSource(Hierarchy<?> hier, int graphId, ScoreMergeStrategy mergeStrategy) {
        super(hier, graphId, mergeStrategy);
    }

    @Override
    public void initialize(Query query) {
        if (query.equals(queryCache)) {
            return;
        }
        queryCache = query;
        initialize(query.getSearcher());
    }

    private void initialize(int s) {
        for (GraphNode u : hier.getRootNode().getUsers()) {
            u.resetMeasure();
        }
        GraphNode searcher = hier.getUserNode(s);
        measureCalculator = getMeasureCalculator(searcher);
        float measure = searcher.getMeasure(measureCalculator);
        if (measure == -1) {
            measureCalculator.calc(hier.getNumberOfWeights(), searcher, hier.getRootNode().getUsers(), hier.getRootNode().usersNum(), (short) 0);
        }
    }

    protected abstract MeasureCalculator getMeasureCalculator(GraphNode searcher);

    @Override
    public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
        return new FunctionValues() {

            @Override
            public float floatVal(int doc) {
                final String[] authors;
                try {
//                    System.out.println((readerContext.docBase + doc) + " " + doc);
                    authors = readerContext.reader().document(doc).get("authors").split(" ");
                } catch (IOException ex) {
                    Logger.getLogger(SingleSourceValueSource.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException();
                }

                Map<GraphNode, double[]> finalScores = new HashMap<>();
                GraphNode firstAuthor = calcScores(hier, measureCalculator, authors, finalScores, mergeStrategy);

                final double[] normalizedScore = CSRValueSource.mergeScores(mergeStrategy, finalScores.values(), finalScores.get(firstAuthor));

                if (normalizedScore[graphId] == Double.POSITIVE_INFINITY) {
                    System.out.println("");
                }
//                System.out.println(normalizedScore + " " + Math.log(normalizedScore));
                return (float) Math.log(normalizedScore[graphId]);
            }

            @Override
            public String toString(int doc) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static GraphNode calcScores(Hierarchy<?> hier, MeasureCalculator measureCalculator, final String[] authors, Map<GraphNode, double[]> finalScores, GraphValueSource.ScoreMergeStrategy mergeStrategy) {
        GraphNode firstAuthor = null;
        for (String a : authors) {
            final GraphNode userNode = hier.getUserNode(Integer.parseInt(a));

            if (firstAuthor == null) {
                firstAuthor = userNode;
            }
            float measure = userNode.getMeasure(measureCalculator);
            double[] pp = new double[]{measure};
            finalScores.put(userNode, pp);
            if (mergeStrategy.equals(GraphValueSource.ScoreMergeStrategy.FIRST_ONLY)) {
                break;
            }
        }
        return firstAuthor;
    }

}
