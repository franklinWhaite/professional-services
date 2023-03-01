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
