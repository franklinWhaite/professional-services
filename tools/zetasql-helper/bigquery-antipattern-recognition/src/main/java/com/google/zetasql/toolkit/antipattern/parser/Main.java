package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.cmd.BQAntiPatternCMDParser;
import com.google.zetasql.toolkit.antipattern.cmd.InputQuery;
import com.google.zetasql.toolkit.antipattern.cmd.OutputGenerator;
import com.google.zetasql.toolkit.antipattern.util.BigQueryHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws ParseException, IOException {
    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    BQAntiPatternCMDParser cmdParser = new BQAntiPatternCMDParser(args);
    Iterator<InputQuery> inputQueriesIterator = cmdParser.getInputQueries();

    InputQuery inputQuery;
    int countQueries = 0, countAntiPatterns = 0, countErrors = 0;
    while (inputQueriesIterator.hasNext()) {
      inputQuery = inputQueriesIterator.next();
      String query = inputQuery.getQuery();
      List<String[]> outputData = new ArrayList<>();

      try {
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String rec = getRecommendations(parsedQuery);
        if (rec.length() > 0) {
          addRecToOutput(cmdParser, outputData, inputQuery, rec);
          OutputGenerator.writeOutput(cmdParser, outputData);
          countAntiPatterns += 1;
        }
      } catch (Exception e) {
        countErrors += 1;
      }
      countQueries += 1;
    }
    logger.info("Processing finished."
        + "Queries read: {}. "
        + "Queries with anti-patterns: {}. "
        + "Queries that could not be parsed: {}.", countQueries, countAntiPatterns, countErrors);
  }

  private static void addRecToOutput(
      BQAntiPatternCMDParser cmdParser,
      List<String[]> outputData,
      InputQuery inputQuery,
      String rec) {
    if (cmdParser.isReadingFromInfoSchema()) {
      outputData.add(
          new String[] {
            inputQuery.getQueryId(),
            inputQuery.getQuery(),
            Float.toString(inputQuery.getSlotHours()),
            "\"" + rec + "\"",
          });
    } else {
      outputData.add(new String[] {inputQuery.getQueryId(), "\"" + rec + "\""});
    }
  }

  private static String getRecommendations(ASTStatement parsedQuery) {
    ArrayList<String> recommendation = new ArrayList<>();
    recommendation.add(new IdentifySimpleSelectStar().run(parsedQuery));
    recommendation.add(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));
    recommendation.add(new IdentifyCrossJoin().run(parsedQuery));
    recommendation.add(new IdentifyCTEsEvalMultipleTimes().run(parsedQuery));
    return recommendation.stream().filter(x -> x.length() > 0).collect(Collectors.joining("\n"));
  }
}
