package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import com.google.zetasql.parser.ASTNodes.ASTTablePathExpression;

public class CrossJoinUtil {

  public static String getNameFromTablePathExpression(ASTTablePathExpression tablePathExpression) {

    if (!(tablePathExpression.getAlias() == null)) {
      return tablePathExpression.getAlias().getIdentifier().getIdString();
    } else {
      return tablePathExpression
              .getPathExpr()
              .getNames()
              .get(tablePathExpression.getPathExpr().getNames().size() - 1)
              .getIdString();
    }
  }
}
