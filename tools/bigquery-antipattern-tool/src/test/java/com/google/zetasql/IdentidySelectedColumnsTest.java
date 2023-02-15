package com.google.zetasql;

import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import org.junit.Before;
import org.junit.Test;

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.*;
import static org.junit.Assert.*;

import java.util.List;

public class IdentidySelectedColumnsTest {

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
    public void selectStarSingleTableTest() {
        String query = "SELECT * FROM `project.dataset.table1`";
        String recommendations = IdentidySelectedColumns.run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals("All columns on table: project.dataset.table1 are being selected. Please be sure that all columns are needed" , recommendations);
    }

    @Test
    public void selectEachColSingleTableTest() {
        String query = "SELECT col1, col2 FROM `project.dataset.table1`";
        String recommendations = IdentidySelectedColumns.run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals("All columns on table: project.dataset.table1 are being selected. Please be sure that all columns are needed" , recommendations);
    }

    @Test
    public void selectStarWithJoinTest() {
        String query =
                "\n" +
                        "SELECT " +
                        "   t1.*, " +
                        "   t2.col2 " +
                        "FROM \n" +
                        "  `project.dataset.table1` t1\n" +
                        "LEFT JOIN\n" +
                        "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";
        String recommendations = IdentidySelectedColumns.run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals("All columns on table: project.dataset.table1 are being selected. Please be sure that all columns are needed" , recommendations);
    }

    @Test
    public void selectStarWithJoinAndSubSelectTest() {
        //failing
        String query =
                "\n" +
                        "SELECT " +
                        "   t1.*, " +
                        "   t2.col2 " +
                        "FROM \n" +
                        "  (SELECT * FROM `project.dataset.table1`) t1\n" +
                        "LEFT JOIN\n" +
                        "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";
        String recommendations = IdentidySelectedColumns.run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL);
        assertEquals("All columns on table: project.dataset.table1 are being selected. Please be sure that all columns are needed" , recommendations);
    }

}
