package com.google.zetasql.toolkit.antipattern.parser;

import static org.junit.Assert.*;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import org.junit.Before;
import org.junit.Test;

public class IdentifySingleRowInsertsTest {

    LanguageOptions languageOptions;

    @Before
    public void setUp() {
        languageOptions = new LanguageOptions();
        languageOptions.enableMaximumLanguageFeatures();
        languageOptions.setSupportsAllStatementKinds();
    }
    @Test
    public void insertSingleRow() {
        String expected = "SINGLE ROW INSERTS";
        String query =
                "INSERT dataset.Inventory (product, quantity) VALUES('top load washer', 10)";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifySingleRowInserts()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void insertMultiRow() {
        String expected = "OTHER INSERT PATTERN";
        String query =
                "INSERT dataset.Inventory (product, quantity) VALUES('top load washer', 10), ('abc', 20)";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifySingleRowInserts()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void insertSingleRowSelect() {
        String expected = "SINGLE ROW INSERTS";
        String query =
                "INSERT dataset.DetailedInventory (product, quantity) VALUES('countertop microwave', (SELECT quantity FROM dataset.DetailedInventory WHERE product = 'microwave'))";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifySingleRowInserts()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void insertSelectUnnest() {
        String expected = "OTHER INSERT PATTERN";
        String query =
                "INSERT dataset.Warehouse (warehouse, state) SELECT * FROM UNNEST([('warehouse #1', 'WA'), ('warehouse #2', 'CA'),  ('warehouse #3', 'WA')])";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifySingleRowInserts()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void insertCte() {
        String expected = "OTHER INSERT PATTERN";
        String query =
                "INSERT dataset.Warehouse (warehouse, state) WITH w AS (SELECT ARRAY<STRUCT<warehouse string, state string>> [('warehouse #1', 'WA'), ('warehouse #2', 'CA'),('warehouse #3', 'WA')] col) SELECT warehouse, state FROM w, UNNEST(w.col)";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifySingleRowInserts()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }

    @Test
    public void insertSelectFrom() {
        String expected = "OTHER INSERT PATTERN";
        String query =
                "INSERT dataset.DetailedInventory (product, quantity, supply_constrained) SELECT product,quantity, false FROM dataset.Inventory ";
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String recommendations = (new IdentifySingleRowInserts()).run(parsedQuery);
        assertEquals(expected, recommendations);
    }
}
