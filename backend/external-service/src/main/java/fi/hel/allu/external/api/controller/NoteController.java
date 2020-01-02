package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

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
import io.swagger.annotations.*;

@RestController
@RequestMapping({"/v2/notes"})
@Api(tags = "Notes")
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

  @ApiOperation(value = "Adds new note and returns ID of the created note. Applicant of note is set according to Allu configuration (default contact).",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Note added successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid input data", response = ErrorInfo.class)
  })
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<Integer> create(@ApiParam(value = "Application data", required = true)
                                        @Valid @RequestBody NoteExt noteExt) {
    Integer id = applicationServiceComposer.createApplication(noteMapper.mapExtNote(noteExt)).getId();
    return ResponseEntity.ok(id);
  }

  @ApiOperation(value = "Delete note with given ID.",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Note deleted successfully")
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL')")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    applicationServiceComposer.deleteNote(id);
    return ResponseEntity.ok().build();
  }
}
