package fi.hel.allu.ui.controller;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.ui.domain.ApplicationJson;
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
    public ResponseEntity<ApplicationJson> changeStatusToCancelled(@PathVariable int id) {
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.CANCELLED), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/pending", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<ApplicationJson> changeStatusToPending(@PathVariable int id) {
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.PENDING), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/handling", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<ApplicationJson> changeStatusToHandling(@PathVariable int id) {
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.HANDLING), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/decisionmaking", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<ApplicationJson> changeStatusToDecisionMaking(@PathVariable int id) {
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.DECISIONMAKING), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/decision", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_DECISION')")
    public ResponseEntity<ApplicationJson> changeStatusToDecision(@PathVariable int id) {
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.DECISION), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/rejected", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_DECISION')")
    public ResponseEntity<ApplicationJson> changeStatusToRejected(@PathVariable int id, @RequestBody CommentJson comment) {
        // TODO: add comment handling
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.REJECTED), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/toPreparation", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_DECISION')")
    public ResponseEntity<ApplicationJson> changeStatusToReturnedToPreparation(@PathVariable int id, @RequestBody CommentJson comment) {
        // TODO: add comment handling
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.RETURNED_TO_PREPARATION), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/status/finished", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
    public ResponseEntity<ApplicationJson> changeStatusToFinished(@PathVariable int id) {
        return new ResponseEntity<>(applicationService.changeStatus(id, StatusType.FINISHED), HttpStatus.OK);
    }
}
