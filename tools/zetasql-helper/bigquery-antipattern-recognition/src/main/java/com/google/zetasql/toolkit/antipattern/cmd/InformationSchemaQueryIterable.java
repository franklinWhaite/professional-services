package com.google.zetasql.toolkit.antipattern.cmd;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import com.google.zetasql.toolkit.antipattern.util.BigQueryHelper;
import java.util.Iterator;

public class InformationSchemaQueryIterable implements Iterator<InputQuery> {

  Iterator<FieldValueList> fieldValueListIterator;
  String IS_TABLE_DEFAULT = "`region-us`.INFORMATION_SCHEMA.JOBS";
  String DAYS_BACK_DEFAULT = "30";

  public InformationSchemaQueryIterable(String projectId) throws InterruptedException {
    TableResult tableResult = BigQueryHelper.getQueries(projectId, DAYS_BACK_DEFAULT, IS_TABLE_DEFAULT);
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  public InformationSchemaQueryIterable(String projectId, String daysBack) throws InterruptedException {
    TableResult tableResult = BigQueryHelper.getQueries(projectId, daysBack, IS_TABLE_DEFAULT);
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  public InformationSchemaQueryIterable(String projectId, String daysBack, String ISTable) throws InterruptedException {
    TableResult tableResult = BigQueryHelper.getQueries(projectId, daysBack, ISTable);
    fieldValueListIterator = tableResult.iterateAll().iterator();
  }

  @Override
  public boolean hasNext() {
    return fieldValueListIterator.hasNext();
  }

  @Override
  public InputQuery next() {
    FieldValueList row = fieldValueListIterator.next();
    String job_id = row.get("job_id").getStringValue();
    String query = row.get("query").getStringValue();
    String slot_hours = row.get("slot_hours").getStringValue();
    return new InputQuery(query, job_id, Float.parseFloat(slot_hours));
  }
}
