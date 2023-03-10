package com.pso.bigquery.optimization.analysis.visitors.subqueryinwhere;

import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedNodes.ResolvedColumnRef;
import com.pso.bigquery.optimization.analysis.visitors.BaseAnalyzerVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnsOfInExprVisitor extends BaseAnalyzerVisitor {

  private List<String> columnList = new ArrayList<>();

  public ColumnsOfInExprVisitor(String projectId, SimpleCatalog catalog) {
    super(projectId, catalog);
  }

  public void visit(ResolvedColumnRef resolvedColumnRef){
    columnList.add(resolvedColumnRef.getColumn().toString().split("#")[0]);
  }

  public String getResult() {
    return columnList.stream().map(Object::toString)
        .collect(Collectors.joining(", "));
  }
}
