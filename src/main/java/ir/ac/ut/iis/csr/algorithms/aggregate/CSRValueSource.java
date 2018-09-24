/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.algorithms.aggregate;

import ir.ac.ut.iis.csr.csr.CSRSimilarityTotalLevel;
import ir.ac.ut.iis.person.DatasetMain;
import ir.ac.ut.iis.person.Main;
import ir.ac.ut.iis.person.base.Statistic;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.query.Query;
import java.io.IOException;
import java.util.Collection;
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
public class CSRValueSource extends GraphValueSource {

    private GraphNode searcher;
    private final CSRSimilarityTotalLevel hs;
    private final float alpha;
    private Map<Integer, float[]> priors;
    private int priorId;

    private static final Map<String, Statistic> statisticsMap = new HashMap<>();

    public CSRValueSource(Hierarchy<?> hier, int graphId, CSRSimilarityTotalLevel hs, float alpha, ScoreMergeStrategy mergeStrategy) {
        super(hier, graphId, mergeStrategy);
        this.hs = hs;
        this.alpha = alpha;
        final Statistic statistic = new Statistic(getName());
        statisticsMap.put(getName(), statistic);
    }

    public CSRValueSource(Hierarchy<?> hier, CSRSimilarityTotalLevel hs, float alpha, ScoreMergeStrategy mergeStrategy) {
        super(hier, mergeStrategy);
        this.hs = hs;
        this.alpha = alpha;
        final Statistic statistic = new Statistic(getName());
        statisticsMap.put(getName(), statistic);
    }

    @Override
    public void initialize(Query query) {
//        if (query.equals(queryCache)) {
//            map = mapCache;
//        } else {
        searcher = hier.getUserNode(query.getSearcher());
        if (Main.i % 100 == 99) {
            hier.getRootNode().pruneMeasures(1000);
        }

//            map = calcWeights(Integer.parseInt(query.getSearcher()), Main.khodaeiDegree);
//            mapCache = map;
//        }
        final Statistic get = statisticsMap.get(getName());
//        System.out.println(get.toString());
        get.initialize();
//        check = true;
    }

    public void setPriors(Map<Integer, float[]> priors) {
        this.priors = priors;
    }

    public void setPriorId(int priorId) {
        this.priorId = priorId;
    }

    @Override
    public void setGraphId(int graphId) {
        if (isStatic) {
            throw new RuntimeException();
        }
        this.graphId = graphId;
    }
    boolean check;

    @Override
    public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
        return new FunctionValues() {

            @Override
            public float floatVal(int doc) {
                String[] authors;
                try {
//                    System.out.println((readerContext.docBase + doc) + " " + doc);
                    authors = readerContext.reader().document(doc).get("authors").split(" ");
                } catch (IOException ex) {
                    Logger.getLogger(CSRValueSource.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException();
                }

                Map<GraphNode, double[]> finalScores = new HashMap<>();
                if (check) {
                    authors = new String[hier.getUserNodeMapping().size()];
                    int t = 0;
                    for (Integer k : hier.getUserNodeMapping().keySet()) {
                        authors[t] = k.toString();
                        t++;
                    }
                }
                GraphNode firstAuthor = calcScores(hs, hier, searcher, authors, finalScores, mergeStrategy, priors, priorId);
                if (check) {
                    double sum = 0;
                    for (Map.Entry<GraphNode, double[]> e : finalScores.entrySet()) {
                        sum += e.getValue()[0];
                    }
                    System.out.println("CSR Sum=" + sum);
                    check = false;
                }

                final double[] normalizedScore = mergeScores(mergeStrategy, finalScores.values(), finalScores.get(firstAuthor));
                for (int i = 0; i < normalizedScore.length; i++) {
                    normalizedScore[i] = normalizedScore[i] * alpha + 1. / DatasetMain.getInstance().getIndexReader().numDocs() * (1 - alpha);
                }

//                System.out.println(normalizedScore + " " + Math.log(normalizedScore));
                statisticsMap.get(getName()).add(Math.log(normalizedScore[graphId]));
                return (float) Math.log(normalizedScore[graphId]);
            }

            @Override
            public String toString(int doc) {
                throw new UnsupportedOperationException();
            }
        };
    }

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

    @Override
    public String getName() {
        return super.getName() + "-" + alpha;
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

    @Override
    public Hierarchy<?> getHierarchy() {
        return hier;
    }

    public static GraphNode calcScores(CSRSimilarityTotalLevel hs, Hierarchy<?> hier, GraphNode searcher, final String[] authors, Map<GraphNode, double[]> finalScores, ScoreMergeStrategy mergeStrategy, Map<Integer, float[]> priors, Integer priorId) {
        Map<GraphNode, double[]> publishers = new HashMap<>();
        GraphNode firstAuthor = null;
        for (String a : authors) {
            final GraphNode userNode = hier.getUserNode(Integer.parseInt(a));

            if (firstAuthor == null) {
                firstAuthor = userNode;
            }
            publishers.put(userNode, new double[hier.getNumberOfWeights()]);
            if (mergeStrategy.equals(ScoreMergeStrategy.FIRST_ONLY)) {
                break;
            }
        }
        hs.CSR(hier.getRootNode(), searcher, publishers, finalScores, priors, priorId);
        return firstAuthor;
    }

}
