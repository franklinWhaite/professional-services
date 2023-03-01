/*
 * Copyright 2023 Google LLC All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zetasql;

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.*;
import static org.junit.Assert.*;

import com.pso.bigquery.optimization.IdentidySelectedColumns;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class IdentidySelectedColumnsTest {

  String billing_project = MY_PROJET;
  SimpleCatalog catalog = new SimpleCatalog(CATALOG_NAME);

  @Before
  public void setUp() {
    catalog.addZetaSQLFunctions(new ZetaSQLBuiltinFunctionOptions());
    SimpleColumn column1 =
        new SimpleColumn(
            TABLE_NAME, COL_1, TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING));
    SimpleColumn column2 =
        new SimpleColumn(
            TABLE_NAME, COL_2, TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING));
    SimpleTable table1 = new SimpleTable(TABLE_1_NAME, List.of(column1, column2));
    SimpleTable table2 = new SimpleTable(TABLE_2_NAME, List.of(column1, column2));
    SimpleTable table3 = new SimpleTable(TABLE_3_NAME, List.of(column1, column2));
    catalog.addSimpleTable(table1);
    catalog.addSimpleTable(table2);
    catalog.addSimpleTable(table3);
  }

  @Test
  public void selectStarSingleTableTest() {
    String query = "SELECT * FROM `project.dataset.table1`";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(
        "All columns on table: project.dataset.table1 are being selected. Please be sure that all"
            + " columns are needed",
        recommendations);
  }

  @Test
  public void selectStarTwoTablesTest() {
    String expected =
        "All columns on table: project.dataset.table1 are being selected. Please be sure that all columns are needed\n"
            + "All columns on table: project.dataset.table2 are being selected. Please be sure that all columns are needed";
    String query = "SELECT " +
        "   t1.*, t2.* " +
        "FROM " +
        "   `project.dataset.table1` t1 " +
        "cross JOIN " +
        "    `project.dataset.table2` t2 " +
        "WHERE " +
        "   t1.col1 = t2.col1 ";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(expected, recommendations);
  }

  @Test
  public void noRecommendationTest() {
    String query = "SELECT col1 FROM `project.dataset.table1`";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals("", recommendations);
  }

  @Test
  public void selectEachColSingleTableTest() {
    String query = "SELECT col1, col2 FROM `project.dataset.table1`";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(
        "All columns on table: project.dataset.table1 are being selected. Please be sure that all"
            + " columns are needed",
        recommendations);
  }

  @Test
  public void selectStarWithJoinTest() {
    String query =
        "\n"
            + "SELECT "
            + "   t1.*, "
            + "   t2.col2 "
            + "FROM \n"
            + "  `project.dataset.table1` t1\n"
            + "LEFT JOIN\n"
            + "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(
        "All columns on table: project.dataset.table1 are being selected. Please be sure that all"
            + " columns are needed",
        recommendations);
  }

  @Test
  public void selectStarBothTablesWithJoinTest() {
    String query =
        "\n"
            + "SELECT "
            + "   t1.*, "
            + "   t2.col1, t2.col2, "
            + "FROM \n"
            + "  `project.dataset.table1` t1\n"
            + "LEFT JOIN\n"
            + "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(
        "All columns on table: project.dataset.table1 are being selected. Please be sure that all"
            + " columns are needed\n"
            + "All columns on table: project.dataset.table2 are being selected. Please be sure that"
            + " all columns are needed",
        recommendations);
  }

  @Test
  public void selectStarWithJoinAndSubSelectTest() {
    // failing
    String query =
        "\n"
            + "SELECT "
            + "   t1.*, "
            + "   t2.col2 "
            + "FROM \n"
            + "  (SELECT * FROM `project.dataset.table1`) t1\n"
            + "LEFT JOIN\n"
            + "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";
    String recommendations =
        new IdentidySelectedColumns()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(
        "All columns on table: project.dataset.table1 are being selected. Please be sure that all"
            + " columns are needed",
        recommendations);
  }
}
