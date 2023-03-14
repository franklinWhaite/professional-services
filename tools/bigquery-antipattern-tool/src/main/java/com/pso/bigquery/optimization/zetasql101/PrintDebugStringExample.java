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
package com.pso.bigquery.optimization.zetasql101;

import com.google.zetasql.*;
import com.google.zetasql.resolvedast.ResolvedNodes;
import java.util.List;

public class PrintDebugStringExample {

  private static AnalyzerOptions getAnalyzerOptions() {
    LanguageOptions languageOptions = new LanguageOptions().enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    AnalyzerOptions analyzerOptions = new AnalyzerOptions();
    analyzerOptions.setLanguageOptions(languageOptions);
    analyzerOptions.setCreateNewColumnForEachProjectedOutput(true);
    return analyzerOptions;
  }

  public static void main(String[] args) {
    SimpleCatalog catalog = new SimpleCatalog("catalog");
    catalog.addZetaSQLFunctions(new ZetaSQLBuiltinFunctionOptions());
    SimpleColumn column1 =
        new SimpleColumn(
            "project.dataset.table",
            "col1",
            TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING));
    SimpleColumn column2 =
        new SimpleColumn(
            "project.dataset.table",
            "col2",
            TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING));
    SimpleTable table1 = new SimpleTable("project.dataset.table1", List.of(column1, column2));
    SimpleTable table2 = new SimpleTable("project.dataset.table2", List.of(column1, column2));
    SimpleTable table3 = new SimpleTable("project.dataset.table3", List.of(column1, column2));
    catalog.addSimpleTable(table1);
    catalog.addSimpleTable(table2);
    catalog.addSimpleTable(table3);

    AnalyzerOptions analyzerOptions = getAnalyzerOptions();

    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "   t1.col2 not in (select col2 from `project.dataset.table1`) "
            + "   AND t1.col2 not in (select distinct col2 from `project.dataset.table1`) ";
    // "ON t1.col2 = t2.col2 " +
    ;

    ResolvedNodes.ResolvedStatement resolvedStatement =
        Analyzer.analyzeStatement(query, analyzerOptions, catalog);

    System.out.println(resolvedStatement.debugString());
  }
}
