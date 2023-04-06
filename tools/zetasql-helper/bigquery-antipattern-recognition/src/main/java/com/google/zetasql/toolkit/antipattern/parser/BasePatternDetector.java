package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;

public interface BasePatternDetector {

  public String run(ASTStatement parsedQuery);

}
