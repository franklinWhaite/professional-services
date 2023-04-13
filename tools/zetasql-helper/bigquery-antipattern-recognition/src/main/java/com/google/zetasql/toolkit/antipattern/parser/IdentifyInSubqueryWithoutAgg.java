package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.InSubqueryWithoutAggVisitor;
import java.util.stream.Collectors;

public class IdentifyInSubqueryWithoutAgg implements BasePatternDetector{

  @Override
  public String run(ASTStatement parsedQuery) {
    InSubqueryWithoutAggVisitor visitor = new InSubqueryWithoutAggVisitor();
    parsedQuery.accept(visitor);

    return visitor.getResult().stream().distinct().collect(Collectors.joining("\n"));
  }
}
