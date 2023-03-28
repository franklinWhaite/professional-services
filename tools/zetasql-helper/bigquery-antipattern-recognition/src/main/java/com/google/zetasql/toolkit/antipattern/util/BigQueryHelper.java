package com.google.zetasql.toolkit.antipattern.util;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import java.util.Map;
import java.util.UUID;

public class BigQueryHelper {

  public static TableResult getQueries(String project_id) throws InterruptedException {
    BigQuery bigquery = BigQueryOptions.newBuilder().setProjectId(project_id).build().getService();
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(
                "SELECT\n"
                    + "  project_id,\n"
                    + "  CONCAT(project_id, \":UD.\",  job_id) job_id, \n"
                    + "  query, \n"
                    + "  total_slot_ms / (1000 * 60 * 60 ) AS slot_hours\n"
                    + "FROM\n"
                    + "  `region-us`.INFORMATION_SCHEMA.JOBS\n"
                    + "WHERE \n"
                    + "  start_time >= CURRENT_TIMESTAMP - INTERVAL 30 DAY\n"
                    + "  AND total_slot_ms > 0\n"
                    + "  AND (statement_type != \"SCRIPT\" OR statement_type IS NULL)\n"
                    + "  AND (reservation_id != 'default-pipeline' or reservation_id IS NULL)\n"
                    + "ORDER BY\n"
                    + "  project_id, start_time desc\n"
                    + "LIMIT 1")
            // Use standard SQL syntax for queries.
            // See: https://cloud.google.com/bigquery/sql-reference/
            .setUseLegacySql(false)
            .build();

    Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).build());
    return queryJob.getQueryResults();
  }

  public static void writeResults(String project_id, Map<String, Object> rowContent) {
    TableId tableId = TableId.of(project_id, "dataset", "antipattern_output_table");
    BigQuery bigquery = BigQueryOptions.newBuilder().setProjectId(project_id).build().getService();
    bigquery.insertAll(InsertAllRequest.newBuilder(tableId).addRow(rowContent).build());
  }
}
