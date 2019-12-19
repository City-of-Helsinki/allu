package fi.hel.allu.supervision.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.supervision.api.domain.CommentCreateJson;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api(tags = "Comments")
public class CommentController {

  @Autowired
  private CommentService commentService;

  @ApiOperation(value = "Add new comment for an application with given ID.",
      notes = "User is allowed to add comments with following types:"
      + "<ul>"
      + " <li>INTERNAL</li>"
      + " <li>INVOICING</li>"
      + " <li>EXTERNAL_SYSTEM</li>"
      + "</ul>",
      produces = "application/json",
      consumes = "application/json",
      response = CommentJson.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comment added successfully", response = CommentJson.class),
      @ApiResponse(code = 400, message = "Invalid request data", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{id}/comments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CommentJson> addApplicationComment(@PathVariable Integer id, @RequestBody @Valid CommentCreateJson comment) {
    return ResponseEntity.ok(commentService.addApplicationComment(id, new CommentJson(comment.getType(), comment.getText())));
  }

  @ApiOperation(value = "Add new comment for an project with given ID.",
      notes = "User is allowed to add comments with following types:"
      + "<ul>"
      + " <li>INTERNAL</li>"
      + " <li>INVOICING</li>"
      + " <li>EXTERNAL_SYSTEM</li>"
      + "</ul>",
      produces = "application/json",
      consumes = "application/json",
      response = CommentJson.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comment added successfully", response = CommentJson.class),
      @ApiResponse(code = 400, message = "Invalid request data", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/projects/{id}/comments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CommentJson> addProjectComment(@PathVariable Integer id, @RequestBody @Valid CommentCreateJson comment) {
    return ResponseEntity.ok(commentService.addProjectComment(id, new CommentJson(comment.getType(), comment.getText())));
  }

  @ApiOperation(value = "Get all comments for application with given ID. ",
      produces = "application/json",
      response = CommentJson.class,
      responseContainer = "List",
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comments fetched successfully", response = CommentJson.class, responseContainer = "List")
  })
  @RequestMapping(value = "/applications/{id}/comments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<CommentJson>> findByApplication(@PathVariable Integer id) {
    return ResponseEntity.ok(commentService.findByApplicationId(id));
  }

  @ApiOperation(value = "Get all comments for project with given ID. ",
      produces = "application/json",
      response = CommentJson.class,
      responseContainer = "List",
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comments fetched successfully", response = CommentJson.class, responseContainer = "List")
  })
  @RequestMapping(value = "/projects/{id}/comments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<CommentJson>> findByProject(@PathVariable Integer id) {
    return ResponseEntity.ok(commentService.findByProjectId(id));
  }

  @ApiOperation(value = "Get comment by ID. ",
      produces = "application/json",
      response = CommentJson.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comment fetched successfully", response = CommentJson.class),
      @ApiResponse(code = 404, message = "Comment with given ID not found", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CommentJson> findById(@PathVariable Integer id) {
    return ResponseEntity.ok(commentService.mapToJson(commentService.findById(id)));
  }

  @ApiOperation(value = "Remove comment with given ID.",
      notes = "User is allowed to remove <b>own</b> comments with following types:"
      + "<ul>"
      + " <li>INTERNAL</li>"
      + " <li>INVOICING</li>"
      + " <li>EXTERNAL_SYSTEM</li>"
      + "</ul>",
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comment removed successfully"),
      @ApiResponse(code = 400, message = "Invalid comment type", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
    commentService.validateIsOwnedByCurrentUser(id);
    commentService.deleteComment(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Update comment with given ID.",
      notes = "User is allowed to update only <b>own</b> comments",
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comment updated successfully"),
      @ApiResponse(code = 400, message = "Invalid comment type", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<CommentJson> updateComment(@PathVariable Integer id, @RequestBody String commentText) {
    commentService.validateIsOwnedByCurrentUser(id);
    CommentJson updatedComment = commentService.updateComment(id, commentText);
    return ResponseEntity.ok(updatedComment);
  }
}
