package fi.hel.allu.external.api.controller;

import java.util.List;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping({"/v1/applications", "/v2/applications"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comments")
public class CommentController {

  private final CommentServiceExt commentService;

  private final ApplicationServiceExt applicationService;

  public CommentController(CommentServiceExt commentService, ApplicationServiceExt applicationService) {
    this.commentService = commentService;
    this.applicationService = applicationService;
  }

  @Operation(summary = "Adds comment for application. Returns ID of the created comment")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Comment added successfully", content = @Content(schema =
      @Schema(implementation = Integer.class))),
      @ApiResponse(responseCode = "400", description = "Invalid comment", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)))
  })
  @PostMapping(value = "/{id}/comments", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> addComment(@Parameter(description = "Id of the application to add comment for.") @PathVariable Integer id,
                                            @Parameter(description = "Comments to add") @RequestBody @Valid CommentExt comment) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return new ResponseEntity<>(commentService.addComment(applicationId, comment), HttpStatus.OK);
  }

  @Operation(summary = "Removes given comment from application")
  @DeleteMapping(value = "/comments/{id}", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> deleteComment(@Parameter(description = "Comment id to delete") @PathVariable Integer id) {
    commentService.deleteComment(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Gets comments sent by Allu handler to client system")
  @GetMapping(value = "/{id}/comments/received", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<CommentOutExt>> getReceivedComments(@Parameter(description = "Application id to get comments for") @PathVariable Integer id) {
    return getComments(id, CommentType.TO_EXTERNAL_SYSTEM);
  }

  @Operation(summary = "Gets own comments sent from client system to Allu handler")
  @GetMapping(value = "/{id}/comments/sent", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<CommentOutExt>> getSentComments(@Parameter(description = "Application id to get comments for") @PathVariable Integer id) {
    return getComments(id, CommentType.EXTERNAL_SYSTEM);
  }

  private ResponseEntity<List<CommentOutExt>> getComments(Integer externalApplicationId, CommentType type) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(externalApplicationId);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(commentService.getComments(applicationId, type));
  }
}
