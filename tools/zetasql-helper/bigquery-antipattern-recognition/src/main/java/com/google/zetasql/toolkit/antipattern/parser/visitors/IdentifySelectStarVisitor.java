package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTInExpression;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;

public class IdentifySelectStarVisitor extends ParseTreeVisitor {

  private final String SUGGESTION_MESSAGE =
      "SELECT *";

  private final String SELECT_STAR_NODE_KIND_STRING = "Star";

  private ArrayList<String> result = new ArrayList<String>();

  @Override
  public void visit(ASTNodes.ASTSelectColumn node) {
    System.out.println(node.getExpression().nodeKindString().equals(SELECT_STAR_NODE_KIND_STRING));
  }

  public ArrayList<String> getResult() {
    return result;
  }


}
