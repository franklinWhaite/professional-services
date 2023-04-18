package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.IdentifySimpleSelectStarVisitor;
import java.util.stream.Collectors;

/*
* Identifies only the simple "select *":
* SELECT * FROM table
 */
public class IdentifySimpleSelectStar {

  public String run(ASTStatement parsedQuery) {
    IdentifySimpleSelectStarVisitor visitor = new IdentifySimpleSelectStarVisitor();
    parsedQuery.accept(visitor);

    return visitor.getResult().stream().distinct().collect(Collectors.joining("\n"));
  }
}
