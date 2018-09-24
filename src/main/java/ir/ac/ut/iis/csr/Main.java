package ir.ac.ut.iis.csr;

import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.DatasetMain;
import static ir.ac.ut.iis.person.Main.retrieve;
import ir.ac.ut.iis.person.PapersMain;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.csr.csr.PageRankCacher;
import ir.ac.ut.iis.csr.csr.CSRSimilarityTotalLevel;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.index.CorruptIndexException;
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
//        cachePageRanks();
    }

    public static void experiment() {
        Configs.baseSimilarityName = "MyLM";
        Configs.lmDirichletMu = 400;
        ir.ac.ut.iis.person.AddSearchers.reinitialize();
        Configs.loadGraph = true;
        Configs.ignoreTopLevelCluster = true;
        Configs.useCachedPPRs = false;
        try (PapersMain main = new PapersMain()) {
            main.main("T");
            CSRSimilarityTotalLevel hs = new CSRSimilarityTotalLevel(Configs.pagerankAlpha);
            ir.ac.ut.iis.person.AddSearchers.addBaseline();
            AddSearchers.addClusterBasedSearchers(hs);
            AddSearchers.addAggregateSearchersParameterTuning();
            AddSearchers.addSocialInfluenceParameterTuning();
//        ir.ac.ut.iis.person.AddSearchers.addAggregateSearchers(false);
            retrieve();
        }
    }

    private static void cachePageRanks() {
        Configs.loadGraph = true;
//        {
        Configs.graphFile = "topics/" + Configs.topicsName + "/authors_giant_graph_topics7.csv";
//        }
        try (PapersMain main = new PapersMain()) {
            main.main("CP");
            final Hierarchy<?> loadHierarchy = DatasetMain.getInstance().getHierarchy();
            PageRankCacher pageRankCacher = new PageRankCacher(loadHierarchy, Configs.database_name, String.valueOf(loadHierarchy.getNumberOfWeights() + "_" + Configs.pagerankAlpha), Configs.pagerankAlpha);
            {
                pageRankCacher.cache(Configs.pagerankAlpha);
            }
//        {
//        pageRankCacher.cacheTopic(loadHierarchy, Configs.datasetRoot + "topics/15-SymmetricAlpha/tempTopics", 15);
//        }
        }
    }

    private static void initializeConfigs() {
        Configs.skipQueries = 0;
        Configs.useSearchCaching = false;
        Configs.datasetName = "aminer_>2002";
        Configs.database_name = "aminer_>2002";
        Configs.indexName = "index_15_SymmetricAlpha";
        Configs.clustersFileName = "PPC-1.3";
        Configs.topicsName = "15_SymmetricAlpha";
        Configs.runNumber = 110;
        {
//            Configs.ignoreSelfCitations = true;
//            Configs.useSearchCaching = false;
        }
        ir.ac.ut.iis.person.AddSearchers.reinitialize();
    }

}
