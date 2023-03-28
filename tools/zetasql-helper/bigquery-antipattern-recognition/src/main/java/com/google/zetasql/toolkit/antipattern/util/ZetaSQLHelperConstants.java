package com.google.zetasql.toolkit.antipattern.util;

public class ZetaSQLHelperConstants {
  public static String INFO_SCHEMA_SCHEMATA_QUERY =
      "SELECT \n" + "  schema_name\n" + "FROM\n" + "  `%s`.INFORMATION_SCHEMA.SCHEMATA\n";

  public static String INFO_SCHEMA_COLUMNS_QUERY =
      "SELECT \n"
          + "  table_name, column_name, data_type\n"
          + "FROM \n"
          + "  %s.%s.INFORMATION_SCHEMA.COLUMNS\n"
          + "ORDER BY\n"
          + "  table_schema, table_name";

  public static String TABLE_NAME = "project.dataset.table1";
  public static String TABLE_1_NAME = "project.dataset.table1";
  public static String TABLE_2_NAME = "project.dataset.table2";
  public static String TABLE_3_NAME = "project.dataset.table3";
  public static String COL_1 = "col1";
  public static String COL_2 = "col2";
  public static String CATALOG_NAME = "catalog";
  public static String MY_PROJET = "my-project";
}
