package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTTablePathExpression;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;

public class IdentifySimpleSelectStarVisitor extends ParseTreeVisitor {

  private final String SUGGESTION_MESSAGE =
      "SELECT * on table: %s. Check that all columns are needed.";

  private final String SELECT_STAR_NODE_KIND_STRING = "Star";
  private boolean foundFrom = false;
  private boolean foundJoin = false;
  private boolean isSimpleSelect = true;
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
    super.visit(selectNode);
  }

  @Override
  public void visit(ASTNodes.ASTFromClause node) {
    if((!foundFrom) && isSimpleSelect){
      foundFrom = true;
      super.visit(node);
    } else {
      isSimpleSelect = false;
    }
  }

  @Override
  public void visit(ASTNodes.ASTGroupBy node) {
      isSimpleSelect = false;
  }

  @Override
  public void visit(ASTNodes.ASTJoin node) {
    if((!foundJoin) && isSimpleSelect){
      foundJoin = true;
      super.visit(node);
    } else {
      isSimpleSelect = false;
    }
  }

  public ArrayList<String> getResult() {
    if(isSimpleSelect) {
      return result;
    } else {
      return new ArrayList<String>();
    }

  }


}
