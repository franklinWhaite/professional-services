package com.google.zetasql.toolkit.antipattern.cmd;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import com.opencsv.CSVReader;

public class InputCsvQueryIterator implements Iterator<InputQuery> {

  private Iterator<String[]> reader;

  public InputCsvQueryIterator(String csvPath) throws IOException {
    reader = (new CSVReader(new FileReader(csvPath))).iterator();
    // pop header
    reader.next();

  }

  @Override
  public boolean hasNext() {
    return reader.hasNext();
  }

  @Override
  public InputQuery next() {
    String[] next = reader.next();
    return new InputQuery(next[1].replace("\"\"", "\""), next[0]);
  }
}
