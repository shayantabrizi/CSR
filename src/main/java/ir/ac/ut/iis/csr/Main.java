package ir.ac.ut.iis.csr;

import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.DatasetMain;
import static ir.ac.ut.iis.person.Main.retrieve;
import ir.ac.ut.iis.person.PapersMain;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.csr.csr.PageRankCacher;
import ir.ac.ut.iis.csr.csr.CSRSimilarityTotalLevel;
import ir.ac.ut.iis.person.evaluation.person.PERSONEvaluator;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.HierarchyNode;
import ir.ac.ut.iis.person.hierarchy.PPRLoader;
import ir.ac.ut.iis.person.hierarchy.UniformPPR;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.store.LockObtainFailedException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Shayan
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, CorruptIndexException, LockObtainFailedException, LockObtainFailedException, IOException, DOMException, ParserConfigurationException, SAXException, QueryNodeException, FileNotFoundException, FileNotFoundException, IOException {
        initializeConfigs();
        experiment();        // Use with -Xmx4000m
//        PPRLoader.load("PPRs_kol.txt");
//        createMethodBasedJudgments();
//        cachePageRanks();
    }

    public static void experiment() {
        Configs.multiThreaded = false;
        Configs.baseSimilarityName = "MyLM";
        Configs.lmDirichletMu = 400;
        ir.ac.ut.iis.person.AddSearchers.reinitialize();
        Configs.loadGraph = true;
        Configs.ignoreTopLevelCluster = true;
        Configs.useCachedPPRs = false;
        Configs.pruneThreshold = -1;
        Configs.queryCount = 500;
        try (PapersMain main = new PapersMain()) {
            main.main("experiment");
            CSRSimilarityTotalLevel hs = new CSRSimilarityTotalLevel(Configs.pagerankAlpha);
//            ir.ac.ut.iis.person.AddSearchers.addBaseline();
            AddSearchers.addClusterBasedSearchers(hs);
//            AddSearchers.addAggregateSearchersParameterTuning();
//            AddSearchers.addSocialInfluenceParameterTuning();
//        ir.ac.ut.iis.person.AddSearchers.addAggregateSearchers(false);
            retrieve();
        }
    }

    private static void cachePageRanks() {
        Configs.loadGraph = true;
        Configs.multiThreaded = false;
//        {
//        Configs.graphFile = "topics/" + Configs.topicsName + "/authors_giant_graph_topics7.csv";
//        }
        try (PapersMain main = new PapersMain()) {
            main.main("CP");
//            final Hierarchy<?> hierarchy = DatasetMain.getInstance().getHierarchy();
            Hierarchy<?> hierarchy = DatasetMain.getInstance().loadHierarchy(Configs.datasetRoot + Configs.graphFile, Configs.datasetRoot + "clusters/" + Configs.clustersFileName + ".tree", Configs.clustersFileName, false, true, false, false);
            PageRankCacher pageRankCacher = new PageRankCacher(hierarchy, Configs.database_name, String.valueOf(hierarchy.getNumberOfWeights() + "_" + Configs.pagerankAlpha), Configs.pagerankAlpha);
            {
                pageRankCacher.cache(Configs.pagerankAlpha);
            }
//        {
//        pageRankCacher.cacheTopic(loadHierarchy, Configs.datasetRoot + "topics/15-SymmetricAlpha/tempTopics", 15);
//        }
        }
    }

    private static void createMethodBasedJudgments() {
        Configs.runStage = Configs.RunStage.CREATE_METHOD_BASED_JUDGMENTS;
        Configs.evaluator = new PERSONEvaluator();
        Configs.queryCount = 10_000;
        Configs.baseSimilarityName = "LM";
        Configs.lmDirichletMu = 100;
        Configs.useTFIDFWeightingInCampos = false;
        Configs.onlyQueriesWhoseAuthorHasMoreThan_THIS_Papers = 1;
        ir.ac.ut.iis.person.AddSearchers.reinitialize();
        try (PapersMain main = new PapersMain()) {
            main.main("createMethodBasedJudgments");
            AddSearchers.addMethodBasedEvaluatorSearchers();
            retrieve();
        }
    }

    private static void initializeConfigs() {
        Configs.skipQueries = 50;
        Configs.useSearchCaching = false;
        Configs.datasetName = "aminer_>2002";
        Configs.database_name = "aminer_>2002_2";
        Configs.indexName = "index_PPR";
        Configs.clustersFileName = "PPC-1.3";
        Configs.topicsName = "PPR";
        Configs.runNumber = 200;
        {
//            Configs.ignoreSelfCitations = true;
//            Configs.useSearchCaching = false;
        }
        ir.ac.ut.iis.person.AddSearchers.reinitialize();
    }
}
