package fi.hel.allu.pdfcreator.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.pdfcreator.config.ApplicationProperties;
import fi.hel.allu.pdfcreator.util.Executioner;
import fi.hel.allu.pdfcreator.util.FileSysAccessor;
import fi.hel.allu.pdfcreator.util.JsonConverter;

import static org.junit.Assert.fail;

public class PdfServiceTest {

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private FileSysAccessor fileSysAccessor;
  @Mock
  private Executioner executioner;
  @Mock
  private JsonConverter jsonConverter;

  private PdfService pdfService;

  private final static String TEMPDIR = "TEMPDIR";
  private final static String STYLESHEETDIR = "STYLESHEETDIR";
  private final static String PDFGENERATOR = "PDFGENERATOR";
  private final static String STYLESHEET = "STYLESHEET";
  private final static Path TEMPFILE = Paths.get("TEMPFILE");
  private final static String DUMMY_JSON = "{\"foo\": \"bar\"}";
  private final static String COMMON_STYLESHEET = "COMMON";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getTempDir()).thenReturn(TEMPDIR);
    Mockito.when(applicationProperties.getStylesheetDir()).thenReturn(STYLESHEETDIR);
    Mockito.when(applicationProperties.getPdfGenerator()).thenReturn(PDFGENERATOR);
    Mockito.when(jsonConverter.applyStylesheet(Mockito.nullable(String.class), Mockito.nullable(InputStream.class))).thenReturn(STYLESHEET);
    Mockito.when(fileSysAccessor.createTempFile(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(TEMPFILE);

    pdfService = new PdfService(applicationProperties, fileSysAccessor, executioner, jsonConverter);
  }

  @Test
  public void testBareContent() throws IOException, TransformerException {
    testFallbackDocument("stylesheet", "stylesheet.xsl");
  }

  @Test(expected = NoSuchEntityException.class)
  public void testInvalidStylesheet() throws IOException, TransformerException {
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).thenReturn(false);

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");
    // Shouldn't ever get here:
    fail("Expected exception not thrown!");
  }

  @Test
  public void testWithHeader() throws IOException, TransformerException {
    // Test creating a pdf from stylesheet that also has header:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgument(0);
      return arg.endsWith("stylesheet.xsl") || arg.endsWith("stylesheet-header.xsl");
    });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    // Stylesheet should have been applied twice now:
    Mockito.verify(jsonConverter, Mockito.times(2)).applyStylesheet(Mockito.any(), Mockito.any());
  }

  @Test
  public void testWithFooter() throws IOException, TransformerException {
    // Test creating a pdf from stylesheet that also has a footer:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgument(0);
      return arg.endsWith("stylesheet.xsl") || arg.endsWith("stylesheet-footer.xsl");
    });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    // Stylesheet should have been applied twice now:
    Mockito.verify(jsonConverter, Mockito.times(2)).applyStylesheet(Mockito.any(), Mockito.any());
  }

  @Test
  public void testWithHeaderAndFooter() throws IOException, TransformerException {
    // Test creating a pdf from stylesheet that has both header and footer:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgument(0);
      return arg.endsWith("stylesheet.xsl") || arg.endsWith("stylesheet-header.xsl")
          || arg.endsWith("stylesheet-footer.xsl");
    });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    // Stylesheet should have been applied twice now:
    Mockito.verify(jsonConverter, Mockito.times(3)).applyStylesheet(Mockito.any(), Mockito.any());
  }

  @Test
  public void testFirstFallbackDocument() throws IOException, TransformerException {
    testFallbackDocument("stylesheet-suffix1-suffix2", COMMON_STYLESHEET + "-suffix1-suffix2.xsl");
  }

  @Test
  public void testLastResortFallbackDocument() throws IOException, TransformerException {
    testFallbackDocument("stylesheet-suffix1-suffix2", COMMON_STYLESHEET + "-suffix2.xsl");
  }

  private void testFallbackDocument(String documentName, String expectedFallback) throws IOException, TransformerException {
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgument(0);
      if (arg.endsWith(expectedFallback)) {
        return true;
      } else {
        return false;
      }
    });

    pdfService.generatePdf(DUMMY_JSON, documentName);

    // Only one call should have been made:
    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    Mockito.verify(jsonConverter).applyStylesheet(Mockito.any(), Mockito.any());
  }
}
