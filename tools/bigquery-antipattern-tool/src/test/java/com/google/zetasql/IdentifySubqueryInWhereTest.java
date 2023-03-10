package com.google.zetasql;

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.CATALOG_NAME;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.COL_1;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.COL_2;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.MY_PROJET;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.TABLE_1_NAME;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.TABLE_2_NAME;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.TABLE_3_NAME;
import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.TABLE_NAME;
import static org.junit.Assert.assertEquals;

import com.pso.bigquery.optimization.IdentifyCrossJoin;
import com.pso.bigquery.optimization.IdentifySubqueryInWhere;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class IdentifySubqueryInWhereTest {

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
  public void IdentifySubqueryInWhereTest() {
    String expected =
        "You are using an IN filter with a subquery without a DISTINCT on the following columns: project.dataset.table1.col2";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    t1.col2 not in (select col2 from `project.dataset.table2`) ";
    String recommendation =
        new IdentifySubqueryInWhere()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(expected, recommendation);
  }

  @Test
  public void IdentifySubqueryInWhere2CasesTest() {
    String expected =
        "You are using an IN filter with a subquery without a DISTINCT on the following columns: project.dataset.table1.col1\n"
            + "You are using an IN filter with a subquery without a DISTINCT on the following columns: project.dataset.table1.col2";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    t1.col1 not in (select col1 from `project.dataset.table2`) "
            + "    AND t1.col2 not in (select col2 from `project.dataset.table2`) ";
    String recommendation =
        new IdentifySubqueryInWhere()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(expected, recommendation);
  }

  @Test
  public void IdentifySubqueryInWhereWithDistinct() {
    String expected = "";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    t1.col1 not in (select distinct col1 from `project.dataset.table2`) ";
    String recommendation =
        new IdentifySubqueryInWhere()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(expected, recommendation);
  }

  @Test
  public void IdentifySubqueryInWhereWithStruct() {
    String expected = "You are using an IN filter with a subquery without a DISTINCT on the "
        + "following columns: project.dataset.table1.col1, project.dataset.table1.col2";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    (t1.col1, t1.col2) not in (select (col1, col2) from `project.dataset.table2`) ";
    String recommendation =
        new IdentifySubqueryInWhere()
            .run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
    assertEquals(expected, recommendation);
  }
}
