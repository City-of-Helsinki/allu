package fi.hel.allu.pdfcreator.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import fi.hel.allu.pdfcreator.config.ApplicationProperties;

/**
 * Component that's responsible for creating / emptying the application's
 * temporary directory at startup.
 */
@Component
public class TempDirCleaner implements ApplicationListener<ApplicationReadyEvent> {

  private ApplicationProperties applicationProperties;

  @Autowired
  public TempDirCleaner(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    Path tempDir = Paths.get(applicationProperties.getTempDir());
    try {
      setupTempDir(tempDir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void setupTempDir(Path tempDir) throws IOException {
    if (!Files.exists(tempDir)) {
      Files.createDirectories(tempDir);
    } else {
      cleanUpDirectory(tempDir);
    }
  }

  private void cleanUpDirectory(Path dir) throws IOException {
    Files.list(dir).forEach((p) -> {
      try {
        if (Files.isDirectory(p)) {
          cleanUpDirectory(p);
        }
        Files.delete(p);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
  }
}
