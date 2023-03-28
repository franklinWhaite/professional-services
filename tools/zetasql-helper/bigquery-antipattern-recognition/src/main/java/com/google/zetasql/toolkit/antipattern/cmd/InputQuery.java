package com.google.zetasql.toolkit.antipattern.cmd;

public class InputQuery {

  private String query;
  private String path;
  private String jobId;
  private float slotHours;

  public InputQuery(String query, String inputFilePath) {
    this.query = query;
    this.path = inputFilePath;
  }

  public InputQuery(String query, String jobId, float slotHours) {
    this.query = query;
    this.jobId = jobId;
    this.slotHours= slotHours;
  }

  public String getQuery() {
    return query;
  }

  public String getPath() {
    return path;
  }

  public String getJobId() {
    return jobId;
  }

  public float getSlotHours() {
    return slotHours;
  }
}