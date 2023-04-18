package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.parser.visitors.IdentifyCTEsEvalMultipleTimesVisitor;
import java.util.stream.Collectors;

/**
 This class is a concrete implementation of the BasePatternDetector interface, which identifies
 common anti-patterns related to parsing of ZetaSQL queries. It specifically checks whether
 any common table expressions (CTEs) are evaluated multiple times within the same query.
 */
public class IdentifyCTEsEvalMultipleTimes implements BasePatternDetector {

  @Override
  public String run(ASTStatement parsedQuery) {
    IdentifyCTEsEvalMultipleTimesVisitor visitor = new IdentifyCTEsEvalMultipleTimesVisitor();
    parsedQuery.accept(visitor);
    return visitor.getResult().stream().distinct().collect(Collectors.joining("\n"));
  }
}
