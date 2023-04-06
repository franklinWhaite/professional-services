package com.google.zetasql.toolkit.antipattern.analyzer;

import com.google.zetasql.resolvedast.ResolvedNodes.ResolvedStatement;
import com.google.zetasql.toolkit.ZetaSQLToolkitAnalyzer;
import com.google.zetasql.toolkit.antipattern.analyzer.visitors.subqueryinwhere.SubqueryInWhereVisitor;
import com.google.zetasql.toolkit.catalog.bigquery.BigQueryCatalog;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

public class IdentifySubqueryInWhere implements BasePatternDetector {

  private final String SUBQUERY_IN_WHERE_SUGGESTION_MESSAGE =
      "You are using an IN filter with a subquery without a DISTINCT on the following columns: ";
  ArrayList<String> result = new ArrayList<String>();

  public String run(
      String query,
      BigQueryCatalog catalog,
      ZetaSQLToolkitAnalyzer analyzer) {

    SubqueryInWhereVisitor visitor = new SubqueryInWhereVisitor();

    Iterator<ResolvedStatement> statementIterator = analyzer.analyzeStatements(query, catalog);

    statementIterator.forEachRemaining(statement -> statement.accept(visitor));

    visitor.getResult().forEach(subqueryInWhereColumns -> parseResults(subqueryInWhereColumns));

    return StringUtils.join(result, "\n");
  }

  public void parseResults(String subqueryInWhereColumns) {
    result.add(SUBQUERY_IN_WHERE_SUGGESTION_MESSAGE + subqueryInWhereColumns);
  }
}
