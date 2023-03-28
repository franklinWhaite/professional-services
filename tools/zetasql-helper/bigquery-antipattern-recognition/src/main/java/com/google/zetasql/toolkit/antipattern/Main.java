package com.google.zetasql.toolkit.antipattern;

import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import com.google.zetasql.AnalyzerOptions;
import com.google.zetasql.LanguageOptions;
import com.google.zetasql.toolkit.ZetaSQLToolkitAnalyzer;
import com.google.zetasql.toolkit.catalog.bigquery.BigQueryCatalog;
import com.google.zetasql.toolkit.options.BigQueryLanguageOptions;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main {
  public static void main(String[] args) {
    // setup analyzer
    AnalyzerOptions options = new AnalyzerOptions();
    LanguageOptions languageOptions = BigQueryLanguageOptions.get().enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();
    options.setLanguageOptions(languageOptions);
    options.setCreateNewColumnForEachProjectedOutput(true);
    ZetaSQLToolkitAnalyzer analyzer = new ZetaSQLToolkitAnalyzer(options);

    // create BQ catalog
    BigQueryCatalog catalog = new BigQueryCatalog("bigquery-public-data");
    String query = "SELECT * FROM `bigquery-public-data.samples.wikipedia` WHERE title = 'random title';";
    catalog.addAllTablesUsedInQuery(query, options);

    // check for patterns
    System.out.println((new IdentidySelectedColumns()).run(query, catalog, analyzer));
    // System.out.println((new IdentifyCrossJoin()).run(query, catalog, analyzer));
    //
    // BigQuery bigquery = BigQueryOptions.newBuilder().setProjectId("pso-dev-whaite").build().getService();
    // QueryJobConfiguration queryConfig =
    //     QueryJobConfiguration.newBuilder(
    //             "SELECT\n"
    //                 + "  CONCAT(project_id, \":UD.\",  job_id) job_id, \n"
    //                 + "  query, \n"
    //                 + "  total_slot_ms / (1000 * 60 * 60 ) AS slot_hours\n"
    //                 + "FROM\n"
    //                 + "  `region-us`.INFORMATION_SCHEMA.JOBS\n"
    //                 + "WHERE \n"
    //                 + "  start_time >= CURRENT_TIMESTAMP - INTERVAL 30 DAY\n"
    //                 + "ORDER BY\n"
    //                 + "  start_time desc\n"
    //                 + "LIMIT 1")
    //         // Use standard SQL syntax for queries.
    //         // See: https://cloud.google.com/bigquery/sql-reference/
    //         .setUseLegacySql(false)
    //         .build();
    //
    // JobId jobId = JobId.of(UUID.randomUUID().toString());
    // Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());
    //
    // try {
    //   TableResult tableResult = queryJob.getQueryResults();
    //
    //   for (FieldValueList row : tableResult.iterateAll()) {
    //     String job_id = row.get("job_id").getStringValue();
    //     String query = row.get("query").getStringValue();
    //     String slot_hours = row.get("slot_hours").getStringValue();
    //
    //     catalog.addAllTablesUsedInQuery(query, options);
    //     String rec = (new IdentidySelectedColumns()).run(query, catalog, analyzer);
    //     System.out.println(rec);
    //     TableId tableId = TableId.of("pso-dev-whaite", "dataset", "antipattern_output_table");
    //
    //     // Create a map of rows to insert
    //     Map<String, Object> rowContent = new HashMap<>();
    //     rowContent.put("job_id", job_id);
    //     rowContent.put("query", query);
    //     rowContent.put("slot_hours", Float.parseFloat(slot_hours));
    //     rowContent.put("recommendation", rec);
    //
    //     // Create a list of row content to insert
    //     bigquery.insertAll(InsertAllRequest.newBuilder(tableId).addRow(rowContent).build());
    //
    //
    //   }
    // } catch (InterruptedException e) {
    //   throw new RuntimeException(e);
    // }

  }
}
