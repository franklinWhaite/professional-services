package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import java.util.ArrayList;
import java.util.List;

public class CrossJoin {

  private CrossJoinSide left;
  private CrossJoinSide right;
  private List<String> namesTablesUsedOnFilter = new ArrayList<>();


  CrossJoin(CrossJoinSide left, CrossJoinSide right) {
    this.left = left;
    this.right = right;
  }

  public List<String> getNamesTablesUsedOnFilter() {
    return namesTablesUsedOnFilter;
  }

  public CrossJoinSide getLeft() {
    return left;
  }

  public CrossJoinSide getRight() {
    return right;
  }

  public void addTableName(String tableName) {
    namesTablesUsedOnFilter.add(tableName);
  }
}
