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

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.ExtractScansVisitor;
import com.pso.bigquery.optimization.catalog.CatalogUtils;
import io.vavr.control.Try;
import java.util.List;

public class BuildCatalogFromInformationSchema {
  public static void main(String[] args) {
    String PROJECT = "my-project";
    String QUERY =
        "SELECT \n"
            + "  t1.unique_key \n"
            + "FROM \n"
            + "  `my-project.DATASET_1.test_table_1` t1\n"
            + "LEFT JOIN\n"
            + "  `my-project.DATASET_1.test_table_2` t2 ON t1.unique_key=t2.unique_key\n"
            + "WHERE\n"
            + " t1.unique_key is not null\n"
            + " AND t2.unique_key is not null\n";

    SimpleCatalog catalog = CatalogUtils.createCatalogForInfoSchema(PROJECT);

    QueryAnalyzer parser = new QueryAnalyzer();
    Try<List<ExtractScansVisitor.QueryScan>> tryScans =
        parser.getScansInQuery(PROJECT, QUERY, catalog, QueryAnalyzer.CatalogScope.PROJECT);
    List<ExtractScansVisitor.QueryScan> scanResults = tryScans.get();

    scanResults.stream()
        .map(ExtractScansVisitor.QueryScan::toMap)
        .forEach(scanResult -> System.out.println(scanResult));
  }
}
