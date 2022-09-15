package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.supervision.api.domain.CommentCreateJson;
import io.swagger.v3.oas.annotations.Operation;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Add new comment for an application with given ID.",
            description = "User is allowed to add comments with following types:"
                    + "<ul>"
                    + " <li>INTERNAL</li>"
                    + " <li>INVOICING</li>"
                    + " <li>EXTERNAL_SYSTEM</li>"
                    + "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully",
                    content = @Content(schema = @Schema(implementation = CommentJson.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/applications/{id}/comments", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CommentJson> addApplicationComment(@PathVariable Integer id,
                                                             @RequestBody @Valid CommentCreateJson comment) {
        return ResponseEntity.ok(
                commentService.addApplicationComment(id, new CommentJson(comment.getType(), comment.getText())));
    }

    @Operation(summary = "Add new comment for an project with given ID.",
            description = "User is allowed to add comments with following types:"
                    + "<ul>"
                    + " <li>INTERNAL</li>"
                    + " <li>INVOICING</li>"
                    + " <li>EXTERNAL_SYSTEM</li>"
                    + "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully",
                    content = @Content(schema = @Schema(implementation = CommentJson.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping(value = "/projects/{id}/comments", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CommentJson> addProjectComment(@PathVariable Integer id,
                                                         @RequestBody @Valid CommentCreateJson comment) {
        return ResponseEntity.ok(
                commentService.addProjectComment(id, new CommentJson(comment.getType(), comment.getText())));
    }

    @Operation(summary = "Get all comments for application with given ID. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully",
                    content = @Content(schema = @Schema(implementation = CommentJson.class)))
    })
    @GetMapping(value = "/applications/{id}/comments", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<CommentJson>> findByApplication(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.findByApplicationId(id));
    }

    @Operation(summary = "Get all comments for project with given ID. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully",
                    content = @Content(schema = @Schema(implementation = CommentJson.class)))
    })
    @GetMapping(value = "/projects/{id}/comments", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<CommentJson>> findByProject(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.findByProjectId(id));
    }

    @Operation(summary = "Get comment by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment fetched successfully",
                    content = @Content(schema = @Schema(implementation = CommentJson.class))),
            @ApiResponse(responseCode = "404", description = "Comment with given ID not found",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/comments/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<CommentJson> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.mapToJson(commentService.findById(id)));
    }

    @Operation(summary = "Remove comment with given ID.",
            description = "User is allowed to remove <b>own</b> comments with following types:"
                    + "<ul>"
                    + " <li>INTERNAL</li>"
                    + " <li>INVOICING</li>"
                    + " <li>EXTERNAL_SYSTEM</li>"
                    + "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment type",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @DeleteMapping(value = "/comments/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.validateIsOwnedByCurrentUser(id);
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update comment with given ID.",
            description = "User is allowed to update only <b>own</b> comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment type",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping(value = "/comments/{id}")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<CommentJson> updateComment(@PathVariable Integer id, @RequestBody String commentText) {
        commentService.validateIsOwnedByCurrentUser(id);
        CommentJson updatedComment = commentService.updateComment(id, commentText);
        return ResponseEntity.ok(updatedComment);
    }
}
