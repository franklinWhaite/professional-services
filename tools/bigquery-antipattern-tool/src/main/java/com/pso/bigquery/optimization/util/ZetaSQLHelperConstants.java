package com.pso.bigquery.optimization.util;

public interface ZetaSQLHelperConstants {

    String INFO_SCHEMA_SCHEMATA_QUERY = "SELECT \n" +
            "  schema_name\n" +
            "FROM\n" +
            "  `%s`.INFORMATION_SCHEMA.SCHEMATA\n";

    String INFO_SCHEMA_COLUMNS_QUERY = "SELECT \n" +
            "  table_name, column_name, data_type\n" +
            "FROM \n" +
            "  %s.%s.INFORMATION_SCHEMA.COLUMNS\n" +
            "ORDER BY\n" +
            "  table_schema, table_name";

    String TABLE_NAME = "project.dataset.table1";
    String TABLE_1_NAME = "project.dataset.table1";
    String TABLE_2_NAME = "project.dataset.table2";
    String TABLE_3_NAME = "project.dataset.table3";
    String COL_1 = "col1";
    String COL_2 = "col2";
    String CATALOG_NAME = "catalog";
    String MY_PROJET = "my-project";

}
