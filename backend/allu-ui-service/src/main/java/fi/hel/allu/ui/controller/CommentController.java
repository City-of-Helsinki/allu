package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

/**
 * Controller for managing comments.
 */
@RestController
public class CommentController {

  private CommentService commentService;

  @Autowired
  CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @RequestMapping(value = "/applications/{applicationId}/comments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CommentJson>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(commentService.findByApplicationId(applicationId), HttpStatus.OK);
  }

  @RequestMapping(value = "/projects/{projectId}/comments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CommentJson>> findByProjectId(@PathVariable int projectId) {
    return new ResponseEntity<>(commentService.findByProjectId(projectId), HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{applicationId}/comments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CommentJson> insertApplicationComment(@PathVariable int applicationId,
      @Valid @RequestBody(required = true) CommentJson commentJson) {
    return new ResponseEntity<>(commentService.addApplicationComment(applicationId, commentJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/projects/{projectId}/comments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CommentJson> insertProjectComment(@PathVariable int projectId,
      @Valid @RequestBody(required = true) CommentJson commentJson) {
    return new ResponseEntity<>(commentService.addProjectComment(projectId, commentJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/comments/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CommentJson> update(@PathVariable int id,
      @Valid @RequestBody(required = true) CommentJson commentJson) {
    return new ResponseEntity<>(commentService.updateComment(id, commentJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    commentService.deleteComment(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
