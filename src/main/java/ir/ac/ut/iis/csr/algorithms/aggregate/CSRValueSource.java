/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.algorithms.aggregate;

import ir.ac.ut.iis.csr.csr.CSRSimilarityTotalLevel;
import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.DatasetMain;
import ir.ac.ut.iis.person.Main;
import ir.ac.ut.iis.person.base.Statistic;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.paper.PapersRetriever;
import ir.ac.ut.iis.person.query.Query;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
        initialize(query.getSearcher());
    }

    private void initialize(int s) {
        //        if (query.equals(queryCache)) {
//            map = mapCache;
//        } else {
//        if (searcher != null) {
//            HierarchyNode hierarchyNode = searcher.getHierarchyNode();
//            while (hierarchyNode.usersNum() < 0) {
//                HierarchyNode hn = hierarchyNode;
//                hn.getParent().getUsers().forEach(user -> {
//                    user.getMeasure().remove(new UniformPPR(hn.getId(), hn.getUsers(), hn.usersNum(), hs.getPageRankAlpha()));
//                });
//                hierarchyNode = hierarchyNode.getParent();
//            }
//        }
        searcher = hier.getUserNode(s);
        if (Main.i % 100 == 99) {
            hier.getRootNode().pruneMeasures(Configs.pruneThreshold);
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
//                String[] authors;
////                Map<Integer, Map<Integer, Float>> map = null;
//                try {
////                    System.out.println((readerContext.docBase + doc) + " " + doc);
//                    authors = readerContext.reader().document(doc).get("authors").split(" ");
////                    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(readerContext.reader().document(doc).getBinaryValue("pprs").bytes))) {
////                        map = (Map<Integer, Map<Integer, Float>>) in.readObject();
////                    } catch (IOException | ClassNotFoundException ex) {
////                        Logger.getLogger(TopicsValueSource.class.getName()).log(Level.SEVERE, null, ex);
////                        throw new RuntimeException();
////                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(CSRValueSource.class.getName()).log(Level.SEVERE, null, ex);
//                    throw new RuntimeException();
//                }
//                hs.map = map;
                Map<GraphNode, Double> finalScores = new HashMap<>();
                final String[] authors = PapersRetriever.authors[readerContext.docBase + doc];
//                if (check) {
//                    authors = new String[hier.getUserNodeMapping().size()];
//                    int t = 0;
//                    for (Integer k : hier.getUserNodeMapping().keySet()) {
//                        authors[t] = k.toString();
//                        t++;
//                    }
//                }
                GraphNode firstAuthor = calcScores(hs, hier, searcher, authors, finalScores, mergeStrategy, priors, priorId);
                if (check) {
                    double sum = 0;
                    for (Map.Entry<GraphNode, Double> e : finalScores.entrySet()) {
                        sum += e.getValue();
                    }
                    System.out.println("CSR Sum=" + sum);
                    check = false;
                }

                double normalizedScore = mergeScores(mergeStrategy, finalScores.values(), finalScores.get(firstAuthor));
                normalizedScore = normalizedScore * alpha + 1. / DatasetMain.getInstance().getIndexReader().numDocs() * (1 - alpha);

//                System.out.println(normalizedScore + " " + Math.log(normalizedScore));
                statisticsMap.get(getName()).add(Math.log(normalizedScore));
                return (float) Math.log(normalizedScore);
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

    public static double mergeScores(ScoreMergeStrategy mergeStrategy, Collection<Double> scores, double firstScore) {
        double score = 0;
        switch (mergeStrategy) {
            case FIRST_ONLY:
                return firstScore;
            case SUM:
                for (double d : scores) {
                    score += d;
                }
                return score;
            case AVG:
                int count = 0;
                for (double d : scores) {
                    score += d;
                    count++;
                }
                score /= count;
                return score;
            case MAX:
                for (double d : scores) {
                    score = Math.max(score, d);
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

    public static GraphNode calcScores(CSRSimilarityTotalLevel hs, Hierarchy<?> hier, GraphNode searcher, final String[] authors, Map<GraphNode, Double> finalScores, ScoreMergeStrategy mergeStrategy, Map<Integer, float[]> priors, Integer priorId) {
        Map<GraphNode, Double> publishers = new HashMap<>();
        GraphNode firstAuthor = null;
        for (String a : authors) {
            final GraphNode userNode = hier.getUserNode(Integer.parseInt(a));

            if (firstAuthor == null) {
                firstAuthor = userNode;
            }
            publishers.put(userNode, 0.);
            if (mergeStrategy.equals(ScoreMergeStrategy.FIRST_ONLY)) {
                break;
            }
        }
        hs.CSR(hier.getRootNode(), searcher, publishers, finalScores, priors, priorId);
        return firstAuthor;
    }

}
