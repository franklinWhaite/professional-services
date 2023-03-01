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

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.catalog.BigQueryTableService;

public interface BasePatternDetector {
  BigQueryTableService bigQueryTableService = BigQueryTableService.buildDefault();
  QueryAnalyzer parser = new QueryAnalyzer(bigQueryTableService);

  String run(
      String query,
      String billingProjectId,
      SimpleCatalog catalog,
      QueryAnalyzer.CatalogScope catalogScope);
}
