/*
 * Copyright 2023 Google LLC All Rights Reserved
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
package com.pso.bigquery.optimization;

import com.google.zetasql.NotFoundException;
import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.Table;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.SelectedColumnsVisitor;
import com.pso.bigquery.optimization.exceptions.TableNotFoundInCatalog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class IdentidySelectedColumns implements BasePatternDetector {

  private static final String SELECTED_COLUMNS_MESSAGE =
      "All columns on table: %s are being selected. Please be sure that all columns are needed";

  public String run(
      String query,
      String billingProjectId,
      SimpleCatalog catalog,
      QueryAnalyzer.CatalogScope catalogScope) {
    SelectedColumnsVisitor visitor = new SelectedColumnsVisitor(billingProjectId, catalog);

    parser.visitQuery(billingProjectId, query, catalog, catalogScope, visitor);

    List<SelectedColumnsVisitor.TableWithSelectedCol> visitorResults = visitor.getResult();
    ArrayList<String> result = new ArrayList<String>();

    visitorResults.stream()
        .forEach(
            tableWithSelectedCol -> {
              try {
                checkTable(tableWithSelectedCol, catalog, result);
              } catch (NotFoundException e) {
                throw new TableNotFoundInCatalog(e.getMessage());
              }
            });

    return StringUtils.join(result, "\n");
  }

  static void checkTable(
      SelectedColumnsVisitor.TableWithSelectedCol tableWithSelectedCol,
      SimpleCatalog catalog,
      ArrayList<String> result)
      throws NotFoundException {
    String tableName = tableWithSelectedCol.getTable();
    Table tableInCatalog = catalog.findTable(Arrays.asList(tableName));

    if (tableInCatalog.getColumnCount() == tableWithSelectedCol.getSelectedColumns().size()) {
      result.add(String.format(SELECTED_COLUMNS_MESSAGE, tableName));
    }
  }
}
