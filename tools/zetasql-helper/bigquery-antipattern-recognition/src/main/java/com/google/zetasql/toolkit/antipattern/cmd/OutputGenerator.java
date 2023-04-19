package com.google.zetasql.toolkit.antipattern.cmd;

import static com.google.zetasql.toolkit.antipattern.cmd.BQAntiPatternCMDParser.OUTPUT_FILE_OPTION_NAME;

import com.google.api.client.util.DateTime;
import com.google.zetasql.toolkit.antipattern.util.BigQueryHelper;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputGenerator {


  public static void writeOutput(BQAntiPatternCMDParser cmdParser, List<String[]> outputData)
      throws IOException {
    if(cmdParser.hasOutputFileOptionName()) {
      writeOutputToCSV(outputData, cmdParser);
    } else if (cmdParser.isReadingFromInfoSchema() && cmdParser.hasOutputTable()) {
      writeOutputToBQTable(outputData, cmdParser);
    } else {
      for (String[] row : outputData) {
        System.out.println("----------------------------------------");
        System.out.println(String.join("\n", row));
      }
    }
  }

  private static void writeOutputToCSV(List<String[]> outputData, BQAntiPatternCMDParser cmdParser) throws IOException {
    FileWriter csvWriter = new FileWriter(cmdParser.getOutputFileOptionName());
    csvWriter.write(String.join(",", new String[]{"id", "query\n"}));
    for (String[] row : outputData) {
      csvWriter.write(String.join(",", row));
      csvWriter.write("\n");
    }
    csvWriter.close();
  }

  private static void writeOutputToBQTable(List<String[]> outputData, BQAntiPatternCMDParser cmdParser) {
    DateTime date = new DateTime(new Date());
    for (String[] row : outputData) {
      Map<String, Object> rowContent = new HashMap<>();
      rowContent.put("job_id", row[0]);
      rowContent.put("query", row[1]);
      rowContent.put("slot_hours", row[2]);
      rowContent.put("recommendation", row[3]);
      rowContent.put("process_timestamp", date);
      BigQueryHelper.writeResults(cmdParser.getProcessingProject(), cmdParser.getOutputTable(), rowContent);
    }
  }
}
