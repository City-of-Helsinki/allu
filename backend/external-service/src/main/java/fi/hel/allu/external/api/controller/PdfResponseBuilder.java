package fi.hel.allu.external.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class PdfResponseBuilder {

  public static ResponseEntity<byte[]> createResponseEntity(byte[] bytes) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }
}
