/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.iis.csr.csr;

import ir.ac.ut.iis.person.Configs;
import ir.ac.ut.iis.person.algorithms.social_textual.MySQLConnector;
import ir.ac.ut.iis.person.hierarchy.GraphNode;
import ir.ac.ut.iis.person.hierarchy.Hierarchy;
import ir.ac.ut.iis.person.hierarchy.HierarchyNode;
import ir.ac.ut.iis.person.hierarchy.MeasureCalculator;
import ir.ac.ut.iis.person.hierarchy.PPRCalculator;
import ir.ac.ut.iis.person.hierarchy.UniformPPR;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shayan
 */
public class PageRankCacher {

    private final Connection conn;
    private final Map<Integer, PreparedStatement> userInsertStatements = new HashMap<>();
    private final PreparedStatement selfInsertStatement;
    private final String tableNameSuffix;
    private final Hierarchy<?> hier;
    private final double pageRankAlpha;

    public PageRankCacher(Hierarchy<?> hier, String databaseName, String tableNameSuffix, double pageRankAlpha) {
        this.tableNameSuffix = tableNameSuffix;
        this.conn = MySQLConnector.connect(databaseName);
        try {
            conn.setAutoCommit(false);
            conn.prepareStatement("DROP TABLE IF EXISTS `PPR_self_" + tableNameSuffix + Configs.databaseTablesPostfix + "`").executeUpdate();
            conn.prepareStatement("CREATE TABLE `PPR_self_" + tableNameSuffix + Configs.databaseTablesPostfix + "` ("
                    + "  `hierarchyNode_id` int(11) NOT NULL,"
                    + "  `PPR` varchar(256) DEFAULT NULL,"
                    + "  PRIMARY KEY (`hierarchyNode_id`)"
                    + ") ENGINE = MyISAM PACK_KEYS = 1 ROW_FORMAT = FIXED DEFAULT CHARSET=utf8;").executeUpdate();
            conn.commit();
            selfInsertStatement = conn.prepareStatement("insert into `PPR_self_" + tableNameSuffix + Configs.databaseTablesPostfix + "` values(?,?)");
        } catch (SQLException ex) {
            Logger.getLogger(PageRankCacher.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
        this.hier = hier;
        this.pageRankAlpha = pageRankAlpha;
    }

    public void cache(double pagerankAlpha) {
        cacheHierarchyNode(hier.getRootNode(), pagerankAlpha);
    }

//    public void loadPPRs() {
//        Map userNodeMapping = Main.hiers[0].getUserNodeMapping();
//        try (Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(Main.datasetRoot + "ppr-cache.txt")))) {
//            while (sc.hasNextLine()) {
//                String s = sc.nextLine();
//                String[] split = s.split(" ");
//                GraphNode get = (GraphNode) userNodeMapping.get(split[0]);
//                if (split.length > 1) {
//                    for (int i = 1; i < split.length; i++) {
//                        String[] split1 = split[i].split(":");
//                        get.addPPR(Integer.parseInt(split1[0]), Double.parseDouble(split1[1]));
//                    }
//                }
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(PageRankCacher.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException();
//        }
//    }
    int cnt = 0;

    boolean check = true;

    private void cacheHierarchyNode(HierarchyNode hn, double pagerankAlpha) {
//        if (hn.getParent() == null || !(hn.getParent().usersNum() > 100 * hn.usersNum())) {
        if (hn.getParent() != null) {
            StringBuilder sb = new StringBuilder();
            if (hn.getParent() != null) {
                HierarchyNode curr = hn;
                while (curr.getId() != Integer.MIN_VALUE) {
                    sb.insert(0, ":" + curr.getId());
                    curr = curr.getParent();
                }
            }
            System.out.println(sb.toString() + " " + hn.getLevel() + " " + hn.getParent().usersNum());
//            if (hn.usersNum() > 5000) {
                if (check) {
                    final float selfPPR = hn.selfPPR(pagerankAlpha);
                    store(selfPPR, hn.getParent().getUsers(), hn.getId(),
                            (hn.getParent() == null || hn.getParent().usersNum() >= 300) && hn.usersNum() >= 10);
                }
//            }
        }
//        if (hn.usersNum() > 5000) {
            for (HierarchyNode value : hn.getChildren().values()) {
                cacheHierarchyNode(value, pagerankAlpha);
            }
//        }
    }

    public void cacheTopic(Hierarchy<?> hier, String clustersFolder, int numberOfClusters) {
        for (int i = 0; i < numberOfClusters; i++) {
            Set<GraphNode> topicsNodes = new HashSet<>();
            try (Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(clustersFolder + "/hier_test" + i + ".tree")))) {
                while (sc.hasNextLine()) {
                    String[] split = sc.nextLine().split("\t");
                    topicsNodes.add(hier.getUserNode(Integer.parseInt(split[0])));
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PageRankCacher.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException();
            }
            cacheHierarchyNodeTopical(hier.getRootNode(), topicsNodes, (short) (-i - 1));
        }
    }

    protected void store(final float selfPPR, Iterable<GraphNode> users, int id, boolean storeUserPPRs) throws RuntimeException {
        String s = String.valueOf(selfPPR);

        try {
            selfInsertStatement.setInt(1, id);
            selfInsertStatement.setString(2, s);
            selfInsertStatement.executeUpdate();
            for (GraphNode n : users) {
                if (storeUserPPRs) {
                    if (n.getMeasure().size() > 1) {
                        throw new RuntimeException();
                    }
                    n.getMeasure().forEach((a, b) -> {
                        try {
                            PPRCalculator aa = (PPRCalculator) a;
                            PreparedStatement get = userInsertStatements.get(aa.getSeedsId());
                            if (get == null) {
                                conn.prepareStatement("DROP TABLE IF EXISTS `PPR_user_" + tableNameSuffix + "_" + a + Configs.databaseTablesPostfix + "`").executeUpdate();
                                conn.prepareStatement("CREATE TABLE `PPR_user_" + tableNameSuffix + "_" + a + Configs.databaseTablesPostfix + "` ("
                                        + "  `node_id` int,"
                                        + "  `PPR` varchar(256) DEFAULT NULL,"
                                        + "  PRIMARY KEY (`node_id`)"
                                        + ") ENGINE = MyISAM PACK_KEYS = 1 ROW_FORMAT = FIXED DEFAULT CHARSET=utf8;").executeUpdate();
                                get = conn.prepareStatement("insert into `PPR_user_" + tableNameSuffix + "_" + aa.getSeedsId() + Configs.databaseTablesPostfix + "` values(?,?)");
                                userInsertStatements.put(aa.getSeedsId(), get);
                            }
                            get.setInt(1, n.getId().getId());
                            get.setString(2, String.valueOf(b));
                            get.executeUpdate();
                        } catch (SQLException ex) {
                            Logger.getLogger(PageRankCacher.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
                n.getMeasure().clear();
            }
            conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(PageRankCacher.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }

    private void cacheHierarchyNodeTopical(HierarchyNode hn, Set<GraphNode> topic, short topicId) {
        float selfPPR = 0;
        for (GraphNode t : topic) {
            float PPR = new UniformPPR(topicId, hn.getUsers(), hn.usersNum(), pageRankAlpha).calc(hier.getNumberOfWeights(), t, hn.getUsers(), hn.usersNum(), (short) 0);
            selfPPR += PPR;
        }
        store(selfPPR, hn.getUsers(), topicId, true);
    }

    protected String convertToString(final float[] selfPPR) {
        StringBuilder sb1 = new StringBuilder(Float.toString(selfPPR[0]));
        for (int i = 1; i < selfPPR.length; i++) {
            sb1.append(",").append(selfPPR[i]);
        }
        String s = sb1.toString();
        return s;
    }

}
