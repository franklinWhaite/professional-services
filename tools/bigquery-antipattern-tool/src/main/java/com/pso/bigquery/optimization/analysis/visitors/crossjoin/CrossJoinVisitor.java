package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedNodes.*;
import com.pso.bigquery.optimization.analysis.visitors.BaseAnalyzerVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CrossJoinVisitor extends BaseAnalyzerVisitor {

    private final String INNER_JOIN_TYPE = "INNER";

    Stack<ResolvedExpr> filterStack = new Stack<ResolvedExpr>();
    FindTableInFilterExprVisitor findTableInFilterExprVisitor = null;

    private List<CrossJoin> crossJoinList = new ArrayList<>();
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
        if(joinsExpr == null && resolvedJoinScan.getJoinType().toString().equals(INNER_JOIN_TYPE) && filterStack.size()>0) {

            CrossJoinChildNode leftNode = new CrossJoinChildNode(resolvedJoinScan.getLeftScan());
            CrossJoinChildNode rightNode = new CrossJoinChildNode(resolvedJoinScan.getRightScan());

            findTableInFilterExprVisitor = new FindTableInFilterExprVisitor(super.getProjectId(), super.getCatalog());
            findTableInFilterExprVisitor.setLeftSideTableList(leftNode.getTableNameList());
            findTableInFilterExprVisitor.setRightSideTableList(rightNode.getTableNameList());

            filterStack.peek().accept(findTableInFilterExprVisitor);
            if(findTableInFilterExprVisitor.result()){
                crossJoinList.add(new CrossJoin(leftNode, rightNode));
            }


        }

    }

    public List<CrossJoin>  getResult() {
        return crossJoinList;
    }




}
