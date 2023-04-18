package com.google.zetasql.toolkit.antipattern.parser;

import static org.junit.Assert.*;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import org.junit.Before;
import org.junit.Test;

public class IdentifySimpleSelectStarTest {
  LanguageOptions languageOptions;

  @Before
  public void setUp() {
    languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();
  }

  @Test
  public void simpleSelectStarTest() {
    String expected = "SELECT * on table: project.dataset.table1. Check that all columns are needed.";
    String query = "SELECT * FROM `project.dataset.table1`";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendations = (new IdentifySimpleSelectStar()).run(parsedQuery);
    assertEquals(
        expected,
        recommendations);
  }

  @Test
  public void simpleSelectStarWithLimitTest() {
    String expected = "SELECT * on table: project.dataset.table1. Check that all columns are needed.";
    String query = "SELECT * FROM `project.dataset.table1` LIMIT 1000";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendations = (new IdentifySimpleSelectStar()).run(parsedQuery);
    assertEquals(
        expected,
        recommendations);
  }

  @Test
  public void selectStarWithGroupByTest() {
    String expected = "";
    String query = "SELECT * FROM `project.dataset.table1` GROUP BY col1";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendations = (new IdentifySimpleSelectStar()).run(parsedQuery);
    assertEquals(
        expected,
        recommendations);
  }

  @Test
  public void selectStarWithJoinTest() {
    String expected = "";
    String query = "SELECT * FROM table1, table2";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendations = (new IdentifySimpleSelectStar()).run(parsedQuery);
    assertEquals(
        expected,
        recommendations);
  }
}
