package com.google.zetasql.toolkit.antipattern.parser.visitors.crossjoin;

import java.util.ArrayList;
import java.util.List;

public class CrossJoin {

  private CrossJoinSide left;
  private CrossJoinSide right;
  private List<String> tableNames = new ArrayList<>();

  CrossJoin(CrossJoinSide left, CrossJoinSide right) {
    this.left = left;
    this.right = right;
  }

  public List<String> getTableNames() {
    return tableNames;
  }

  public CrossJoinSide getLeft() {
    return left;
  }

  public CrossJoinSide getRight() {
    return right;
  }
}
