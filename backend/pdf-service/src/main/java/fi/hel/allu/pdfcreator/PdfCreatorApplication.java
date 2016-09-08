package fi.hel.allu.pdfcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "fi.hel.allu.pdfcreator", "fi.hel.allu.common.controller" })
public class PdfCreatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(PdfCreatorApplication.class, args);
  }

}
