package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdentifyOrderByWithoutLimitTest {

  LanguageOptions languageOptions;

  @Before
  public void setUp() {
    languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();
  }

  @Test
  public void OrderByWithoutLimitTest() {
    String expected = "Order By clause without Limit.";
    String query = "SELECT t1.col1 FROM `project.dataset.table1` t1 ORDER BY t1.col1;";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyOrderByWithoutLimit().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

  @Test
  public void OrderByWithLimitTest() {
    String expected = "";
    String query = "SELECT t1.col1 FROM `project.dataset.table1` t1 ORDER BY t1.col1 LIMIT 5;";
    ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
    String recommendation = new IdentifyOrderByWithoutLimit().run(parsedQuery);
    assertEquals(expected, recommendation);
  }

}
