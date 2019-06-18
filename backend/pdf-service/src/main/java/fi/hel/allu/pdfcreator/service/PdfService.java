package fi.hel.allu.pdfcreator.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.transform.TransformerException;

import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.pdfcreator.config.ApplicationProperties;
import fi.hel.allu.pdfcreator.util.Executioner;
import fi.hel.allu.pdfcreator.util.FileSysAccessor;
import fi.hel.allu.pdfcreator.util.JsonConverter;

@Service
public class PdfService {

  private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

  private static final String COMMON_STYLESHEET = "COMMON";
  private static final String STYLESHEET_SEPARATOR = "-";

  // filename suffixes for header and footer
  private static final String HEADER_SUFFIX = "-header";
  private static final String FOOTER_SUFFIX = "-footer";

  // wkhtmltopdf command line arguments to specify header and footer:
  private static final String HEADER_ARG = "--header-html";
  private static final String FOOTER_ARG = "--footer-html";

  private final ApplicationProperties applicationProperties;
  private final FileSysAccessor fileSysAccessor;
  private final Executioner executioner;
  private final JsonConverter jsonConverter;

  private final Path tempDir;
  private final Path stylesheetDir;

  @Autowired
  public PdfService(ApplicationProperties applicationProperties, FileSysAccessor fileSysAccessor,
      Executioner executioner, JsonConverter jsonConverter) throws IOException {
    logger.debug("PdfService starting");
    this.applicationProperties = applicationProperties;
    this.fileSysAccessor = fileSysAccessor;
    this.executioner = executioner;
    this.jsonConverter = jsonConverter;
    tempDir = Paths.get(applicationProperties.getTempDir());
    stylesheetDir = Paths.get(applicationProperties.getStylesheetDir()).toAbsolutePath();
  }

  public byte[] generatePdf(String dataJson, String stylesheet)
      throws IOException, TransformerException {
    Path contentPath = null;
    Path headerPath = null;
    Path footerPath = null;
    Path pdfPath = null;
    String xml = jsonConverter.jsonToXml(dataJson, stylesheetDir.toString() + "/");
    try {
      contentPath = writeHtml(xml, stylesheet);
      if (contentPath == null) {
        throw new NoSuchEntityException("Can't find the stylesheet '" + stylesheet + "'");
      }
      headerPath = writeHtml(xml, stylesheet, HEADER_SUFFIX);
      footerPath = writeHtml(xml, stylesheet, FOOTER_SUFFIX);
      pdfPath = writePdf(contentPath, headerPath, footerPath);
      return fileSysAccessor.readAllBytes(pdfPath);
    } finally {
      fileSysAccessor.deleteIfExist(contentPath, headerPath, footerPath, pdfPath);
    }
  }

  private Path writeHtml(String xml, String stylesheet, String suffix)  throws IOException, TransformerException {
    Path path = writeHtml(xml, stylesheet + suffix);
    if (path != null) {
      return path;
    } else {
      return writeHtml(xml, getFallbackStyleSheetName(stylesheet + suffix));
    }
  }

  private Path writeHtml(String xml, String stylesheet) throws IOException, TransformerException {
    // Check that stylesheet exists:
    Path xslPath = stylesheetDir.resolve(stylesheet + ".xsl");
    if (!fileSysAccessor.exists(xslPath)) {
      return null;
    }

    String html = jsonConverter.applyStylesheet(xml, fileSysAccessor.newInputStream(xslPath));

    // Write HTML to temporary file
    Path htmlPath = fileSysAccessor.createTempFile(tempDir, "pdfsource-", ".html");
    fileSysAccessor.write(htmlPath, html.getBytes(Charset.forName("UTF-8")));
    return htmlPath;
  }

  private Path writePdf(Path contentPath, Path headerPath, Path footerPath) throws IOException {
    Path pdfPath = fileSysAccessor.createTempFile(tempDir, "output-", ".pdf");
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
      executioner.execute(cmdLine);
    } catch (IOException e) {
      fileSysAccessor.deleteIfExist(pdfPath);
      throw e;
    }
    return pdfPath;
  }

  private String getFallbackStyleSheetName(String styleSheet) {
    String[] parts = styleSheet.split(STYLESHEET_SEPARATOR);
    parts[0] = COMMON_STYLESHEET;
    return String.join("-", parts);
  }
}
