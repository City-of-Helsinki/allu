package fi.hel.allu.pdfcreator.config;

import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private String tempDir;
  private String stylesheetDir;
  private String pdfGenerator;

  @Autowired
  public ApplicationProperties(@Value("${pdf.tempdir}") @NotEmpty String tempDir,
      @Value("${pdf.generator}") @NotEmpty String pdfGenerator,
      @Value("${pdf.stylesheetdir}") @NotEmpty String stylesheetDir) {
    this.tempDir = tempDir;
    this.pdfGenerator = pdfGenerator;
    this.stylesheetDir = stylesheetDir;
  }

  /**
   * Get the work directory (will be emptied on startup, so should be private).
   *
   * @return the tempDir
   */
  public String getTempDir() {
    return tempDir;
  }

  /**
   * Get the directory for the style sheets.
   *
   * @return the stylesheetDir
   */
  public String getStylesheetDir() {
    return stylesheetDir;
  }

  /**
   * Get the full path to the pdf generator binary.
   *
   * @return the pdfGenerator
   */
  public String getPdfGenerator() {
    return pdfGenerator;
  }

}
