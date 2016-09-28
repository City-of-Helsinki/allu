package fi.hel.allu.pdfcreator.controller;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.pdfcreator.service.PdfService;

@RestController
public class PdfController {

  PdfService pdfService;

  @Autowired
  public PdfController(PdfService pdfService) {
    this.pdfService = pdfService;
  }

  /**
   * Generate PDF from the given data
   *
   * @param stylesheet
   *          name of the stylesheet to use
   * @param contents
   *          the data to put in the PDF - a JSON object
   * @return The attachment's data
   * @throws IOException
   * @throws TransformerException
   */
  @RequestMapping(value = "/generate", method = RequestMethod.POST)
  public ResponseEntity<byte[]> generatePdf(@RequestParam String stylesheet, @RequestBody String contents)
      throws IOException, TransformerException {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
      return new ResponseEntity<>(pdfService.generatePdf(contents, stylesheet), httpHeaders, HttpStatus.OK);
  }

}
