package com.pso.bigquery.optimization.antipatterns.cmd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import com.google.cloud.storage.*;
import java.util.ArrayList;
import java.util.Arrays;


public class InputQueryIterable implements Iterator<InputQuery> {

  Iterator<String> filePathIterator;

  public InputQueryIterable(List<String> filePathList) {
    this.filePathIterator = filePathList.iterator();
  }

  @Override
  public boolean hasNext() {
    return filePathIterator.hasNext();
  }

  @Override
  public InputQuery next() {
    String filePathStr = filePathIterator.next();
    Path fileName = Path.of(filePathStr);
    try {
      if (filePathStr.startsWith("gs://")){
          String trimFilePathStr = filePathStr.replace("gs://", "");
          List<String> list = new ArrayList(Arrays.asList(trimFilePathStr.split("/")));
          String bucket = list.get(0);
          list.remove(0);
          String filename = String.join("/", list);
          Storage storage = StorageOptions.newBuilder()
                  .build()
                  .getService();
          Blob blob = storage.get(bucket, filename);
          String fileContent = new String(blob.getContent());
        return new InputQuery(fileContent, filePathStr);}
      else{
      return new InputQuery(Files.readString(fileName), filePathStr);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
