package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import com.google.zetasql.resolvedast.ResolvedNodes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrossJoinChildNode {

    public static final String TABLE_TYPE = "TABLE";
    public static final String JOIN_SCAN_TYPE = "JOIN_SCAN";
    public static final String SUBQUERY_TYPE = "SUBQUERY";
    private String type;
    private String name = null;
    private List<String> tableNameList = new ArrayList<>();

    CrossJoinChildNode(ResolvedNodes.ResolvedScan resolvedScan) {
        setup(resolvedScan);
    }

    private void setup(ResolvedNodes.ResolvedScan resolvedScan) {
        if(resolvedScan instanceof ResolvedNodes.ResolvedTableScan) {
            type = TABLE_TYPE;
            name = ((ResolvedNodes.ResolvedTableScan) resolvedScan).getTable().getFullName();
            tableNameList.add(name);
        } else if (resolvedScan instanceof ResolvedNodes.ResolvedJoinScan) {
            type = JOIN_SCAN_TYPE;
            name = "'OTHER JOIN'";
            ResolvedNodes.ResolvedJoinScan resolvedJoinScan = (ResolvedNodes.ResolvedJoinScan) resolvedScan;
            System.out.println("====a===");
            System.out.println(resolvedJoinScan.getColumnList());
            tableNameList = resolvedJoinScan.getColumnList().stream().map(resolvedColumn -> resolvedColumn.getTableName()).collect(Collectors.toList());
            System.out.println("====b===");
        } else if (resolvedScan instanceof ResolvedNodes.ResolvedProjectScan) {
            type = SUBQUERY_TYPE;
            ResolvedNodes.ResolvedProjectScan resolvedProjectScan = (ResolvedNodes.ResolvedProjectScan) resolvedScan;
            name = resolvedProjectScan.getColumnList().get(0).getTableName();
            tableNameList.add(name);
        }
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getTableNameList() {
        return tableNameList;
    }
}
