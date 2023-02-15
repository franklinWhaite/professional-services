package com.pso.bigquery.optimization.exceptions;

public class TableNotFoundInCatalog extends RuntimeException{

    public TableNotFoundInCatalog(String message) {
        super(message);
    }
}
