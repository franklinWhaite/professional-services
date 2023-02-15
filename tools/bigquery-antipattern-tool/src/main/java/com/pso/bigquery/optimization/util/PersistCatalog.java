package com.pso.bigquery.optimization.util;

import com.google.zetasql.SimpleCatalog;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;
import com.pso.bigquery.optimization.analysis.visitors.ExtractScansVisitor;
import com.pso.bigquery.optimization.catalog.CatalogUtils;
import io.vavr.control.Try;

import java.io.*;
import java.util.List;

public class PersistCatalog {

    public static void main(String[] args) {
        String PROJECT_ID = "MY-PROJECT";
        String QUERY =
                "SELECT \n"
                        + "  t1.unique_key \n"
                        + "FROM \n"
                        + "  `MY-PROJECT.DATASET_1.test_table_1` t1\n"
                        + "LEFT JOIN\n"
                        + "  `MY-PROJECT.DATASET_1.test_table_2` t2 ON t1.unique_key=t2.unique_key\n"
                        + "WHERE\n"
                        + " t1.unique_key is not null\n"
                        + " AND t2.unique_key is not null\n";
        SimpleCatalog catalog = CatalogUtils.createCatalogForProject(PROJECT_ID);

        try {

            FileOutputStream f = new FileOutputStream(new File("myCatalog.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write serialized catalog to file
            o.writeObject(catalog);

            o.close();
            f.close();

            FileInputStream fi = new FileInputStream(new File("myCatalog.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read catalog from file
            SimpleCatalog catalogDeserialized  = (SimpleCatalog) oi.readObject();
            oi.close();
            fi.close();

            // setup ZetatSQL
            QueryAnalyzer parser = new QueryAnalyzer();
            Try<List<ExtractScansVisitor.QueryScan>> tryScans =
                    parser.getScansInQuery(PROJECT_ID, QUERY, catalogDeserialized, QueryAnalyzer.CatalogScope.PROJECT);
            List<ExtractScansVisitor.QueryScan> scanResults = tryScans.get();

            scanResults.stream()
                    .map(ExtractScansVisitor.QueryScan::toMap)
                    .forEach(scanResult -> System.out.println(scanResult));


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}