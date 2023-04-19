package com.google.zetasql.toolkit.antipattern.util;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import java.util.Map;

public class BigQueryHelper {

  public static TableResult getQueries(String projectId, String daysBack, String ISTable) throws InterruptedException {
    BigQuery bigquery = BigQueryOptions.newBuilder().setProjectId(projectId).build().getService();
    QueryJobConfiguration queryConfig =
        QueryJobConfiguration.newBuilder(
                "SELECT\n"
                    + "  project_id,\n"
                    + "  CONCAT(project_id, \":US.\",  job_id) job_id, \n"
                    + "  query, \n"
                    + "  total_slot_ms / (1000 * 60 * 60 ) AS slot_hours\n"
                    + "FROM\n"
                    + ISTable + "\n"
                    + "WHERE \n"
                    + "  start_time >= CURRENT_TIMESTAMP - INTERVAL "+ daysBack + " DAY\n"
                    + "  AND total_slot_ms > 0\n"
                    + "  AND (statement_type != \"SCRIPT\" OR statement_type IS NULL)\n"
                    + "  AND (reservation_id != 'default-pipeline' or reservation_id IS NULL)\n"
                    + "ORDER BY\n"
                    + "  project_id, start_time desc\n")
            .setUseLegacySql(false)
            .build();

    Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).build());
    return queryJob.getQueryResults();
  }

  public static void writeResults(String processingProject, String outputTable, Map<String, Object> rowContent) {
    String[] tableName = outputTable.split("\\.");
    TableId tableId = TableId.of(tableName[0], tableName[1], tableName[2]);
    BigQuery bigquery = BigQueryOptions.newBuilder().setProjectId(processingProject).build().getService();
    bigquery.insertAll(InsertAllRequest.newBuilder(tableId).addRow(rowContent).build());
  }
}
