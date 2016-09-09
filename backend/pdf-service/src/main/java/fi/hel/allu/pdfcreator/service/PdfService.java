package fi.hel.allu.pdfcreator.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.xml.transform.TransformerException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.pdfcreator.config.ApplicationProperties;
import fi.hel.allu.pdfcreator.util.JsonConverter;

@Service
public class PdfService {

  private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

  private ApplicationProperties applicationProperties;

  private final Path tempDir;
  private final Path stylesheetDir;

  @Autowired
  public PdfService(ApplicationProperties applicationProperties) throws IOException {
    logger.debug("PdfService starting");
    this.applicationProperties = applicationProperties;
    tempDir = Paths.get(applicationProperties.getTempDir());
    stylesheetDir = Paths.get(applicationProperties.getStylesheetDir()).toAbsolutePath();
  }

  @PostConstruct
  private void setupTempDir() throws IOException {
    if (!Files.exists(tempDir)) {
      Files.createDirectories(tempDir);
    } else {
      cleanUpDirectory(tempDir);
    }
  }

  public byte[] generatePdf(String dataJson, String stylesheet)
      throws IOException, JSONException, TransformerException {
    Path htmlPath = null;
    Path pdfPath = null;
    try {
      htmlPath = writeHtml(dataJson, stylesheet);
      pdfPath = writePdf(htmlPath);
      return Files.readAllBytes(pdfPath);
    } finally {
      if (htmlPath != null) {
        Files.deleteIfExists(htmlPath);
      }
      if (pdfPath != null) {
        Files.deleteIfExists(pdfPath);
      }
    }
  }

  private Path writeHtml(String dataJson, String stylesheet) throws IOException, JSONException, TransformerException {
    // Check that stylesheet exists:
    Path xslPath = stylesheetDir.resolve(stylesheet + ".xsl");
    if (!Files.exists(xslPath)) {
      throw new NoSuchEntityException("Can't find XML file " + xslPath.toString());
    }

    String xml = JsonConverter.jsonToXml(dataJson, stylesheetDir.toString() + "/");
    String html = JsonConverter.applyStylesheet(xml, Files.newInputStream(xslPath));

    // Write HTML to temporary file
    Path htmlPath = Files.createTempFile(tempDir, "pdfsource-", ".html");
    Files.write(htmlPath, html.getBytes());
    return htmlPath;
  }

  private Path writePdf(Path xmlPath) throws IOException {
    Path pdfPath = Files.createTempFile(tempDir, "output-", ".pdf");
    final CommandLine cmdLine = new CommandLine(applicationProperties.getPdfGenerator());
    cmdLine.addArgument(xmlPath.toString());
    cmdLine.addArgument(pdfPath.toString());
    try {
      final Executor executioner = new DefaultExecutor();
      executioner.execute(cmdLine);
    } catch (IOException e) {
      Files.deleteIfExists(pdfPath);
      throw e;
    }
    return pdfPath;
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
