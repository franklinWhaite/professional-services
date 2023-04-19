package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes.ASTOrderBy;
import com.google.zetasql.parser.ASTNodes.ASTLimitOffset;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;

public class OrderByWithoutLimitVisitor extends ParseTreeVisitor {

  private final String ORDER_BY_SUGGESTION_MESSAGE =
      "Check that Order By clause without Limit.";

  private ArrayList<String> result = new ArrayList<String>();
  private ArrayList<String> resultToReturn = new ArrayList<String>();
  private Boolean LimitExist = Boolean.FALSE;
  @Override
  public void visit(ASTOrderBy node) {
    if(!(node.getOrderingExpressions().isEmpty())){
      result.add(ORDER_BY_SUGGESTION_MESSAGE);
    }
    super.visit(node);
  }

  @Override
  public void visit(ASTLimitOffset node) {
    if(!(node.getLimit() == null)){
      LimitExist = Boolean.TRUE;
    }
    super.visit(node);
  }

  public ArrayList<String> getResult() {
    if(!LimitExist){
      return result;
    }
    return resultToReturn;
  }
}
