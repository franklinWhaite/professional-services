package com.google.zetasql.toolkit.antipattern.parser;

import static org.junit.Assert.*;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import org.junit.Before;
import org.junit.Test;

public class IdentifyRegexpContainsTest {
    LanguageOptions languageOptions;

    @Before
    public void setUp() {
        languageOptions = new LanguageOptions();
        languageOptions.enableMaximumLanguageFeatures();
        languageOptions.setSupportsAllStatementKinds();
    }

    @Test
    public void regexContainsOnly() {
        String expected = "REGEXP_CONTAINS : Prefer LIKE when the full power of regex is not needed (e.g. wildcard matching).";
        String query =
                "select dim1 from `dataset.table` where regexp_contains(dim1, '.*test.*')";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifyRegexpContains()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void regexContainsWithOtherFuncs() {
        String expected = "REGEXP_CONTAINS : Prefer LIKE when the full power of regex is not needed (e.g. wildcard matching).";
        String query =
                "select dim1 from `dataset.table` where effective_start_dte = current_date() and dim2 = 'x' and regexp_contains(dim1, '.*test.*')";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifyRegexpContains()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void withoutRegexContains() {
        String expected = "";
        String query =
                "select dim1 from `dataset.table` where effective_start_dte = current_date()";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifyRegexpContains()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void otherFunc() {
        String expected = "";
        String query =
                "select dim1 from `dataset.table` where effective_start_dte = current_date() and lower(\"Sunday\") = day_of_week";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifyRegexpContains()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void regexpContainWith() {
        String expected = "REGEXP_CONTAINS : Prefer LIKE when the full power of regex is not needed (e.g. wildcard matching).";
        String query =
                "with a as (select dim1 from `dataset.table` where effective_start_dte = current_date() and regexp_contains(dim1, '.*test.*') and lower('Sunday') = day_of_week) select * from a";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifyRegexpContains()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

}
/*
Test cases
1. Regexp_contains only = "select dim1 from `dataset.table` where regexp_contains(dim1, '.*test.*')" - REGEX PATTERN
2. Regexp_contains with other functions = "select dim1 from `dataset.table` where effective_start_dte = current_date() and dim2 = 'x' and regexp_contains(dim1, '.*test.*')" - REGEX PATTERN
3. Query without regexp_contains = "select dim1 from `dataset.table` where effective_start_dte = current_date()" - NO_PATTERN
4. Query with other functions but regexp_contains = "select dim1 from `dataset.table` where effective_start_dte = current_date() and lower("Sunday") = day_of_week" - NO_PATTERN
5. Regexp_contains inside complex query where clause = "with a as (select dim1 from `dataset.table` where effective_start_dte = current_date() and regexp_contains(dim1, '.*test.*') and lower('Sunday') = day_of_week) select * from a "
 */