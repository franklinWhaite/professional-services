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
  public void BasicInExpressionTest() {
    String expected = "Subquery in the WHERE clause without aggregation.";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    t1.col2 IN (SELECT col2 FROM `project.dataset.table2`) ";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyInSubqueryWithoutAgg().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

  @Test
  public void BasicNotInExpressionTest() {
    String expected = "Subquery in the WHERE clause without aggregation.";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "    t1.col2 NOT IN (SELECT col2 FROM `project.dataset.table2`) ";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyInSubqueryWithoutAgg().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

  @Test
  public void InExpressionWithOtherFiltersTest() {
    String expected = "Subquery in the WHERE clause without aggregation.";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "   t1.col1 > 0 "
            + "   AND t1.col2 IN (SELECT col2 FROM `project.dataset.table2`) "
            + "   AND t1.col3 = 1";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyInSubqueryWithoutAgg().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

  @Test
  public void noAntiPatternTest() {
    String expected = "";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "   t1.col1 > 0 "
            + "   AND t1.col2 IN (SELECT DISTINCT col2 FROM `project.dataset.table2`) "
            + "   AND t1.col3 IN (SELECT col3 FROM `project.dataset.table3` GROUP BY col3) "
            + "   AND t1.col4 IN (1, 2, 3, 4)";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyInSubqueryWithoutAgg().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

  @Test
  public void inExpressionWithStructTest() {
    String expected = "Subquery in the WHERE clause without aggregation.";
    String query =
        "SELECT "
            + "   t1.col1 "
            + "FROM "
            + "   `project.dataset.table1` t1 "
            + "WHERE "
            + "   (t1.col1,t1.col2) IN (SELECT (col1,col2) FROM `project.dataset.table3`) ";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyInSubqueryWithoutAgg().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

}
