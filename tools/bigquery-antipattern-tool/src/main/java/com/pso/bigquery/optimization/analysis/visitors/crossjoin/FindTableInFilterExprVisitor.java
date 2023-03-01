package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import com.google.common.collect.ImmutableList;
import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedNodes;
import com.pso.bigquery.optimization.analysis.visitors.BaseAnalyzerVisitor;
import java.util.List;

public class FindTableInFilterExprVisitor extends BaseAnalyzerVisitor {

    private int countColumnRefs = 0;
    private Boolean foundFilterWithLeftTable = false;
    private Boolean foundFilterWithRightTable = false;
    private Boolean foundFilterForCrossJoin = false;

    private List<String>  rightSideTableList = null;
    private List<String>  leftSideTableList = null;

    public FindTableInFilterExprVisitor(String projectId, SimpleCatalog catalog) {
        super(projectId, catalog);
    }

    public void setRightSideTableList(List<String> rightSideTableList) {
        this.rightSideTableList = rightSideTableList;
    }

    public void setLeftSideTableList(List<String> leftSideTableList) {
        this.leftSideTableList = leftSideTableList;
    }

    public void visit(ResolvedNodes.ResolvedFilterScan resolvedFilterScan) {
        super.visit(resolvedFilterScan);
    }

    public void visit(ResolvedNodes.ResolvedFunctionCall resolvedFunctionCall) {
        if(!foundFilterForCrossJoin){
            if(resolvedFunctionCall.getFunction().toString().equals("ZetaSQL:$equal")) {
                System.out.println("======3=====");
                System.out.println(resolvedFunctionCall.toString());
                System.out.println("======4=====");
                checkArgumentList(resolvedFunctionCall.getArgumentList());
            }
            super.visit(resolvedFunctionCall);
        }
    }

    private void checkArgumentList(ImmutableList<ResolvedNodes.ResolvedExpr> argumentList) {
        foundFilterForCrossJoin = false;
        if(argumentList.size()==2) {
            for (ResolvedNodes.ResolvedExpr resolvedExpr : argumentList) {
                if(resolvedExpr instanceof ResolvedNodes.ResolvedColumnRef) {
                    // System.out.println("======5=====");
                    // System.out.println(((ResolvedNodes.ResolvedColumnRef) resolvedExpr).toString());
                    // System.out.println("======6=====");
                    // leftSideTableList.forEach(System.out::println);
                    // System.out.println("======7=====");
                    // rightSideTableList.forEach(System.out::println);
                    // System.out.println("======8=====");
                    String tableName = ((ResolvedNodes.ResolvedColumnRef) resolvedExpr).getColumn().getTableName();
                    if(leftSideTableList.contains(tableName)) {
                        foundFilterWithLeftTable = true;
                    } else if(rightSideTableList.contains(tableName)) {
                        foundFilterWithRightTable = true;
                    }
                }
            }
            if(foundFilterWithLeftTable && foundFilterWithRightTable) {
                foundFilterForCrossJoin = true;
            }
        }
        else {
            foundFilterForCrossJoin = false;
        }
    }

    public boolean result() {
        return foundFilterWithLeftTable;
    }

}
