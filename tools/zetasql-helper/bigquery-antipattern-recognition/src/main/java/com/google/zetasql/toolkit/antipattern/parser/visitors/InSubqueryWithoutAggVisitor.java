package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes.ASTInExpression;
import com.google.zetasql.parser.ASTNodes.ASTSelect;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;

public class InSubqueryWithoutAggVisitor extends ParseTreeVisitor {

  private final String SUBQUERY_IN_WHERE_SUGGESTION_MESSAGE =
      "You are using an IN filter with a subquery without a DISTINCT on the following columns: ";

  private ArrayList<String> result = new ArrayList<String>();
  @Override
  public void visit(ASTInExpression node) {
    // System.out.println(node);
    if(!(node.getQuery() == null)){
      if(node.getQuery().getQueryExpr() instanceof ASTSelect) {
        ASTSelect select = (ASTSelect) node.getQuery().getQueryExpr();
        if((!select.getDistinct()) && select.getGroupBy() == null) {
          result.add(SUBQUERY_IN_WHERE_SUGGESTION_MESSAGE);
        }
      }
    }

    //System.out.println(node.getQuery().getQueryExpr());
    super.visit(node);
  }

  public ArrayList<String> getResult() {
    return result;
  }
}
