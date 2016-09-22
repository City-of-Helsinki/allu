package fi.hel.allu.pdfcreator.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

  // filename suffixes for header and footer
  private static final String HEADER_SUFFIX = "-header";
  private static final String FOOTER_SUFFIX = "-footer";

  // wkhtmltopdf command line arguments to specify header and footer:
  private static final String HEADER_ARG = "--header-html";
  private static final String FOOTER_ARG = "--footer-html";

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

  public byte[] generatePdf(String dataJson, String stylesheet)
      throws IOException, JSONException, TransformerException {
    Path contentPath = null;
    Path headerPath = null;
    Path footerPath = null;
    Path pdfPath = null;
    try {
      contentPath = writeHtml(dataJson, stylesheet);
      if (Files.exists(stylesheetDir.resolve(stylesheet + HEADER_SUFFIX + ".xsl"))) {
        headerPath = writeHtml(dataJson, stylesheet + HEADER_SUFFIX);
      }
      if (Files.exists(stylesheetDir.resolve(stylesheet + FOOTER_SUFFIX + ".xsl"))) {
        footerPath = writeHtml(dataJson, stylesheet + FOOTER_SUFFIX);
      }
      pdfPath = writePdf(contentPath, footerPath, headerPath);
      return Files.readAllBytes(pdfPath);
    } finally {
      if (contentPath != null) {
        Files.deleteIfExists(contentPath);
      }
      if (headerPath != null) {
        Files.deleteIfExists(headerPath);
      }
      if (footerPath != null) {
        Files.deleteIfExists(footerPath);
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

  private Path writePdf(Path contentPath, Path headerPath, Path footerPath) throws IOException {
    Path pdfPath = Files.createTempFile(tempDir, "output-", ".pdf");
    final CommandLine cmdLine = new CommandLine(applicationProperties.getPdfGenerator());
    cmdLine.addArgument(contentPath.toString());
    if (headerPath != null) {
      cmdLine.addArgument(HEADER_ARG);
      cmdLine.addArgument(headerPath.toString());
    }
    if (footerPath != null) {
      cmdLine.addArgument(FOOTER_ARG);
      cmdLine.addArgument(footerPath.toString());
    }
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

}
