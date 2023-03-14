package com.pso.bigquery.optimization.antipatterns.cmd;

public class InputQuery {

  private String query;
  private String path;

  public InputQuery(String query, String path){
    this.query = query;
    this.path = path;
  }

  public String getQuery() {
    return query;
  }

  public String getPath() {
    return path;
  }
}
