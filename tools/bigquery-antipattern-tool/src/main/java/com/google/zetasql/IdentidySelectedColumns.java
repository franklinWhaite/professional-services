package com.google.zetasql;

import com.google.zetasql.Table;
import com.pso.bigquery.optimization.BasePatternDetector;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.SelectedColumnsVisitor;
import io.vavr.control.Try;

import java.util.List;

public class IdentidySelectedColumns implements BasePatternDetector {

    public static String run(String query, String billingProjectId, SimpleCatalog catalog, QueryAnalyzer.CatalogScope catalogScope) {
        SelectedColumnsVisitor visitor = new SelectedColumnsVisitor(billingProjectId, catalog);

        parser.visitQuery(billingProjectId, query, catalog, catalogScope, visitor);

        List<SelectedColumnsVisitor.TableWithSelectedCol> visitorResults = visitor.getResult();
        StringBuffer sb = new StringBuffer();

        visitorResults.stream()
                .forEach(tableWithSelectedCol -> checkTable(tableWithSelectedCol, catalog, sb));

        return sb.toString();

    }

    static void checkTable(SelectedColumnsVisitor.TableWithSelectedCol tableWithSelectedCol, SimpleCatalog catalog, StringBuffer sb) {
        String tableName = tableWithSelectedCol.getTable();
        Table tableInCatalog = catalog.getTable(tableName);

        if(tableInCatalog.getColumnCount() == tableWithSelectedCol.getSelectedColumns().size()) {
           sb.append(String.format("All columns on table: %s are being selected. Please be sure that all columns are needed", tableName));
        }

    }

}
