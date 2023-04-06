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
        + "WHERE "
        //+ " t1.col2 IN (select distinct t2.col2 from `project.dataset.table2` t2)"
        + " t1.col2 IN (select t2.col2 from `project.dataset.table2` t2)"
       // + " t1.col2 IN (select t2.col2 from `project.dataset.table2` t2 group by t2.col2)"
       // + " t1.col2 IN (1,2,3)"
        + " AND t1.col3>0";

    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);

    //System.out.println(parsedQuery);
    System.out.println(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));

  }
}


