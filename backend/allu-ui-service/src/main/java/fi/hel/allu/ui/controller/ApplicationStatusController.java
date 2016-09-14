package fi.hel.allu.ui.controller;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.ui.domain.CommentJson;
import fi.hel.allu.ui.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class ApplicationStatusController {
    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/{id}/status/cancelled", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<Void> changeStatusToCancelled(@PathVariable int id) {
        applicationService.changeStatus(id, StatusType.CANCELLED);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/pending", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<Void> changeStatusToPending(@PathVariable int id) {
        applicationService.changeStatus(id, StatusType.PENDING);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/handling", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<Void> changeStatusToHandling(@PathVariable int id) {
        applicationService.changeStatus(id, StatusType.HANDLING);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/decisionmaking", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<Void> changeStatusToDecisionMaking(@PathVariable int id) {
        applicationService.changeStatus(id, StatusType.DECISIONMAKING);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/decision", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_DECISION')")
    public ResponseEntity<Void> changeStatusToDecision(@PathVariable int id) {
        applicationService.changeStatus(id, StatusType.DECISION);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/rejected", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_DECISION')")
    public ResponseEntity<Void> changeStatusToRejected(@PathVariable int id, @RequestBody CommentJson comment) {
        // TODO: add comment handling
        applicationService.changeStatus(id, StatusType.REJECTED);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/toPreparation", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_DECISION')")
    public ResponseEntity<Void> changeStatusToReturnedToPreparation(@PathVariable int id, @RequestBody CommentJson comment) {
        // TODO: add comment handling
        applicationService.changeStatus(id, StatusType.RETURNED_TO_PREPARATION);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/finished", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<Void> changeStatusToFinished(@PathVariable int id) {
        applicationService.changeStatus(id, StatusType.FINISHED);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
