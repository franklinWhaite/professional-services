package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.InSubqueryWithoutAggVisitor;
import org.apache.commons.lang3.StringUtils;

public class IdentifyInSubqueryWithoutAgg implements BasePatternDetector{

  @Override
  public String run(ASTStatement parsedQuery) {
    InSubqueryWithoutAggVisitor visitor = new InSubqueryWithoutAggVisitor();
    parsedQuery.accept(visitor);

    return StringUtils.join(visitor.getResult(), "\n");
  }
}
