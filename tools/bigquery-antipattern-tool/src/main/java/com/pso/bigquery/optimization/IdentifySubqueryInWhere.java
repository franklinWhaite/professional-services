package com.pso.bigquery.optimization;

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoin;
import com.pso.bigquery.optimization.analysis.visitors.subqueryinwhere.SubqueryInWhereVisitor;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class IdentifySubqueryInWhere implements BasePatternDetector{

  private final String SUBQUERY_IN_WHERE_SUGGESTION_MESSAGE =
      "You are using an IN filter with a subquery without a DISTINCT on the following columns: ";
  ArrayList<String> result = new ArrayList<String>();

  public String run(
      String query,
      String billingProjectId,
      SimpleCatalog catalog,
      QueryAnalyzer.CatalogScope catalogScope) {

    SubqueryInWhereVisitor visitor = new SubqueryInWhereVisitor(billingProjectId, catalog);
    parser.visitQuery(billingProjectId, query, catalog, catalogScope, visitor);
    visitor.getResult().forEach(subqueryInWhereColumns -> parseResults(subqueryInWhereColumns));

    return StringUtils.join(result, "\n");
  }

  public void parseResults(String subqueryInWhereColumns) {
    result.add(
            SUBQUERY_IN_WHERE_SUGGESTION_MESSAGE + subqueryInWhereColumns);
  }

}
