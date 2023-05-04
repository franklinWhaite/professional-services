package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.OrderByWithoutLimitVisitor;

import java.util.stream.Collectors;

public class IdentifyOrderByWithoutLimit implements BasePatternDetector{

  @Override
  public String run(ASTStatement parsedQuery) {
    OrderByWithoutLimitVisitor visitor = new OrderByWithoutLimitVisitor();
    parsedQuery.accept(visitor);

    return visitor.getResult().stream().distinct().collect(Collectors.joining("\n"));
  }
}
