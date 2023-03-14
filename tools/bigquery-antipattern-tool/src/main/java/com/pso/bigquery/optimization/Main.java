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

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.*;

import com.google.zetasql.*;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer.CatalogScope;
import com.pso.bigquery.optimization.antipatterns.cmd.BQAntiPatternCMDParser;
import com.pso.bigquery.optimization.antipatterns.cmd.InputQuery;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.cli.*;

public class Main {

  public static void main(String[] args) throws ParseException {
    String billing_project = MY_PROJET;
    InputQuery inputQuery;
    SimpleCatalog catalog = new SimpleCatalog(CATALOG_NAME);

    catalog.addZetaSQLFunctions(new ZetaSQLBuiltinFunctionOptions());
    SimpleColumn column1 =
        new SimpleColumn(
            TABLE_NAME, COL_1, TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING));
    SimpleColumn column2 =
        new SimpleColumn(
            TABLE_NAME, COL_2, TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING));
    SimpleTable table1 = new SimpleTable(TABLE_1_NAME, List.of(column1, column2));
    SimpleTable table2 = new SimpleTable(TABLE_2_NAME, List.of(column1, column2));
    SimpleTable table3 = new SimpleTable(TABLE_3_NAME, List.of(column1, column2));
    catalog.addSimpleTable(table1);
    catalog.addSimpleTable(table2);
    catalog.addSimpleTable(table3);

    Iterator<InputQuery> inputQueries = BQAntiPatternCMDParser.getInputQueries(args);
    while (inputQueries.hasNext()) {
      inputQuery = inputQueries.next();

      System.out.println(inputQuery.getPath());
      System.out.println(
          new IdentidySelectedColumns()
              .run(
                  inputQuery.getQuery(),
                  billing_project,
                  catalog,
                  QueryAnalyzer.CatalogScope.MANUAL));
      System.out.println(
          new IdentifyCrossJoin()
              .run(
                  inputQuery.getQuery(),
                  billing_project,
                  catalog,
                  QueryAnalyzer.CatalogScope.MANUAL));
      System.out.println(
          new IdentifySubqueryInWhere()
              .run(inputQuery.getQuery(), billing_project, catalog, CatalogScope.MANUAL));
    }
  }
}
