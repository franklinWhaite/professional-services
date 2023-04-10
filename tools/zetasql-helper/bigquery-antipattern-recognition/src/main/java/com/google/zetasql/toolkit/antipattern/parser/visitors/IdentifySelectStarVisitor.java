package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTTablePathExpression;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;

public class IdentifySelectStarVisitor extends ParseTreeVisitor {

  private final String SUGGESTION_MESSAGE =
      "SELECT * on table: %s. Check that all columns are needed.";

  private final String SELECT_STAR_NODE_KIND_STRING = "Star";

  private ArrayList<String> result = new ArrayList<String>();

  @Override
  public void visit(ASTNodes.ASTSelect selectNode) {
    selectNode.getSelectList().getColumns().forEach(selectColumnNode -> {
      if(selectColumnNode.getExpression().nodeKindString().equals(SELECT_STAR_NODE_KIND_STRING)) {
        if(selectNode.getFromClause().getTableExpression() instanceof ASTTablePathExpression) {
          String idString = ((ASTTablePathExpression) selectNode.getFromClause().getTableExpression()).getPathExpr().getNames().get(0).getIdString();
          result.add(String.format(SUGGESTION_MESSAGE, idString));
        }
      }
    });

  }

  public ArrayList<String> getResult() {
    return result;
  }


}
