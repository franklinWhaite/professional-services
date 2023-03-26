package com.pso.bigquery.optimization.antipatterns.cmd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


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
      System.out.println(fileName);
//      example: gs://anti_pattern_sql/query_file1.sql
      String bucketName = "anti_pattern_sql";
      String blobName = "query_file1.sql";
      Storage storage = StorageOptions.getDefaultInstance().getService();
      BlobId blobId = BlobId.of(bucketName, blobName);
      Blob blob = storage.get(blobId);
      long size = blob.getSize(); // no RPC call is required
      byte[] content = blob.getContent(); // one or multiple RPC calls will be issued;
      String contentString = Base64.getEncoder().encodeToString(content);
      return new InputQuery(contentString, filePathStr);}

      else{
      return new InputQuery(Files.readString(fileName), filePathStr);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
