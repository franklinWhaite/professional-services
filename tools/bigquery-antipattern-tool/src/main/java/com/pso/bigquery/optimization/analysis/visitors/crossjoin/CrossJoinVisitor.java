package com.pso.bigquery.optimization.analysis.visitors;

import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedNode;
import com.google.zetasql.resolvedast.ResolvedNodes.*;

import java.util.ArrayList;
import java.util.List;

public class CrossJoinVisitor extends BaseAnalyzerVisitor {

    public CrossJoinVisitor(String projectId, SimpleCatalog catalog) {
        super(projectId, catalog);
    }

    public void visit(ResolvedJoinScan resolvedJoinScan) {
        checkForCrossJoin(resolvedJoinScan);
        super.visit(resolvedJoinScan);
    }

    public void checkForCrossJoin(ResolvedJoinScan resolvedJoinScan) {
        ResolvedExpr joinsExpr = resolvedJoinScan.getJoinExpr();
        if(joinsExpr == null && resolvedJoinScan.getJoinType().toString().equals("INNER")) {
            ResolvedScan leftNode = resolvedJoinScan.getLeftScan();
            ResolvedScan rightNode = resolvedJoinScan.getRightScan();
            new CrossJoinChildNode(leftNode);
            new CrossJoinChildNode(rightNode);
        }
    }


    public String getResult() {
        return "";
    }

    private class CrossJoinChildNode {
        public String type;
        public String name;
        CrossJoinChildNode(ResolvedScan resolvedScan) {
            setup(resolvedScan);
        }

        private void setup(ResolvedScan resolvedScan) {
            if(resolvedScan instanceof ResolvedTableScan) {
                type = "TABLE";
                name = ((ResolvedTableScan) resolvedScan).getTable().getFullName();
            } else if (resolvedScan instanceof ResolvedJoinScan) {
                type = "JOIN_SCAN";
                name = null;
            } else if (resolvedScan instanceof ResolvedProjectScan) {
                type = "SUBQUERY";
                name = null;
            }
        }

    }

    private class CrossJoin() {
        public CrossJoinChildNode left;
        public CrossJoinChildNode right;

        CrossJoin(CrossJoinChildNode left, CrossJoinChildNode right) {
            this.left = left;
            this.right = right;

        }
    }




}
