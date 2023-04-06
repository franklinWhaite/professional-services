package com.google.zetasql.toolkit.antipattern.parser.visitors;

import com.google.zetasql.parser.ASTNodes.ASTInExpression;
import com.google.zetasql.parser.ASTNodes.ASTSelect;
import com.google.zetasql.parser.ParseTreeVisitor;

public class InSubqueryWithoutAggVisitor extends ParseTreeVisitor {

  @Override
  public void visit(ASTInExpression node) {
    // System.out.println(node);
    if(!(node.getQuery() == null)){
      if(node.getQuery().getQueryExpr() instanceof ASTSelect) {
        ASTSelect select = (ASTSelect) node.getQuery().getQueryExpr();
        System.out.println("has distinct = " + select.getDistinct());
        System.out.println("has group by = " + !(select.getGroupBy() == null));
      }
    }

    //System.out.println(node.getQuery().getQueryExpr());
    super.visit(node);
  }
}
