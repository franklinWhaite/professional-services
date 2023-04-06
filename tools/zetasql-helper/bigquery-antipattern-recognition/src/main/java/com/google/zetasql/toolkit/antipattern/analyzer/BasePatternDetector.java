package com.google.zetasql.toolkit.antipattern.analyzer;

import com.google.zetasql.toolkit.ZetaSQLToolkitAnalyzer;
import com.google.zetasql.toolkit.catalog.bigquery.BigQueryCatalog;

public interface BasePatternDetector {

   public String run(
      String query,
      BigQueryCatalog catalog,
      ZetaSQLToolkitAnalyzer analyzer);
}
