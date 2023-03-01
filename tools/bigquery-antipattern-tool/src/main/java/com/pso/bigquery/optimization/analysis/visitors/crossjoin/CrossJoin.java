package com.pso.bigquery.optimization.analysis.visitors.crossjoin;

import java.util.ArrayList;
import java.util.List;

public class CrossJoin {

  private CrossJoinChildNode left;
  private CrossJoinChildNode right;
  private List<String> tableNames = new ArrayList<>();

  CrossJoin(CrossJoinChildNode left, CrossJoinChildNode right) {
    this.left = left;
    this.right = right;
  }

  public CrossJoinChildNode getLeft() {
    return left;
  }

  public CrossJoinChildNode getRight() {
    return right;
  }

  public List<String> getTableNames() {
    return tableNames;
  }

  public void addTableName(String tableName) {
    tableNames.add(tableName);
  }
}
