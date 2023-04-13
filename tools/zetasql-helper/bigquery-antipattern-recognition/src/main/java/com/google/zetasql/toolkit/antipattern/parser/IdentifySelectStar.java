package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.IdentifySelectStarVisitor;
import java.util.stream.Collectors;


public class IdentifySelectStar {

  public String run(ASTStatement parsedQuery) {
    IdentifySelectStarVisitor visitor = new IdentifySelectStarVisitor();
    parsedQuery.accept(visitor);

    return visitor.getResult().stream().distinct().collect(Collectors.joining("\n"));
  }
}
