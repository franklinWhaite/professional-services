package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedNodes.*;
import com.pso.bigquery.optimization.analysis.visitors.BaseAnalyzerVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CrossJoinVisitor extends BaseAnalyzerVisitor {

  private final String INNER_JOIN_TYPE = "INNER";
  private List<CrossJoin> crossJoinList = new ArrayList<>();
  private Stack<ResolvedExpr> filterStack = new Stack<ResolvedExpr>();
  private FindFilterForCrossJoinVisitor findFilterForCrossJoinVisitor = null;

  public CrossJoinVisitor(String projectId, SimpleCatalog catalog) {
    super(projectId, catalog);
  }

  public void visit(ResolvedJoinScan resolvedJoinScan) {
    checkForCrossJoin(resolvedJoinScan);
    super.visit(resolvedJoinScan);
  }

  public void visit(ResolvedFilterScan resolvedFilterScan) {
    filterStack.push(resolvedFilterScan.getFilterExpr());
    super.visit(resolvedFilterScan);
    filterStack.pop();
  }

  public void checkForCrossJoin(ResolvedJoinScan resolvedJoinScan) {
    ResolvedExpr joinsExpr = resolvedJoinScan.getJoinExpr();
    if (joinsExpr == null
        && resolvedJoinScan.getJoinType().toString().equals(INNER_JOIN_TYPE)
        && filterStack.size() > 0) {

      CrossJoinChildNode leftNode = new CrossJoinChildNode(resolvedJoinScan.getLeftScan());
      CrossJoinChildNode rightNode = new CrossJoinChildNode(resolvedJoinScan.getRightScan());
      CrossJoin crossJoin = new CrossJoin(leftNode, rightNode);

      findFilterForCrossJoinVisitor =
          new FindFilterForCrossJoinVisitor(super.getProjectId(), super.getCatalog());
      findFilterForCrossJoinVisitor.setCrossJoin(crossJoin);

      filterStack.peek().accept(findFilterForCrossJoinVisitor);
      if (findFilterForCrossJoinVisitor.result()) {
        crossJoinList.add(crossJoin);
      }
    }
  }

  public List<CrossJoin> getResult() {
    return crossJoinList;
  }
}
