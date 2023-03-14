package com.pso.bigquery.optimization;

import org.apache.commons.cli.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BQAntiPatternCMDParser {
    public static String getInputQuery(String[] args){

//        public static String getInputQuery(String[] args)throws java.text.ParseException, IOException{

        String queryStr = "\n" +
                "SELECT " +
                "   t1.*, " +
                "   t2.col2 " +
                "FROM \n" +
                "  (SELECT * FROM `project.dataset.table1`) t1\n" +
                "LEFT JOIN\n" +
                "  `project.dataset.table2` t2\n ON t1.col1 = t2.col2";;

        Options options = new Options();

        Option query = Option.builder("query")
                .argName("query")
                .hasArg()
                .required(false)
                .desc("set query").build();
        options.addOption(query);

        Option filePath = Option.builder("filePath")
                .argName("filePath")
                .hasArg()
                .required(false)
                .desc("set file path").build();
        options.addOption(filePath);

        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if(cmd.hasOption("query")){
                queryStr = cmd.getOptionValue("query");
                System.out.println("query detected as " + queryStr);
            }
            else if(cmd.hasOption("filePath")){
                String filePathStr = cmd.getOptionValue("filePath");
                Path file_name = Path.of(filePathStr);
                queryStr = Files.readString(file_name);
                System.out.println("file path detected as " + filePathStr);
            }
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }
        return queryStr;
    }


}
