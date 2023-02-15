package com.pso.bigquery.optimization;

import com.google.zetasql.*;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;

import java.util.List;

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.*;

public class Main {

    public static void main(String[] args){

        String billing_project = MY_PROJET;
        SimpleCatalog catalog =  new SimpleCatalog(CATALOG_NAME);

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

        String query =
                "\n" +
                        "SELECT " +
                        "   t1.col1, " +
                        "   t2.col2 " +
                        "FROM \n" +
                        "  (SELECT col1 FROM `project.dataset.table1`) t1\n" +
                        "LEFT JOIN\n" +
                        "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";

        System.out.println(new IdentidySelectedColumns().run(query, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL));
    }

}
