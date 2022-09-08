package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.NoteExt;
import fi.hel.allu.external.mapper.NoteExtMapper;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;

@RestController
@RequestMapping({"/v2/notes"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notes")
public class NoteController {

  @Autowired
  protected ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  protected ApplicationExtGeometryValidator geometryValidator;

  @Autowired
  private NoteExtMapper noteMapper;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(geometryValidator);
  }

  @Operation(summary = "Adds new note and returns ID of the created note. Applicant of note is set according to Allu configuration (default contact).")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Note added successfully",
              content = @Content(schema = @Schema(implementation = Integer.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data",
              content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<Integer> create(@Parameter(description = "Application data", required = true)
                                        @Valid @RequestBody NoteExt noteExt) {
    Integer id = applicationServiceComposer.createApplication(noteMapper.mapExtNote(noteExt)).getId();
    return ResponseEntity.ok(id);
  }

  @Operation(summary = "Delete note with given ID.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Note deleted successfully")
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    applicationServiceComposer.deleteNote(id);
    return ResponseEntity.ok().build();
  }
}
