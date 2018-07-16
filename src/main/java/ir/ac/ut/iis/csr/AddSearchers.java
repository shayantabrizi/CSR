/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr;

import ir.ac.ut.iis.csr.algorithms.aggregate.CSRValueSource;
import ir.ac.ut.iis.csr.algorithms.aggregate.PPRValueSource;
import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.DatasetMain;
import static ir.ac.ut.iis.person.Main.outputPath;
import static ir.ac.ut.iis.person.Main.retriever;
import ir.ac.ut.iis.person.algorithms.aggregate.AggregateSearcher;
import ir.ac.ut.iis.person.algorithms.aggregate.MyValueSource;
import ir.ac.ut.iis.person.algorithms.social_textual.citeseerx.CiteseerxSocialTextualValueSource;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.csr.csr.CSRSimilarityTotalLevel;
import ir.ac.ut.iis.csr.social_influence.SocialInfluenceValueSource;
import org.apache.lucene.search.similarities.ClassicSimilarity;

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
        Hierarchy<?> hier = DatasetMain.getInstance().getHierarchy();
        MyValueSource myValueSource = new CSRValueSource(hier, hier.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1f, 1f));

        Hierarchy<?> hier2 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, false, false, false);
        PPRValueSource pprValueSource = new PPRValueSource(hier2, hier2.getNumberOfWeights() - 1, CSRValueSource.ScoreMergeStrategy.SUM);
        retriever.addSearcher(new AggregateSearcher(pprValueSource, DatasetMain.getInstance().getIndexSearcher(), "PPR-" + pprValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1f, 1f));

        Hierarchy<?> hier3 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, false, false);
        myValueSource = new CSRValueSource(hier3, hier3.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "P-CSR-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));

        Hierarchy<?> hier4 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, false, true, false);
        myValueSource = new CSRValueSource(hier4, hier4.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "CSR_Part-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));

        Hierarchy<?> hier5 = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, true, false);
        myValueSource = new CSRValueSource(hier5, hier5.getNumberOfWeights() - 1, hs, 1, CSRValueSource.ScoreMergeStrategy.SUM);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), "P-CSR_Part-" + myValueSource.getName(), ir.ac.ut.iis.person.AddSearchers.getBaseSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), 1.0f, 1f));
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
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 0);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 0);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .3f, .7f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 0);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .1f, .9f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 2.5, 1);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 1, 1);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 5, 1);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 10, 1);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 50, 1);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));
        myValueSource = new SocialInfluenceValueSource(hier6, CSRValueSource.ScoreMergeStrategy.FIRST_ONLY, true, 100, 1);
        retriever.addSearcher(new AggregateSearcher(myValueSource, DatasetMain.getInstance().getIndexSearcher(), myValueSource.getName(), new ClassicSimilarity(), ir.ac.ut.iis.person.AddSearchers.getQueryConverter(), .2f, .8f, false, true));

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

    private AddSearchers() {
    }

}
