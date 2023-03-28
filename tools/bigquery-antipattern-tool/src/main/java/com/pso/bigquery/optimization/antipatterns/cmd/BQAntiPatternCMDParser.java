package com.pso.bigquery.optimization.antipatterns.cmd;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.cli.*;
import com.google.api.gax.paging.Page;

public class BQAntiPatternCMDParser {

  public static final String QUERY = "query";
  public static final String FILE_PATH = "file_path";
  public static final String FOLDER_PATH = "folder_path";

  public static Iterator<InputQuery> getInputQueries(String[] args) {
    Options options = new Options();

    Option query =
        Option.builder(QUERY).argName(QUERY).hasArg().required(false).desc("set query").build();
    options.addOption(query);

    Option filePath =
        Option.builder(FILE_PATH)
            .argName(FILE_PATH)
            .hasArg()
            .required(false)
            .desc("set file path")
            .build();
    options.addOption(filePath);

    Option folderPath =
        Option.builder(FOLDER_PATH)
            .argName(FOLDER_PATH)
            .hasArg()
            .required(false)
            .desc("set file path")
            .build();
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
    if (folderPath.startsWith("gs://")){
      Storage storage = StorageOptions.newBuilder().build().getService();
      String trimFolderPathStr = folderPath.replace("gs://", "");
      List<String> list = new ArrayList(Arrays.asList(trimFolderPathStr.split("/")));
      String bucket = list.get(0);
      list.remove(0);
      String directoryPrefix = String.join("/", list) + "/";
      Page<Blob> blobs =
              storage.list(
                      bucket,
                      Storage.BlobListOption.prefix(directoryPrefix),
                      Storage.BlobListOption.currentDirectory());
      ArrayList gcsFileList = new ArrayList();
      for (Blob blob : blobs.iterateAll()) {
        String blobName = blob.getName();
        if (blobName.equals(directoryPrefix)){
          continue;
        }
        gcsFileList.add("gs://" + bucket + "/" + blobName);
      }
      return new InputQueryIterable(gcsFileList);
    }
    else{
    List<String> fileList =
        Stream.of(new File(folderPath).listFiles())
            .filter(file -> file.isFile())
            .map(File::getAbsolutePath)
            .collect(Collectors.toList());
    System.out.println(fileList);
    return new InputQueryIterable(fileList);
    }
  }
}
