package fi.hel.allu.external.controller.maintenance;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.servicecore.service.ApplicationArchiverService;

/**
 * Public interface for managing applications.
 */
@RestController
@RequestMapping("/v1/applications")
@Tag(name = "Applicaton arvhieve", description = "Used only for maintanence")
public class ApplicationArchiveController {

  @Autowired
  ApplicationArchiverService applicationArchiverService;

  @PatchMapping(value = "/finished/status")
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> updateStatusForFinishedApplications() {
    applicationArchiverService.updateStatusForFinishedApplications();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PatchMapping(value = "/terminated/status")
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> updateStatusForTerminatedApplications() {
    applicationArchiverService.updateStatusForTerminatedApplications();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PatchMapping(value = "/finished/archive")
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> archiveFinishedApplications(@RequestBody List<Integer> applicationIds) {
    applicationArchiverService.archiveApplicationsIfNecessary(applicationIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
