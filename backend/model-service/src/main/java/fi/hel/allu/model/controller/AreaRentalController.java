package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/arearentals")
public class AreaRentalController {

  private final ApplicationService applicationService;

  @Autowired
  public AreaRentalController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  public ResponseEntity<Void> reportWorkFinished(@PathVariable Integer id, @RequestBody ZonedDateTime workFinishedDate) {
   applicationService.setWorkFinishedDate(id, ApplicationType.AREA_RENTAL, workFinishedDate);
   return new ResponseEntity<>(HttpStatus.OK);
  }
}
