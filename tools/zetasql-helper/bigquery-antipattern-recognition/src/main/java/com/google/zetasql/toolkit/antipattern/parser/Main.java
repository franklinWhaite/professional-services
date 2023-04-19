package com.google.zetasql.toolkit.antipattern.parser;

import com.google.zetasql.LanguageOptions;
import com.google.zetasql.Parser;
import com.google.zetasql.parser.ASTNodes.ASTStatement;
import com.google.zetasql.toolkit.antipattern.cmd.BQAntiPatternCMDParser;
import com.google.zetasql.toolkit.antipattern.cmd.InputQuery;
import com.google.zetasql.toolkit.antipattern.cmd.OutputGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.ParseException;

public class Main {

  public static void main(String[] args) throws ParseException, IOException {

    LanguageOptions languageOptions = new LanguageOptions();
    languageOptions.enableMaximumLanguageFeatures();
    languageOptions.setSupportsAllStatementKinds();

    BQAntiPatternCMDParser cmdParser = new BQAntiPatternCMDParser(args);
    Iterator<InputQuery> inputQueriesIterator = cmdParser.getInputQueries();

    InputQuery inputQuery;
    List<String[]> outputData = new ArrayList<>();

    while (inputQueriesIterator.hasNext()) {
      inputQuery = inputQueriesIterator.next();
      String query = inputQuery.getQuery();


      try {
        ASTStatement parsedQuery = Parser.parseStatement(query, languageOptions);
        String rec = getRecommendations(parsedQuery);
        addRecToOutput(cmdParser, outputData, inputQuery, rec);
      } catch (Exception e) {
        outputData.add(new String[] {inputQuery.getQueryId(), "error"});
      }
    }
    OutputGenerator.writeOutput(cmdParser, outputData);
  }

  private static void addRecToOutput(
      BQAntiPatternCMDParser cmdParser,
      List<String[]> outputData,
      InputQuery inputQuery,
      String rec) {
    if (rec.length() > 0) {
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
  }

  private static String getRecommendations(ASTStatement parsedQuery) {
    ArrayList<String> recommendation = new ArrayList<>();
    recommendation.add(new IdentifySimpleSelectStar().run(parsedQuery));
    recommendation.add(new IdentifyInSubqueryWithoutAgg().run(parsedQuery));
    recommendation.add(new IdentifyCrossJoin().run(parsedQuery));
    recommendation.add(new IdentifyCTEsEvalMultipleTimes().run(parsedQuery));
    return recommendation.stream().filter(x-> x.length()>0).collect(Collectors.joining("\n"));

  }
}
