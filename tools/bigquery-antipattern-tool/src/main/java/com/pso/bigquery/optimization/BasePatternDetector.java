package com.pso.bigquery.optimization;

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.catalog.BigQueryTableService;
import com.pso.bigquery.optimization.catalog.CatalogUtils;

public interface BasePatternDetector {
    BigQueryTableService bigQueryTableService = BigQueryTableService.buildDefault();
    QueryAnalyzer parser = new QueryAnalyzer(bigQueryTableService);

    String run(String query, String billingProjectId, SimpleCatalog catalog, QueryAnalyzer.CatalogScope catalogScope);


}
