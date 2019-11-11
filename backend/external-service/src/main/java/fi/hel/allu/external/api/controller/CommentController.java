package fi.hel.allu.external.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.external.domain.CommentExt;
import fi.hel.allu.external.domain.CommentOutExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.service.CommentServiceExt;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(value = "v1/applications")
public class CommentController {

  @Autowired
  private CommentServiceExt commentService;

  @Autowired
  private ApplicationServiceExt applicationService;

  @ApiOperation(value = "Adds comment for application. Returns ID of the created comment",
      produces = "application/json",
      consumes = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Comment added successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid comment", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> addComment(@ApiParam(value = "Id of the application to add comment for.") @PathVariable Integer id,
                                            @ApiParam(value = "Comments to add") @RequestBody @Valid CommentExt comment) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return new ResponseEntity<>(commentService.addComment(applicationId, comment), HttpStatus.OK);
  }

  @ApiOperation(value = "Removes given comment from application",
      produces = "application/json",
      consumes = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> deleteComment(@ApiParam(value = "Comment id to delete") @PathVariable Integer id) {
    commentService.deleteComment(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Gets comments sent by Allu handler to client system",
      produces = "application/json",
      consumes = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/{id}/comments/received", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<CommentOutExt>> getReceivedComments(@ApiParam(value = "Application id to get comments for") @PathVariable Integer id) {
    return getComments(id, CommentType.TO_EXTERNAL_SYSTEM);
  }

  @ApiOperation(value = "Gets own comments sent from client system to Allu handler",
      produces = "application/json",
      consumes = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/{id}/comments/sent", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<CommentOutExt>> getSentComments(@ApiParam(value = "Application id to get comments for") @PathVariable Integer id) {
    return getComments(id, CommentType.EXTERNAL_SYSTEM);
  }

  private ResponseEntity<List<CommentOutExt>> getComments(Integer externalApplicationId, CommentType type) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(externalApplicationId);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(commentService.getComments(applicationId, type));
  }
}
