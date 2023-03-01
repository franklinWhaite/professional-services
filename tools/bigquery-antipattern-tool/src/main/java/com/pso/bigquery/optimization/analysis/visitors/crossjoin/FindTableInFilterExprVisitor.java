package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedNodes;
import com.pso.bigquery.optimization.analysis.visitors.BaseAnalyzerVisitor;

public class CrossJoinFilterExprVisitor extends BaseAnalyzerVisitor {

    private int countColumnRefs = 0;
    public CrossJoinFilterExprVisitor(String projectId, SimpleCatalog catalog) {
        super(projectId, catalog);
    }

    public void visit(ResolvedNodes.ResolvedFilterScan resolvedFilterScan) {
        super.visit(resolvedFilterScan);
    }

    public void visit(ResolvedNodes.ResolvedFunctionCall resolvedFunctionCall) {
        if(resolvedFunctionCall.getFunction().toString().equals("ZetaSQL:$equal")) {
            System.out.println("======3=====");
            System.out.println(resolvedFunctionCall.toString());
            System.out.println("======4=====");
            resolvedFunctionCall.getArgumentList().forEach(System.out::println);
            resolvedFunctionCall.ge`
            System.out.println("======5=====");
        }
        super.visit(resolvedFunctionCall);
    }

    private void checkFilter(Resolved resolvedFunctionCall) {
        resolvedFunctionCall.getType()
    }


}
