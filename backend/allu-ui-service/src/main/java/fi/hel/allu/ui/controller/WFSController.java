package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.service.WFSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@RestController
@RequestMapping("/wfs")
public class WFSController {

  @Autowired
  private WFSService wfsService;


  @RequestMapping(value = "/user-areas", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION')")
  public Future<ResponseEntity<String>> getUserGeometries() {
    return wfsService.getUserAreas();
  }
}
