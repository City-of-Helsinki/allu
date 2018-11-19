package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.CommentExt;
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
    return new ResponseEntity<>(commentService.addComment(id, comment), HttpStatus.OK);
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



}
