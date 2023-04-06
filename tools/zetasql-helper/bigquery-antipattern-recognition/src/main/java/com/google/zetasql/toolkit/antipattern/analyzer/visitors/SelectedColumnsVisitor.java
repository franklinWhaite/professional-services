package com.google.zetasql.toolkit.antipattern.analyzer.visitors;

import com.google.zetasql.resolvedast.ResolvedNodes;
import com.google.zetasql.resolvedast.ResolvedNodes.*;
import com.google.zetasql.toolkit.antipattern.util.ZetaSQLStringParsingHelper;
import java.util.*;

public class SelectedColumnsVisitor extends ResolvedNodes.Visitor {

  private final Map<String, TableWithSelectedCol> tablesWithSelectedColsMap =
      new HashMap<String, TableWithSelectedCol>();

  public List<TableWithSelectedCol> getResult() {
    return new ArrayList<TableWithSelectedCol>(this.tablesWithSelectedColsMap.values());
  }

  public void visit(ResolvedComputedColumn computedCol) {
    addColumnToMap(computedCol);
    super.visit(computedCol);
  }

  private void addColumnToMap(ResolvedComputedColumn computedCol) {
    String tableName = ZetaSQLStringParsingHelper.getTableNameFromExpr(computedCol.toString());
    if (tableName != null) {
      String colName = computedCol.getColumn().getName();
      if (tablesWithSelectedColsMap.containsKey(tableName)) {
        tablesWithSelectedColsMap.get(tableName).addSelectedColumn(colName);
      } else {
        TableWithSelectedCol tableWithSelectedCol = new TableWithSelectedCol(tableName);
        tableWithSelectedCol.addSelectedColumn(colName);
        tablesWithSelectedColsMap.put(tableName, tableWithSelectedCol);
      }
    }
  }

  public static class TableWithSelectedCol {

    private final String table;
    private final Set<String> selectedColumns = new HashSet<>();

    public TableWithSelectedCol(String table) {
      this.table = table;
    }

    public String getTable() {
      return table;
    }

    public Set<String> getSelectedColumns() {
      return selectedColumns;
    }

    public void addSelectedColumn(String column) {
      this.selectedColumns.add(column);
    }

    public Map<String, Object> toMap() {
      return Map.of(
          "table", this.table,
          "selectedColumns", this.selectedColumns);
    }
  }
}
