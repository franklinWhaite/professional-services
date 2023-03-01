package com.google.zetasql;

import com.pso.bigquery.optimization.IdentidySelectedColumns;
import com.pso.bigquery.optimization.IdentifyCrossJoin;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.*;
import static org.junit.Assert.assertEquals;

public class IdentifyCrossJoinTest {

    String billing_project = MY_PROJET;
    SimpleCatalog catalog =  new SimpleCatalog(CATALOG_NAME);

    @Before
    public void setUp(){
        catalog.addZetaSQLFunctions(new ZetaSQLBuiltinFunctionOptions());
        SimpleColumn column1 = new SimpleColumn(
                TABLE_NAME,
                COL_1,
                TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING)
        );
        SimpleColumn column2 = new SimpleColumn(
                TABLE_NAME,
                COL_2,
                TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING)
        );
        SimpleTable table1 = new SimpleTable(TABLE_1_NAME, List.of(column1, column2));
        SimpleTable table2 = new SimpleTable(TABLE_2_NAME, List.of(column1, column2));
        SimpleTable table3 = new SimpleTable(TABLE_3_NAME, List.of(column1, column2));
        catalog.addSimpleTable(table1);
        catalog.addSimpleTable(table2);
        catalog.addSimpleTable(table3);
    }

    @Test
    public void crossJoinTwoTablesTest() {
        String expected = "CROSS JOIN between tables: project.dataset.table1 and project.dataset.table2. Try to change for a INNER JOIN if possible.";
        String query = "SELECT " +
                "   t1.col1 " +
                "FROM " +
                "   `project.dataset.table1` t1 " +
                "CROSS JOIN " +
                "    `project.dataset.table2` t2 " +
                "WHERE " +
                "   t1.col1 = t2.col1 ";
        String recommendation = new IdentifyCrossJoin().run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals(expected , recommendation);
    }

    @Test
    public void crossJoinTableSubqueryTest() {
        String expected = "CROSS JOIN between table project.dataset.table2 and subquery. Try to change for a INNER JOIN if possible.";
        String query = "SELECT " +
                "   t1.col1 " +
                "FROM " +
                "   (SELECT * FROM `project.dataset.table1`) t1 " +
                "CROSS JOIN " +
                "    `project.dataset.table2` t2 " +
                "WHERE " +
                "   t1.col1 = t2.col1 ";
        String recommendation = new IdentifyCrossJoin().run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals(expected , recommendation);
    }

    @Test
    public void crossJoinTwoSubqueries() {
        String expected = "CROSS JOIN between two subqueries. Try to change for a INNER JOIN if possible.";
        String query = "SELECT " +
                "   t1.col1 " +
                "FROM " +
                "   (SELECT * FROM `project.dataset.table1`) t1 " +
                "CROSS JOIN " +
                "    (SELECT * FROM `project.dataset.table2`) t2 " +
                "WHERE " +
                "   t1.col1 = t2.col1 ";
        String recommendation = new IdentifyCrossJoin().run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals(expected , recommendation);
    }

    @Test
    public void crossJoinSeveralJoins() {
        String expected = "CROSS JOIN with table: project.dataset.table3. Try to change for a INNER JOIN if possible.";
        String query = "SELECT " +
                "   t1.col1 " +
                "FROM " +
                "   `project.dataset.table1` t1 " +
                "INNER JOIN " +
                "    `project.dataset.table2` t2 ON t1.col2 = t2.col2 " +
                "CROSS JOIN " +
                "    `project.dataset.table3` t3  " +
                "WHERE " +
                "   t1.col1 = t3.col1 ";
        String recommendation = new IdentifyCrossJoin().run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals(expected , recommendation);
    }

    @Test
    public void crossJoinSeveralJoinsAndSubquery() {
        String expected = "CROSS JOIN in query. Try to change for a INNER JOIN if possible.";
        String query = "SELECT " +
                "   t1.col1 " +
                "FROM " +
                "   `project.dataset.table1` t1 " +
                "INNER JOIN " +
                "    `project.dataset.table2` t2 ON t1.col2 = t2.col2 " +
                "CROSS JOIN " +
                "    (SELECT * FROM  `project.dataset.table3`) t3  " +
                "WHERE " +
                "   t1.col1 = t3.col1 ";
        String recommendation = new IdentifyCrossJoin().run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals(expected , recommendation);
    }
}
