package com.google.zetasql.toolkit.antipattern;

import com.google.zetasql.resolvedast.ResolvedNodes.ResolvedStatement;
import com.google.zetasql.toolkit.ZetaSQLToolkitAnalyzer;
import com.google.zetasql.toolkit.antipattern.visitors.crossjoin.CrossJoin;
import com.google.zetasql.toolkit.antipattern.visitors.crossjoin.CrossJoinVisitor;
import com.google.zetasql.toolkit.catalog.bigquery.BigQueryCatalog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class IdentifyCrossJoin implements BasePatternDetector {


  private final String CROSS_JOIN_SUGGESTION_MESSAGE =
      "CROSS JOIN between tables: %s and %s. Try to change for a INNER JOIN if possible.";
  ArrayList<String> result = new ArrayList<String>();

  public String run(
      String query,
      BigQueryCatalog catalog,
      ZetaSQLToolkitAnalyzer analyzer) {

    CrossJoinVisitor visitor = new CrossJoinVisitor();

    Iterator<ResolvedStatement> statementIterator = analyzer.analyzeStatements(query, catalog);
    statementIterator.forEachRemaining(statement -> statement.accept(visitor));

    List<CrossJoin> crossJoinList = visitor.getResult();
    crossJoinList.forEach(crossJoin -> parseCrossJoin(crossJoin));

    return StringUtils.join(result, "\n");
  }

  public void parseCrossJoin(CrossJoin crossJoin) {
    result.add(
        String.format(
            CROSS_JOIN_SUGGESTION_MESSAGE, crossJoin.getTableNames().toArray(new String[0])));
  }

}
