package com.google.zetasql.toolkit.antipattern.util;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.IdentifyCrossJoin;
import com.google.zetasql.toolkit.antipattern.parser.IdentifyInSubqueryWithoutAgg;
import com.google.zetasql.toolkit.antipattern.parser.IdentifySelectStar;

public class PrintDebugString {

  public static void main(String[] args) {
    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    String query =
        "";

    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    System.out.println(parsedQuery);
    System.out.println(new IdentifySelectStar().run(parsedQuery));
    System.out.println(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));
    System.out.println(new IdentifyCrossJoin().run(parsedQuery));

  }
}
