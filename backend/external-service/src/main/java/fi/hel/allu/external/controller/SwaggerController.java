package fi.hel.allu.external.controller;

import fi.hel.allu.common.util.ResourceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller for providing API documentation to Swagger-UI.
 */
@RestController
@RequestMapping("/api-docs")
public class SwaggerController {

  @RequestMapping(value = "/swagger.json", method = RequestMethod.GET)
  public ResponseEntity<String> docs() {
    try {
      String json = ResourceUtil.readClassPathResource("/swagger/swagger.json");
      return new ResponseEntity<>(json, HttpStatus.OK);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
