package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.AreaRentalService;
import fi.hel.allu.servicecore.service.ExcavationAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/arearentals")
public class AreaRentalController {

  @Autowired
  private AreaRentalService areaRentalService;

  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> reportWorkFinished(@PathVariable Integer id,
      @RequestBody @NotNull ZonedDateTime workFinishedDate) {
    return ResponseEntity.ok(areaRentalService.reportWorkFinished(id, workFinishedDate));
  }
}
