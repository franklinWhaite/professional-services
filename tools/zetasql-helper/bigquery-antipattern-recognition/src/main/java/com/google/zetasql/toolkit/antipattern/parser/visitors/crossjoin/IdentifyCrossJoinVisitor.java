package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTJoin;
import com.google.zetasql.parser.ASTNodes.ASTWhereClause;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;
import java.util.Stack;

public class IdentifyCrossJoinVisitor extends ParseTreeVisitor {

  private final static String JOIN_TYPE_CROSS = "CROSS";

  private Stack<ASTWhereClause> filterStack = new Stack<ASTWhereClause>();

  private ArrayList<String> result = new ArrayList<String>();

  public ArrayList<String> getResult() {
    return result;
  }


  @Override
  public void visit(ASTNodes.ASTFromClause node) {
    if(node.getTableExpression() instanceof ASTJoin) {
      ASTJoin joinNode = (ASTJoin) node.getTableExpression();
      if(joinNode.getJoinType().toString().equals(JOIN_TYPE_CROSS)) {
        System.out.println("Found CROSS JOIN");
        CrossJoinSide lhs = new CrossJoinSide(joinNode.getLhs());
        CrossJoinSide rhs = new CrossJoinSide(joinNode.getRhs());
      }
    }
    super.visit(node);
  }

  @Override
  public void visit(ASTNodes.ASTWhereClause node) {
    filterStack.add(node);
    super.visit(node);
    filterStack.pop();
  }

}
