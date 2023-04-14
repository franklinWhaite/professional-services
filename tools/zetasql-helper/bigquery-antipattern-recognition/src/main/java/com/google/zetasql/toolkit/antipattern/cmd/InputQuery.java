package com.google.zetasql.toolkit.antipattern.cmd;

public class InputQuery {

  private String query;
  private String queryIdentifier;
  private String jobId;
  private float slotHours;

  public InputQuery(String query, String queryIdentifier) {
    this.query = query;
    this.queryIdentifier = queryIdentifier;
  }

  public InputQuery(String query, String jobId, float slotHours) {
    this.query = query;
    this.jobId = jobId;
    this.slotHours= slotHours;
  }

  public String getQuery() {
    return query;
  }

  public String getQueryId() {
    return queryIdentifier;
  }

  public String getJobId() {
    return jobId;
  }

  public float getSlotHours() {
    return slotHours;
  }
}