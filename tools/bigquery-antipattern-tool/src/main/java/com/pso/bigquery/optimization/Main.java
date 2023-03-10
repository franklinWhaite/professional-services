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
package com.pso.bigquery.optimization;

import com.google.zetasql.*;
import com.pso.bigquery.optimization.analysis.QueryAnalyzer;

import java.text.ParseException;
import java.util.List;

import static com.pso.bigquery.optimization.util.ZetaSQLHelperConstants.*;

import org.apache.commons.cli.*;

public class Main {

  public static void main(String[] args) throws ParseException {

    String query_str =
        "\n" +
            "SELECT " +
            "   t1.*, " +
            "   t2.col2 " +
            "FROM \n" +
            "  (SELECT * FROM `project.dataset.table1`) t1\n" +
            "LEFT JOIN\n" +
            "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";

    // Options options = new Options();
    //
    // Option query = Option.builder("query")
    //     .argName("query")
    //     .hasArg()
    //     .required(true)
    //     .desc("set query").build();
    // options.addOption(query);
    //
    // CommandLine cmd;
    // CommandLineParser parser = new BasicParser();
    // HelpFormatter helper = new HelpFormatter();
    //
    // try {
    //   cmd = parser.parse(options, args);
    //   if(cmd.hasOption("query")){
    //     query_str = cmd.getOptionValue("query");
    //     System.out.println("query detected as " + query_str);
    //   }
    // } catch (org.apache.commons.cli.ParseException e) {
    //   System.out.println(e.getMessage());
    //   helper.printHelp("Usage:", options);
    //   System.exit(0);
    // }

    String billing_project = MY_PROJET;
    SimpleCatalog catalog = new SimpleCatalog(CATALOG_NAME);

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

    query_str = "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            //+ "   col1 = 'a' "
            + "    t1.col2 not in (select col2 from `project.dataset.table2`) "
            //+ "   AND t1.col2 not in (select distinct col2 from `project.dataset.table2`) "
            //+ "   AND t1.col2 in (select distinct col2 from `project.dataset.table2`) "
            //+ "   AND (t1.col1, t1.col2) not in (select (col1, col2) from `project.dataset.table2`) "
    ;
    //System.out.println(new IdentidySelectedColumns().run(query_str, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL));
    // System.out.println(new IdentifyCrossJoin().run(query_str, billing_project, catalog,
    //     QueryAnalyzer.CatalogScope.MANUAL));
    System.out.println(new IdentifySubqueryInWhere().run(query_str, billing_project, catalog, QueryAnalyzer.CatalogScope.MANUAL));
  }

}