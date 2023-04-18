package com.google.zetasql.toolkit.antipattern.cmd;

public class InputQuery {

  private String query;
  private String queryIdentifier;
  private float slotHours;

  public InputQuery(String query, String queryIdentifier) {
    this.query = query;
    this.queryIdentifier = queryIdentifier;
  }

  public InputQuery(String query, String jobId, float slotHours) {
    this.query = query;
    this.queryIdentifier = jobId;
    this.slotHours= slotHours;
  }

  public String getQuery() {
    return query;
  }

  public String getQueryId() {
    return queryIdentifier;
  }

  public float getSlotHours() {
    return slotHours;
  }
}