package com.pso.bigquery.optimization;

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoin;
import com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoinChildNode;
import com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoinVisitor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class IdentifyCrossJoin implements BasePatternDetector {

    ArrayList<String> result = new ArrayList<String>();
    public String run(String query, String billingProjectId, SimpleCatalog catalog, QueryAnalyzer.CatalogScope catalogScope) {
        CrossJoinVisitor visitor = new CrossJoinVisitor(billingProjectId, catalog);
        parser.visitQuery(billingProjectId, query, catalog, catalogScope, visitor);

        List<CrossJoin> crossJoinList = visitor.getResult();
        int numCrossJoinsFound = crossJoinList.size();
        crossJoinList.forEach(crossJoin -> parseCrossJoin(crossJoin, numCrossJoinsFound));
        return StringUtils.join(result, "\n");
    }

    public void parseCrossJoin(CrossJoin crossJoin, int numCrossJoinsFound) {
        result.add(String.format("CROSS JOIN between tables: %s and %s. Try to change for a INNER JOIN if possible.", crossJoin.getTableNames().toArray(new String[0])));
        // if(crossJoin.getType().equals(CrossJoin.CROSS_JOIN_TYPE_TWO_TABLES)) {
        //     result.add(String.format("CROSS JOIN between tables: %s and %s. Try to change for a INNER JOIN if possible.", crossJoin.getTableNames().toArray(new String[0])));
        // } else if (crossJoin.getType().equals(CrossJoin.CROSS_JOIN_TYPE_TABLE_SUBQUERY)) {
        //     result.add(String.format("CROSS JOIN between table %s and subquery. Try to change for a INNER JOIN if possible.", crossJoin.getTableNames().toArray(new String[0])));
        // } else if (crossJoin.getType().equals(CrossJoin.CROSS_JOIN_TYPE_TABLE_JOIN)) {
        //     result.add(String.format("CROSS JOIN with table: %s. Try to change for a INNER JOIN if possible.", crossJoin.getTableNames().toArray(new String[0])));
        // } else if (crossJoin.getType().equals(CrossJoin.CROSS_JOIN_TYPE_SUBQUERY_SUBQUERY)) {
        //     result.add("CROSS JOIN between two subqueries. Try to change for a INNER JOIN if possible.");
        // } else if (crossJoin.getType().equals(CrossJoin.CROSS_JOIN_TYPE_SUBQUERY_JOIN) && numCrossJoinsFound==1) {
        //     result.add("CROSS JOIN in query. Try to change for a INNER JOIN if possible.");
        // }
    }

}
