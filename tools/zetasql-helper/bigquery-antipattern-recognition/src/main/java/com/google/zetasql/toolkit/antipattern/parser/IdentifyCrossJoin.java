package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin.IdentifyCrossJoinVisitor;
import java.util.stream.Collectors;

public class IdentifyCrossJoin implements BasePatternDetector {
  public String run(ASTStatement parsedQuery) {
    IdentifyCrossJoinVisitor visitor = new IdentifyCrossJoinVisitor();
    parsedQuery.accept(visitor);

    return visitor.getResult().stream().distinct().collect(Collectors.joining("\n"));
  }

}
