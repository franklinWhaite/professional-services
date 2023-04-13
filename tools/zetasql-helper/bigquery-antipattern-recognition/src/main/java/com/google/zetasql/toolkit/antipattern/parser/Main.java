package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;

public class Main {

  public static void main(String[] args) {
    // String query = "SELECT t1.col1 from `project.dataset.table1` t1 WHERE t1.col2 IN (select
    // t2.col2 from `project.dataset.table2`)";
    // String query = "SELECT t1.col1 from `project.dataset.table1` t1 WHERE t1.col2 >0";
    String query =
        "SELECT "
            + " t1.* "
            + "FROM "
            + " `project.dataset.table1` t1 "
            // + "INNER JOIN "
            // + " `project.dataset.table3` t3 ON t1.col1 = t3.col3"
            + "CROSS JOIN "
            + "  (SELECT * FROM `project.dataset.table2`) t2 "
            + "WHERE "
            + "   t1.col3 > 0"
            + "   AND t1.col1 = t2.col1 "
            + "   AND t1.col2 > t2.col2 "
            + "   AND t1.col4 in (SELECT distinct col4 from table4) ";

    // query = "SELECT * FROM `project.dataset.table1`";
    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);

    System.out.println(parsedQuery);
    // System.out.println(new IdentifySelectStar().run(parsedQuery));
    // System.out.println(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));
    // System.out.println(new IdentifyCrossJoin().run(parsedQuery));

  }
}


