package com.google.zetasql.toolkit.antipattern.cmd;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.*;

public class BQAntiPatternCMDParser {

  public static final String QUERY_OPTION_NAME = "query";
  public static final String FILE_PATH_OPTION_NAME = "file_path";
  public static final String FOLDER_PATH_OPTION_NAME = "folder_path";
  public static final String READ_FROM_INFO_SCHEMA_FLAG_NAME = "read_from_info_schema";
  public static final String PROCESSING_PROJECT_ID_OPTION_NAME = "processing_project_id";
  public static final String OUTPUT_PROJECT_ID_OPTION_NAME = "output_project_id";

  private Options options;
  private CommandLine cmd;

  public BQAntiPatternCMDParser(String[] args) throws ParseException {
    options = getOptions();
    CommandLineParser parser = new BasicParser();
    cmd = parser.parse(options, args);

  }

  public String getOutputTableProjectId() {
    return cmd.getOptionValue(OUTPUT_PROJECT_ID_OPTION_NAME);
  }

  public Options getOptions() {
    Options options = new Options();
    Option query =
        Option.builder(QUERY_OPTION_NAME).argName(QUERY_OPTION_NAME).hasArg().required(false).desc("set query").build();
    options.addOption(query);

    Option filePath =
        Option.builder(FILE_PATH_OPTION_NAME)
            .argName(FILE_PATH_OPTION_NAME)
            .hasArg()
            .required(false)
            .desc("set file path")
            .build();
    options.addOption(filePath);

    Option folderPath =
        Option.builder(FOLDER_PATH_OPTION_NAME)
            .argName(FOLDER_PATH_OPTION_NAME)
            .hasArg()
            .required(false)
            .desc("set file path")
            .build();
    options.addOption(folderPath);

    Option useInfoSchemaFlag =
        Option.builder(READ_FROM_INFO_SCHEMA_FLAG_NAME)
            .argName(READ_FROM_INFO_SCHEMA_FLAG_NAME)
            .required(false)
            .desc("flag specifying if the queries should be read from INFORMATION_SCHEMA")
            .build();
    options.addOption(useInfoSchemaFlag);

    Option procesingProjectOption =
        Option.builder(PROCESSING_PROJECT_ID_OPTION_NAME)
            .argName(PROCESSING_PROJECT_ID_OPTION_NAME)
            .hasArg()
            .required(false)
            .desc("project where the solution will execute")
            .build();
    options.addOption(procesingProjectOption);

    Option outputProjectOption =
        Option.builder(OUTPUT_PROJECT_ID_OPTION_NAME)
            .argName(OUTPUT_PROJECT_ID_OPTION_NAME)
            .hasArg()
            .required(false)
            .desc("project with the table to which output will be written")
            .build();
    options.addOption(outputProjectOption);

    return options;
  }

  public Iterator<InputQuery> getInputQueries() {
    try {
      if (cmd.hasOption(READ_FROM_INFO_SCHEMA_FLAG_NAME)) {
        return new InformationSchemaQueryIterable(cmd.getOptionValue(
            PROCESSING_PROJECT_ID_OPTION_NAME));
      } else if (cmd.hasOption(QUERY_OPTION_NAME)) {
        return buildIteratorFromQueryStr(cmd.getOptionValue(QUERY_OPTION_NAME));
      } else if (cmd.hasOption(FILE_PATH_OPTION_NAME)) {
        return buildIteratorFromFilePath(cmd.getOptionValue(FILE_PATH_OPTION_NAME));
      } else if (cmd.hasOption(FOLDER_PATH_OPTION_NAME)) {
        return buildIteratorFromFolderPath(cmd.getOptionValue(FOLDER_PATH_OPTION_NAME));
      }
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
      System.exit(0);
    }
    return null;
  }

  public static Iterator<InputQuery> buildIteratorFromQueryStr(String queryStr) {
    InputQuery inputQuery = new InputQuery(queryStr, "inline query");
    return (new ArrayList<>(Arrays.asList(inputQuery))).iterator();
  }

  public static Iterator<InputQuery> buildIteratorFromFilePath(String filePath) {
    return new InputFolderQueryIterable(new ArrayList<>(Arrays.asList(filePath)));
  }

  public static Iterator<InputQuery> buildIteratorFromFolderPath(String folderPath) {
    List<String> fileList =
        Stream.of(new File(folderPath).listFiles())
            .filter(file -> file.isFile())
            .map(File::getAbsolutePath)
            .collect(Collectors.toList());
    return new InputFolderQueryIterable(fileList);
  }
}
