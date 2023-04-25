package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTOrderBy;
import com.google.zetasql.parser.ASTNodes.ASTLimitOffset;
import com.google.zetasql.parser.ParseTreeVisitor;
import com.google.zetasql.toolkit.antipattern.parser.IdentifyCrossJoin;

import java.util.ArrayList;

public class OrderByWithoutLimitVisitor extends ParseTreeVisitor {

  private final String ORDER_BY_SUGGESTION_MESSAGE =
      "ORDER BY clause without LIMIT.";

  private ArrayList<String> result = new ArrayList<String>();
  private ArrayList<String> resultToReturn = new ArrayList<String>();
  private Boolean LimitExist = Boolean.FALSE;

  @Override
  public void visit(ASTNodes.ASTQuery node) {
    if(!(node.getOrderBy()==null) && (node.getLimitOffset()==null)) {
      result.add(ORDER_BY_SUGGESTION_MESSAGE);
    }
    super.visit(node);
  }

  public ArrayList<String> getResult() {
      return result;
  }
}
