package com.google.zetasql.toolkit.antipattern.cmd;

import static com.google.zetasql.toolkit.antipattern.cmd.BQAntiPatternCMDParser.OUTPUT_FILE_OPTION_NAME;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputGenerator {


  public static void writeOutput(BQAntiPatternCMDParser cmdParser, List<String[]> outputData)
      throws IOException {
    if(cmdParser.hasOutputFileOptionName()) {
      writeOutputToCSV(outputData, cmdParser);
    }
  }
  public static void writeOutputToCSV(List<String[]> outputData, BQAntiPatternCMDParser cmdParser) throws IOException {
    FileWriter csvWriter = new FileWriter(cmdParser.getOutputFileOptionName());
    csvWriter.write(String.join(",", new String[]{"id", "query\n"}));
    for (String[] row : outputData) {
      csvWriter.write(String.join(",", row));
      csvWriter.write("\n");
    }
    csvWriter.close();
  }

}
