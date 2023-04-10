package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import com.google.zetasql.parser.ASTNode;
import com.google.zetasql.parser.ASTNodes.ASTTablePathExpression;
import java.util.ArrayList;
import java.util.List;

public class CrossJoinSide {

  private List<String> tableNameList = new ArrayList<>();

  CrossJoinSide(ASTNode astNode) {
    setup(astNode);
  }

  private void setup(ASTNode astNode) {
    if(astNode instanceof ASTTablePathExpression) {
      System.out.println(((ASTTablePathExpression) astNode).getAlias().getIdentifier().getIdString());
      tableNameList.add(((ASTTablePathExpression) astNode).getAlias().getIdentifier().getIdString());
    }
  }

}
