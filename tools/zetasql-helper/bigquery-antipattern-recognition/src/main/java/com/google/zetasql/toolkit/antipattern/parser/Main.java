package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;

public class Main {

  public static void main(String[] args) {
    // String query = "SELECT t1.col1 from `project.dataset.table1` t1 WHERE t1.col2 IN (select t2.col2 from `project.dataset.table2`)";
    // String query = "SELECT t1.col1 from `project.dataset.table1` t1 WHERE t1.col2 >0";
    String query = "SELECT "
        + " t1.col1 "
        + "FROM "
        + " `project.dataset.table1` t1 "
        + "CROSS JOIN "
        + "  `project.dataset.table2` t2 "
        + "WHERE "
        + "   t1.col1 = t2.col2"
        ;

    // String query = "SELECT * FROM `project.dataset.table1`";
    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);

    // System.out.println(parsedQuery);
    // System.out.println(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));
    // System.out.println(new IdentifySelectStar().run(parsedQuery));
     System.out.println(new IdentifyCrossJoin().run(parsedQuery));

  }
}


