package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import com.google.zetasql.parser.ASTNodes;
import com.google.zetasql.parser.ASTNodes.ASTJoin;
import com.google.zetasql.parser.ASTNodes.ASTWhereClause;
import com.google.zetasql.parser.ParseTreeVisitor;
import java.util.ArrayList;
import java.util.Stack;

public class IdentifyCrossJoinVisitor extends ParseTreeVisitor {

  private final static String JOIN_TYPE_CROSS = "CROSS";
  private final static String CROSS_JOIN_MESSAGE = "CROSS JOIN instead of INNER JOIN between %s and %s.";

  private Stack<ASTWhereClause> filterStack = new Stack<ASTWhereClause>();

  private ArrayList<String> result = new ArrayList<String>();

  public ArrayList<String> getResult() {
    return result;
  }


  @Override
  public void visit(ASTNodes.ASTJoin joinNode) {
    if(joinNode.getJoinType().toString().equals(JOIN_TYPE_CROSS) || (joinNode.getJoinType().toString().equals("DEFAULT_JOIN_TYPE") && joinNode.getOnClause()==null)) {
      CrossJoinSide lhs = new CrossJoinSide(joinNode.getLhs());
      CrossJoinSide rhs = new CrossJoinSide(joinNode.getRhs());
      CrossJoin crossJoin = new CrossJoin(lhs, rhs);
      CrossJoinFilterChecker crossJoinFilterChecker = new CrossJoinFilterChecker();
      crossJoinFilterChecker.setCrossJoin(crossJoin);
      filterStack.peek().accept(crossJoinFilterChecker);
      if(crossJoinFilterChecker.result()) {
        result.add(String.format(CROSS_JOIN_MESSAGE,crossJoin.getNamesTablesUsedOnFilter().toArray(new String[0])));
      }
    }
    super.visit(joinNode);
  }

  @Override
  public void visit(ASTNodes.ASTSelect node) {
    ASTWhereClause whereNode = node.getWhereClause();
    if(!(whereNode==null)) {
      filterStack.add(whereNode);
      super.visit(node);
      filterStack.pop();
    } else {
      super.visit(node);
    }
  }


}
