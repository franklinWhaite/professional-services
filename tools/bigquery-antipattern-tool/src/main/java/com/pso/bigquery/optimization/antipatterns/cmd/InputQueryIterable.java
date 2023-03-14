package com.pso.bigquery.optimization.antipatterns.cmd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

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
      return new InputQuery(Files.readString(fileName), filePathStr);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
