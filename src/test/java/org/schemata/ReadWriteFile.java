package org.schemata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;


public class ReadWriteFile {

  @Test
  public void testFiles()
      throws IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    String path = resourceDirectory.toFile().getAbsolutePath() + "/github/entities.proto";


    RandomAccessFile raf = new RandomAccessFile(path, "rw");
    raf.seek(0);
    String line = raf.readLine();
    raf.close();
    System.out.println(line);


  }

}
