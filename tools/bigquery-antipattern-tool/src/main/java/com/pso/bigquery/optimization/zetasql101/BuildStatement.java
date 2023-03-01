package com.pso.bigquery.optimization.zetasql101;

import com.google.zetasql.*;
import com.google.zetasql.resolvedast.ResolvedNodes;

import java.util.List;
import com.google.zetasql.Analyzer;
import com.google.zetasql.AnalyzerOptions;
import com.google.zetasql.LanguageOptions;
import com.google.zetasql.SimpleCatalog;
import com.google.zetasql.SimpleColumn;
import com.google.zetasql.SimpleTable;
import com.google.zetasql.TypeFactory;
import com.google.zetasql.ZetaSQLBuiltinFunctionOptions;
import com.google.zetasql.ZetaSQLType.TypeKind;
import com.google.zetasql.resolvedast.ResolvedNodes.ResolvedStatement;
import java.util.List;

public class BuildStatement {

    private static AnalyzerOptions getAnalyzerOptions() {
        LanguageOptions languageOptions = new LanguageOptions()
                .enableMaximumLanguageFeatures();
        languageOptions.setSupportsAllStatementKinds();
        AnalyzerOptions analyzerOptions = new AnalyzerOptions();
        analyzerOptions.setLanguageOptions(languageOptions);

        return analyzerOptions;
    }

    public static void main(String[] args) {
        SimpleCatalog catalog = new SimpleCatalog("catalog");
        catalog.addZetaSQLFunctions(new ZetaSQLBuiltinFunctionOptions());

        SimpleCatalog project = catalog.addNewSimpleCatalog("project");
        SimpleCatalog dataset = project.addNewSimpleCatalog("dataset");

        SimpleTable table = new SimpleTable(
                "project.dataset.TableA",
                List.of(
                        new SimpleColumn(
                                "TableA",
                                "Column1",
                                TypeFactory.createSimpleType(TypeKind.TYPE_STRING)
                        )
                )
        );
        table.setFullName("project.dataset.TableA");

        dataset.addSimpleTable("TableA", table);

        String sql = "SELECT * FROM project.dataset.TableA WHERE Column1 = 'Hello World!';";

        ResolvedStatement statement = Analyzer.analyzeStatement(sql, getAnalyzerOptions(), catalog);

        String rebuiltSql = Analyzer.buildStatement(statement, catalog);

        System.out.printf("Original SQL: \n%s\n\n", sql);

        System.out.printf("AST: \n%s\n\n", statement.debugString());

        System.out.printf("Rebuild SQL: \n%s\n\n", rebuiltSql);

    }
}
