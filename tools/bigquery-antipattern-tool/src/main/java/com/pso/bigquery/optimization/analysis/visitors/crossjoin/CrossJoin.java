package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import java.util.ArrayList;
import java.util.List;

import static com.pso.bigquery.optimization.analysis.visitors.crossjoin.CrossJoinChildNode.*;

public class CrossJoin {

    public static final String CROSS_JOIN_TYPE_TWO_TABLES = "TWO_TABLES";
    public static final String CROSS_JOIN_TYPE_TABLE_SUBQUERY = "TABLE-SUBQUERY";
    public static final String CROSS_JOIN_TYPE_TABLE_JOIN = "TABLE-JOIN";
    public static final String CROSS_JOIN_TYPE_SUBQUERY_JOIN = "SUBQUERY-JOIN";
    public static final String CROSS_JOIN_TYPE_SUBQUERY_SUBQUERY = "SUBQUERY-SUBQUERY";

    private CrossJoinChildNode left;
    private CrossJoinChildNode right;
    private String type;
    private List<String> tableNames = new ArrayList<>();

    CrossJoin(CrossJoinChildNode left, CrossJoinChildNode right) {
        this.left = left;
        this.right = right;
        checkType();
    }

    public CrossJoinChildNode getLeft() {
        return left;
    }

    public CrossJoinChildNode getRight() {
        return right;
    }

    public String getType() {
        return type;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    private void checkType() {
        if(left.getType().equals(TABLE_TYPE) && right.getType().equals(TABLE_TYPE)) {
            type = CROSS_JOIN_TYPE_TWO_TABLES;
            tableNames.add(left.getName());
            tableNames.add(right.getName());
        } else if (left.getType().equals(TABLE_TYPE)) {
            tableNames.add(left.getName());
            if(right.getType().equals(SUBQUERY_TYPE)) {
                type = CROSS_JOIN_TYPE_TABLE_SUBQUERY;
            } else if (right.getType().equals(JOIN_SCAN_TYPE)) {
                type = CROSS_JOIN_TYPE_TABLE_JOIN;
            }
        } else if (right.getType().equals(TABLE_TYPE)) {
            tableNames.add(right.getName());
            if (left.getType().equals(SUBQUERY_TYPE)) {
                type = CROSS_JOIN_TYPE_TABLE_SUBQUERY;
            } else if (left.getType().equals(JOIN_SCAN_TYPE)) {
                type = CROSS_JOIN_TYPE_TABLE_JOIN;
            }
        } else if ((left.getType().equals(SUBQUERY_TYPE) && right.getType().equals(JOIN_SCAN_TYPE) || (left.getType().equals(JOIN_SCAN_TYPE) && right.getType().equals(SUBQUERY_TYPE)))) {
            type = CROSS_JOIN_TYPE_SUBQUERY_JOIN;
        } else if(left.getType().equals(SUBQUERY_TYPE) && right.getType().equals(SUBQUERY_TYPE)) {
            type = CROSS_JOIN_TYPE_SUBQUERY_SUBQUERY;
        }
        tableNames.add(left.getName());
        tableNames.add(right.getName());
    }


}
