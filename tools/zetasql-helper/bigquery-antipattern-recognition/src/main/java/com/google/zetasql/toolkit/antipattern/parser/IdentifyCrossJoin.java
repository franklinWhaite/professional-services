package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin.IdentifyCrossJoinVisitor;
import org.apache.commons.lang3.StringUtils;

public class IdentifyCrossJoin {
  public String run(ASTStatement parsedQuery) {
    IdentifyCrossJoinVisitor visitor = new IdentifyCrossJoinVisitor();
    parsedQuery.accept(visitor);

    return StringUtils.join(visitor.getResult(), "\n");
  }

}
