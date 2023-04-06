package com.google.zetasql.toolkit.antipattern.analyzer.visitors.subqueryinwhere;

import com.google.zetasql.resolvedast.ResolvedNodes;
import com.google.zetasql.resolvedast.ResolvedNodes.ResolvedColumnRef;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnsOfInExprVisitor extends ResolvedNodes.Visitor {
  private List<String> columnList = new ArrayList<>();

  public void visit(ResolvedColumnRef resolvedColumnRef) {
    columnList.add(resolvedColumnRef.getColumn().toString().split("#")[0]);
  }

  public String getResult() {
    return columnList.stream().map(Object::toString).collect(Collectors.joining(", "));
  }

}
