/*
 * Copyright 2022 Google LLC All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pso.bigquery.optimization.analysis.visitors;

import com.google.common.collect.ImmutableList;
import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.resolvedast.ResolvedColumn;
import com.google.zetasql.resolvedast.ResolvedNodes.*;
import com.pso.bigquery.optimization.util.ZetaSQLStringParsingHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

// Visitor instance that, given the AST, extract the scans the
// query performs on tables. Scans contain the table/view read,
// the columns used in WHERE conditions and the options used in
// JOIN conditions.
public class SelectedColumnsVisitor extends BaseAnalyzerVisitor {

  private final Map<String, TableWithSelectedCol> tablesWithSelectedColsMap= new HashMap<String, TableWithSelectedCol>();

  public SelectedColumnsVisitor(String projectId, SimpleCatalog catalog) {
    super(projectId, catalog);
  }

  public List<TableWithSelectedCol> getResult() {
    return new ArrayList<TableWithSelectedCol>(this.tablesWithSelectedColsMap.values());
  }

  public void visit(ResolvedComputedColumn computedCol) {
    addColumnToMap(computedCol);
    super.visit(computedCol);
  }

  private void addColumnToMap(ResolvedComputedColumn computedCol){
    String tableName = ZetaSQLStringParsingHelper.getTableNameFromExpr(computedCol.toString());
    if(tableName != null) {
      String colName = computedCol.getColumn().getName();
      if(tablesWithSelectedColsMap.containsKey(tableName)){
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
