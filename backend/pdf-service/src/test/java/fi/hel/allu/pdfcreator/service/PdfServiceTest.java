package fi.hel.allu.pdfcreator.service;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.json.JSONException;
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

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getTempDir()).thenReturn(TEMPDIR);
    Mockito.when(applicationProperties.getStylesheetDir()).thenReturn(STYLESHEETDIR);
    Mockito.when(applicationProperties.getPdfGenerator()).thenReturn(PDFGENERATOR);
    Mockito.when(jsonConverter.applyStylesheet(Mockito.anyString(), Mockito.any())).thenReturn(STYLESHEET);
    Mockito.when(fileSysAccessor.createTempFile(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(TEMPFILE);

    pdfService = new PdfService(applicationProperties, fileSysAccessor, executioner, jsonConverter);
  }

  @Test
  public void testBareContent() throws JSONException, IOException, TransformerException {
    // Test creating a pdf from stylesheet without header or footer:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgumentAt(0, Path.class);
      return arg.endsWith("stylesheet.xsl"); });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    // Only one call should have been made:
    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    Mockito.verify(jsonConverter).applyStylesheet(Mockito.any(), Mockito.any());
  }

  @Test(expected = NoSuchEntityException.class)
  public void testInvalidStylesheet() throws JSONException, IOException, TransformerException {
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).thenReturn(false);

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");
    // Shouldn't ever get here:
    fail("Expected exception not thrown!");
  }

  @Test
  public void testWithHeader() throws JSONException, IOException, TransformerException {
    // Test creating a pdf from stylesheet that also has header:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgumentAt(0, Path.class);
      return arg.endsWith("stylesheet.xsl") || arg.endsWith("stylesheet-header.xsl");
    });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    // Stylesheet should have been applied twice now:
    Mockito.verify(jsonConverter, Mockito.times(2)).applyStylesheet(Mockito.any(), Mockito.any());
  }

  @Test
  public void testWithFooter() throws JSONException, IOException, TransformerException {
    // Test creating a pdf from stylesheet that also has a footer:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgumentAt(0, Path.class);
      return arg.endsWith("stylesheet.xsl") || arg.endsWith("stylesheet-footer.xsl");
    });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    // Stylesheet should have been applied twice now:
    Mockito.verify(jsonConverter, Mockito.times(2)).applyStylesheet(Mockito.any(), Mockito.any());
  }

  @Test
  public void testWithHeaderAndFooter() throws JSONException, IOException, TransformerException {
    // Test creating a pdf from stylesheet that has both header and footer:
    Mockito.when(fileSysAccessor.exists(Mockito.any(Path.class))).then(invocation -> {
      Path arg = invocation.getArgumentAt(0, Path.class);
      return arg.endsWith("stylesheet.xsl") || arg.endsWith("stylesheet-header.xsl")
          || arg.endsWith("stylesheet-footer.xsl");
    });

    pdfService.generatePdf(DUMMY_JSON, "stylesheet");

    Mockito.verify(jsonConverter).jsonToXml(Mockito.eq(DUMMY_JSON), Mockito.any());
    // Stylesheet should have been applied twice now:
    Mockito.verify(jsonConverter, Mockito.times(3)).applyStylesheet(Mockito.any(), Mockito.any());
  }

}
