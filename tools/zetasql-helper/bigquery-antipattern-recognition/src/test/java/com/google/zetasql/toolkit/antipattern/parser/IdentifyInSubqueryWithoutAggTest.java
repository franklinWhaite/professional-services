package com.google.zetasql.toolkit.antipattern.parser;

import static org.junit.Assert.assertEquals;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import org.junit.Before;
import org.junit.Test;

public class IdentifyInSubqueryWithoutAggTest {

  LanguageOptions languageOptions;

  @Before
  public void setUp() {
    languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();
  }

  @Test
  public void IdentifySubqueryInWhereTest() {
    String expected =
        "Subquery in the WHERE clause without aggregation.";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    t1.col2 not in (select col2 from `project.dataset.table2`) ";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation =
        new IdentifyInSubqueryWithoutAgg().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

}
