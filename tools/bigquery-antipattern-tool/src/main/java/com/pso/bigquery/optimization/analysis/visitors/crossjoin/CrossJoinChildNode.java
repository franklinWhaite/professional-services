package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import com.google.zetasql.resolvedast.ResolvedNodes.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrossJoinChildNode {

  private List<String> tableNameList = new ArrayList<>();

  CrossJoinChildNode(ResolvedScan resolvedScan) {
    setup(resolvedScan);
  }

  private void setup(ResolvedScan resolvedScan) {
    if (resolvedScan instanceof ResolvedTableScan) {
      tableNameList.add(((ResolvedTableScan) resolvedScan).getTable().getFullName());
    } else if (resolvedScan instanceof ResolvedJoinScan) {
      ResolvedJoinScan resolvedJoinScan = (ResolvedJoinScan) resolvedScan;
      tableNameList =
          resolvedJoinScan.getColumnList().stream()
              .map(resolvedColumn -> resolvedColumn.getTableName())
              .collect(Collectors.toList());
    } else if (resolvedScan instanceof ResolvedProjectScan) {
      ResolvedProjectScan resolvedProjectScan = (ResolvedProjectScan) resolvedScan;
      tableNameList.add(resolvedProjectScan.getColumnList().get(0).getTableName());
    }
  }

  public List<String> getTableNameList() {
    return tableNameList;
  }
}
