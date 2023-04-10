package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.IdentifySelectStarVisitor;
import org.apache.commons.lang3.StringUtils;

public class IdentifySelectStar {

  public String run(ASTStatement parsedQuery) {
    IdentifySelectStarVisitor visitor = new IdentifySelectStarVisitor();
    parsedQuery.accept(visitor);

    return StringUtils.join(visitor.getResult(), "\n");
  }
}
