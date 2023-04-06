package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.InSubqueryWithoutAggVisitor;

public class IdentifyInSubqueryWithoutAgg implements BasePatternDetector{

  @Override
  public String run(ASTStatement parsedQuery) {
    parsedQuery.accept(new InSubqueryWithoutAggVisitor());
    return null;
  }
}
