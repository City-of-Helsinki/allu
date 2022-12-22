package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ExcavationAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excavationannouncements")
public class ExcavationAnnouncementController {

  @Autowired
  private ExcavationAnnouncementService excavationAnnouncementService;

  @PutMapping(value = "/{id}/requiredtasks")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> setRequiredTasks(@PathVariable Integer id, @RequestBody RequiredTasks tasks) {
    return ResponseEntity.ok(excavationAnnouncementService.setRequiredTasks(id, tasks));
  }
}