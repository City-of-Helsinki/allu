package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.CommentJson;
import fi.hel.allu.ui.service.CommentService;

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
@RequestMapping("/comments")
public class CommentController {

  private CommentService commentService;

  @Autowired
  CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  /**
   * Find comments by application ID
   *
   * @param applicationId
   *          the application ID
   * @return list of comments for the application
   */
  @RequestMapping(value = "/applications/{applicationId}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CommentJson>> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(commentService.findByApplicationId(applicationId), HttpStatus.OK);
  }

  /**
   * Create new comment for an application
   *
   * @param applicationId
   *          the application ID
   * @param commentJson
   *          The comment data
   * @return The created comment
   */
  @RequestMapping(value = "/applications/{applicationId}", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<CommentJson> insert(@PathVariable int applicationId,
      @Valid @RequestBody(required = true) CommentJson commentJson) {
    return new ResponseEntity<>(commentService.addComment(applicationId, commentJson), HttpStatus.OK);
  }

  /**
   * Update existing comment
   *
   * @param id
   *          comment's ID
   * @param commentJson
   *          comment's data
   * @return the updated comment
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<CommentJson> update(@PathVariable int id,
      @Valid @RequestBody(required = true) CommentJson commentJson) {
    return new ResponseEntity<>(commentService.updateComment(id, commentJson), HttpStatus.OK);
  }

  /**
   * Delete a comment
   *
   * @param id
   *          comment's ID
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    commentService.deleteComment(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
