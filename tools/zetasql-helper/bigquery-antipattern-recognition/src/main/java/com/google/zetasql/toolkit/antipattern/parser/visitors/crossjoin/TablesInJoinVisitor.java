package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import com.google.zetasql.parser.ASTNode;
import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTJoin;
import com.google.zetasql.parser.ASTNodes.ASTTablePathExpression;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;
import java.util.List;

public class TablesInJoinVisitor extends ParseTreeVisitor {

  private List<String> tableNameList = new ArrayList<>();

  public List<String> getTableNameList() {
    return tableNameList;
  }

  @Override
  public void visit(ASTJoin joinNode) {
    checkSide(joinNode.getLhs());
    checkSide(joinNode.getRhs());
  }

  private void checkSide(ASTNode node) {
    if (node instanceof ASTTablePathExpression) {
      tableNameList.add(
          CrossJoinUtil.getNameFromTablePathExpression((ASTTablePathExpression) node));

    } else if (node instanceof ASTJoin) {
      super.visit((ASTJoin) node);
    }
  }
}
