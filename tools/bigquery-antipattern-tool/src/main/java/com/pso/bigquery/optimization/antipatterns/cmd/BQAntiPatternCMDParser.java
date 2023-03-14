package com.pso.bigquery.optimization.antipatterns.cmd;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BQAntiPatternCMDParser {

  public static final String QUERY = "query";
  public static final String FILE_PATH = "file_path";
  public static final String FOLDER_PATH = "folder_path";

  public static Iterator<InputQuery> getInputQueries(String[] args) {
    Options options = new Options();

    Option query = Option.builder(QUERY)
        .argName(QUERY)
        .hasArg()
        .required(false)
        .desc("set query").build();
    options.addOption(query);

    Option filePath = Option.builder(FILE_PATH)
        .argName(FILE_PATH)
        .hasArg()
        .required(false)
        .desc("set file path").build();
    options.addOption(filePath);

    Option folderPath = Option.builder(FOLDER_PATH)
        .argName(FOLDER_PATH)
        .hasArg()
        .required(false)
        .desc("set file path").build();
    options.addOption(folderPath);

    CommandLine cmd;
    CommandLineParser parser = new BasicParser();
    HelpFormatter helper = new HelpFormatter();

    try {
      cmd = parser.parse(options, args);
      if (cmd.hasOption(QUERY)) {
        return buildIteratorFromQueryStr(cmd.getOptionValue(QUERY));
      } else if (cmd.hasOption(FILE_PATH)) {
        return buildIteratorFromFilePath(cmd.getOptionValue(FILE_PATH));
      } else if (cmd.hasOption(FOLDER_PATH)) {
        return buildIteratorFromFolderPath(cmd.getOptionValue(FOLDER_PATH));
      }
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      helper.printHelp("Usage:", options);
      System.exit(0);
    }
    return null;
  }

  public static Iterator<InputQuery> buildIteratorFromQueryStr(String queryStr) {
    InputQuery inputQuery = new InputQuery(queryStr, "inline query");
    return (new ArrayList<>(Arrays.asList(inputQuery))).iterator();
  }

  public static Iterator<InputQuery> buildIteratorFromFilePath(String filePath) {
    return new InputQueryIterable(new ArrayList<>(Arrays.asList(filePath)));
  }

  public static Iterator<InputQuery> buildIteratorFromFolderPath(String folderPath) {
    List<String> fileList = Stream.of(new File(folderPath).listFiles())
        .filter(file -> file.isFile())
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
    return new InputQueryIterable(fileList);
  }
}
