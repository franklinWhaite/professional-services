package com.google.zetasql.toolkit.antipattern.exceptions;

public class TableNotFoundInCatalog extends RuntimeException {

  public TableNotFoundInCatalog(String message) {
    super(message);
  }
}
