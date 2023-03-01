package com.pso.bigquery.optimization.zetasql101;

import com.google.zetasql.*;
import com.google.zetasql.resolvedast.ResolvedNodes;

import java.util.List;

public class BuildQueryFromResolvedStmnt {


    private static AnalyzerOptions getAnalyzerOptions() {
        LanguageOptions languageOptions = new LanguageOptions()
                .enableMaximumLanguageFeatures();
        languageOptions.setSupportsAllStatementKinds();

        AnalyzerOptions analyzerOptions = new AnalyzerOptions();
        analyzerOptions.setLanguageOptions(languageOptions);
        analyzerOptions.setCreateNewColumnForEachProjectedOutput(true);
        return analyzerOptions;
    }

    public static void main(String[] args) {
        SimpleCatalog catalog = new SimpleCatalog("catalog");
        catalog.addZetaSQLFunctions(new ZetaSQLBuiltinFunctionOptions());
        SimpleColumn column1 = new SimpleColumn(
                "project.dataset.table",
                "col1",
                TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING)
        );
        SimpleColumn column2 = new SimpleColumn(
                "project.dataset.table",
                "col2",
                TypeFactory.createSimpleType(ZetaSQLType.TypeKind.TYPE_STRING)
        );
        SimpleTable table1 = new SimpleTable("project.dataset.table1", List.of(column1, column2));
        SimpleTable table2 = new SimpleTable("project.dataset.table2", List.of(column1, column2));
        SimpleTable table3 = new SimpleTable("project.dataset.table3", List.of(column1, column2));
        catalog.addSimpleTable(table1);
        catalog.addSimpleTable(table2);
        catalog.addSimpleTable(table3);

        AnalyzerOptions analyzerOptions = getAnalyzerOptions();

        String query =
                "\n" +
                        "SELECT " +
                        "   * " +
                        "FROM \n" +
                        "  `project.dataset.table1` t1\n" ;

        ResolvedNodes.ResolvedStatement resolvedStatement = Analyzer.analyzeStatement(
                query, analyzerOptions, catalog
        );

        System.out.println(resolvedStatement.debugString());

    }
}
