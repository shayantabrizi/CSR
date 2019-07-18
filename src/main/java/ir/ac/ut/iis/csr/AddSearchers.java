/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr;

import ir.ac.ut.iis.person.paper.PapersRetriever;
import ir.ac.ut.iis.csr.algorithms.aggregate.CSRValueSource;
import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.DatasetMain;
import static ir.ac.ut.iis.person.Main.outputPath;
import static ir.ac.ut.iis.person.Main.retriever;
import ir.ac.ut.iis.person.algorithms.aggregate.AggregateSearcher;
import ir.ac.ut.iis.person.algorithms.social_textual.citeseerx.CiteseerxSocialTextualValueSource;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.csr.csr.CSRSimilarityTotalLevel;
import ir.ac.ut.iis.csr.social_influence.SocialInfluenceValueSource;
import ir.ac.ut.iis.person.algorithms.campos.IRR;
import ir.ac.ut.iis.person.algorithms.queries.MyLMQuery;
import ir.ac.ut.iis.person.algorithms.searchers.BasicSearcher;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.myretrieval.MyDummySimilarity;
import ir.ac.ut.iis.person.query.NormalizedQueryExpander;
import ir.ac.ut.iis.person.query.QueryConverter;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;

/**
 *
 * @author shayan
 */
public class AddSearchers {

    protected static void addClusterBasedSearchers(CSRSimilarityTotalLevel hs) {
        outputPath += "," + Configs.clusteredParameters();
//        addClusterBasedParameterTuningSearchers(hs);
        addClusterBasedDefaultSearchers(hs);
//        addClusterBasedAggregationSearchers(hs);
    }

    private static void addClusterBasedParameterTuningSearchers(CSRSimilarityTotalLevel hs) {
        Hierarchy<?> hier = DatasetMain.getInstance().getHierarchy();
//        for (int i = 0; i < 20; i += 1) {
//            MyValueSource myValueSource = new TavalValueSource(hier, hier.getNumberOfWeights() - 1, hs, 1, TavalValueSource.ScoreMergeStrategy.SUM);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_SUM-1.0-" + i / 10f, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), i / 10f, 1f));
//        }
//
//        Hierarchy hier2 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, false);
//        PPRValueSource pprValueSource = new PPRValueSource(hier2, hier2.getNumberOfWeights() - 1, TavalValueSource.ScoreMergeStrategy.SUM);
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-.50", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .5f, 1f));
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-0", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 0f, 1f));
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-.25", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .25f, 1f));
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-.75", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .75f, 1f));
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-1", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1f, 1f));
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-1.25", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.25f, 1f));
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR_SUM-1.0-1.5", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.5f, 1f));

        Hierarchy<?> hier3 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, false, false);
        for (int i = 0; i < 20; i += 1) {
            CSRValueSource myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Mod_SUM-1.0-" + i / 10f, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), i / 10f, 1f));
        }
    }

    private static void addClusterBasedDefaultSearchers(CSRSimilarityTotalLevel hs) {
        final float personalizationWeight = 1f;
        Hierarchy<?> hier = DatasetMain.getInstance().getHierarchy();
//          hier.getRootNode().loadSelfPPRs("Self_CSR_minimal.txt");
//        hier.getRootNode().loadPPRs("CSRs_minimal.txt", 0);
//        hier.getRootNode().injectDummyValues(100);
//      CSRValueSource myValueSource = new CSRValueSource(hier, hier.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), personalizationWeight, 1f));
//

//        Hierarchy<?> hier2 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, false, false, false);
//        PPRValueSource pprValueSource = new PPRValueSource(hier2, hier2.getNumberOfWeights() - 1, CSRValueSource.ScoreMergeStrategy.SUM, Configs.pagerankAlpha);
//        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR-" + pprValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), personalizationWeight, 1f));
//        Hierarchy<?> hier3 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, false, false, false);
//        {        
//        Map<Integer, Integer> map = new HashMap<>();
//        for (GraphNode g : hier3.getRootNode().getUsers()) {
//            if (g.getHierarchyNode().getParent().getChildren().size() > 5000) {
//                if (!map.containsKey(g.getHierarchyNode().getParent().getId())) {
//                    System.out.println(g.getHierarchyNode().getParent().getId() + " " + g.getHierarchyNode().getParent().usersNum() + " " + g.getHierarchyNode().getParent().getChildren().size());
//                }
//            }
//            map.putIfAbsent(g.getHierarchyNode().getParent().getId(), g.getHierarchyNode().getParent().getChildren().size());
//        }
//        Map<Integer, Integer> sortByValue = StopWordsExtractor.MapUtil.sortByValue(map);
//        for (var e : sortByValue.entrySet()) {
//            if (e.getValue() > 1000) {
//                System.out.println(e.getValue() + " " + e.getKey());
//            }
//        }
//        }
//        Hierarchy<?> hier3 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, false, false);
//        for (var c : hier3.getRootNode().getChildren().values()) {
//            System.out.println("size: " + c.usersNum());
//        }
//        System.exit(0);
//
        int ok = 0;
        int nok = 0;
        for (GraphNode u : hier.getUserNodeMapping().values()) {
            boolean check = true;
            if (PapersRetriever.checkBalanceness(u)) {
                ok++;
            } else {
                nok++;
            }
        }
        System.out.println(ok + " " + nok);
        System.exit(0);
//        try {
//            HierarchyNode.pprOSW = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream("CSRs_minimal.txt")));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(AddSearchers.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        Iterable<GraphNode> users = hier.getRootNode().getUsers();
//        List<GraphNode> list = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Integer random = ir.ac.ut.iis.person.Main.random(hier.getRootNode().usersNum());
//            Iterator<GraphNode> iterator = users.iterator();
//            for (int j = 0; j < random; j++) {
//                iterator.next();
//            }
//            list.add(iterator.next());
//        }
//        long millis1 = System.currentTimeMillis();
//        hier.getRootNode().precompute(0, 0, Configs.pagerankAlpha);
//        hier3.getRootNode().precomputePPRs(Configs.pagerankAlpha, list);
//        try {
//            HierarchyNode.pprOSW.close();
//        } catch (IOException ex) {
//            Logger.getLogger(AddSearchers.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        long millis2 = System.currentTimeMillis();
////
//        System.out.println("userPPRCount: " + HierarchyNode.userPPRCount);
//        System.out.println("selfPPRCount: " + HierarchyNode.selfPPRCount);
//        System.out.println("millis1: " + millis1);
//        System.out.println("millis2: " + millis2);
//        System.exit(0);
//        hier3.getRootNode().loadSelfPPRs("Self_kol.txt");
//        hier3.getRootNode().loadPPRs("PPRs.txt", 5000);
//        hier3.getRootNode().loadSelfPPRs("Self_minimal.txt");
//        hier3.getRootNode().loadPPRs("PPRs_minimal.txt", -1);
//        hier3.getRootNode().injectDummyValues(100);
//        PPRLoader.extractMinimalPPRs(hier3,"PPRs_kol.txt", "PPR_minimal.txt" );
//        PPRLoader.extractSelfPPRs(hier, "CSRs_minimal.txt", "Self_CSR_minimal.txt");
//        PPRLoader.addToDatabase(hier3, "PPRs.txt", 6000);
//        System.exit(0);
//        CSRValueSource myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "P-CSR-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), personalizationWeight, 1f));
//        Hierarchy<?> hier4 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, false, true, false);
//        myValueSource = new CSRValueSource(hier4, hier4.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Part-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), personalizationWeight, 1f));
//
//        Hierarchy<?> hier5 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, true, false);
//        myValueSource = new CSRValueSource(hier5, hier5.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "P-CSR_Part-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), personalizationWeight, 1f));
    }

    public static void addAggregateSearchersParameterTuning() {
        outputPath += "," + Configs.socialTextualParameters();
//        MyValueSource myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-.85", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .85f, .15f));
//        for (double i = 1; i > .001; i /= 2) {
//            CiteseerxSocialTextualValueSource myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, i);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-.8-" + i, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .8f, .2f));
//        }
//        for (float i = .5f; i <= .95f; i += .05f) {
//            myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .01);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-" + i + "-.01", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), i, 1 - i));
//        }
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .01);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-.85-" + .01, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .85f, .15f));
        CiteseerxSocialTextualValueSource myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .09);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .8f, .2f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .90f, .10f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .7f, .3f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .4f, .6f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .5f, .5f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .6f, .4f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .7f, .3f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .8f, .2f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .9f, .1f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .95f, .05f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .85f, .15f));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .75f, .25f));
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .08);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .8f, .2f));
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .8f, .2f));
//        for (float i = .8f; i <= .96f; i += .05f) {
//            CiteseerxSocialTextualValueSource myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-TFIDF-pow-" + i, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), i, 1 - i));
//        }
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .01);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-pow-" + .85 + "-" + .01, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .85f, .15f, true));
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .01);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-pow-" + .01 + "-" + .01, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .01f, (1-.01f), true));
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .01);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-pow-" + .001 + "-" + .01, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .001f, (1-.001f), true));
//        myValueSource = new CiteseerxSocialTextualValueSource(Configs.database_name, .01);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "AggregateTFIDF-pow-" + .0001 + "-" + .01, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .0001f, (1-.0001f), true));
    }

    public static void addSocialInfluenceParameterTuning() {
        outputPath += "," + Configs.socialTextualParameters();
        Hierarchy<?> hier6 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, true, false);
//        for (double i = .0001; i <= .00051; i += .0001) {
//            SocialInfluenceValueSource myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, i);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-1.0-" + i, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
//        }
//        for (float i = .25f; i <= 1.5f; i += .25f) {
//            SocialInfluenceValueSource myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, .0002);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-" + i + "-.0002", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), i, 1f));
//        }
//        SocialInfluenceValueSource myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 55);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 55, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 50);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 50, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 60);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 60, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 65);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 65, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 70);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 70, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        SocialInfluenceValueSource myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 120);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 120, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));

        SocialInfluenceValueSource myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, false, 2.5, 0);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .25f, .75f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .15f, .85f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .3f, .7f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .15f, .85f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .25f, .75f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 0);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 0);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .3f, .7f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 0);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 2.5, 1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 1, 1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 5, 1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 50, 1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 100, 1);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));

//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.05-" + 120, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .05f, .95f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.2-" + 120, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.15-" + 120, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .15f, .85f, false, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, 400);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.1-" + 400, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.05-" + 50, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .05f, .95f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-.15-" + 50, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .15f, .85f, false, true));
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-1.0-" + 2, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
//        for (float i = .075f; i <= .151f; i += .025f) {
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-" + i + "-" + 2, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), i, (1f - i), false, true));
//        }
//        for (int i = 8; i <= 128; i *= 2f) {
//            SocialInfluenceValueSource myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, i);
//            retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-TFIDF-norm-" + .15 + "-" + i, new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .15f, .85f, false, true));
//        }
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, .0002);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-pow-1.0-" + .0002, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f, true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, .0002);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-pow-0.001-" + .0002, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .001f, (1-.001f), true));
//        myValueSource = new SocialInfluenceValueSource(hier6, TavalValueSource.ScoreMergeStrategy.SUM, .0002);
//        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "SocialInfluence_SUM-pow-0.0001-" + .0002, ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .0001f, (1-.0001f), true));
    }

    private static void addClusterBasedAggregationSearchers(CSRSimilarityTotalLevel hs) {
        Hierarchy<?> hier3 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, true, false);
        CSRValueSource myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Mod_SUM-1.0-1.0", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
        myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.MAX);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Mod_MAX-1.0-1.0", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
        myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.AVG);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Mod_AVG-1.0-1.0", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
        myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Mod_FIRST-1.0-1.0", ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
    }

    public static void addMethodBasedEvaluatorSearchers() {
        final QueryConverter queryConverter = ir.ac.ut.iis.person.AddSearchers.getQueryConverter();
        Similarity sim = new LMDirichletSimilarity(100);
        BasicSearcher searcher = new BasicSearcher(DatasetMain.getInstance().getIndexSearcher(), "LM-100", sim, queryConverter);
        retriever.addSearcher(searcher);
        sim = new MyDummySimilarity(400, new MyLMQuery((400)));
        searcher = new BasicSearcher(DatasetMain.getInstance().getIndexSearcher(), "MyLM-400", sim, queryConverter);
        retriever.addSearcher(searcher);
        sim = new ClassicSimilarity();
        searcher = new BasicSearcher(DatasetMain.getInstance().getIndexSearcher(), "Classic", sim, queryConverter);
        retriever.addSearcher(searcher);
        NormalizedQueryExpander normalizedQueryExpander = new NormalizedQueryExpander(20, Configs.evaluator, .66);
        retriever.addSearcher(new IRR(DatasetMain.getInstance().getIndexSearcher(), "IRR", new LMDirichletSimilarity(100), queryConverter, normalizedQueryExpander));
    }

    private AddSearchers() {
    }

}
