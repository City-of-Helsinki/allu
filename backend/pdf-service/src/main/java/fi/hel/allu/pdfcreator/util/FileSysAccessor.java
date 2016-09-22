package fi.hel.allu.pdfcreator.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

/**
 * Wrapper class for selected static methods in java.nio, to make mocking
 * easier.
 */
@Component
public class FileSysAccessor {

  public boolean exists(Path path) {
    return Files.exists(path);
  }

  public byte[] readAllBytes(Path path) throws IOException {
    return Files.readAllBytes(path);
  }

  /**
   * Delete all non-null paths if possible.
   *
   * @param paths
   *          the paths to delete
   * @throws IOException
   */
  public void deleteIfExist(Path... paths) throws IOException {
    for (Path path : paths) {
      if (path != null) {
        Files.deleteIfExists(path);
      }
    }
  }

  public Path createTempFile(Path dir, String prefix, String suffix) throws IOException {
    return Files.createTempFile(dir, prefix, suffix);
  }

  public InputStream newInputStream(Path path) throws IOException {
    return Files.newInputStream(path);
  }

  public Path write(Path path, byte[] bytes) throws IOException {
    // TODO Auto-generated method stub
    return Files.write(path, bytes);
  }
}
