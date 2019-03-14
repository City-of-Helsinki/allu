package fi.hel.allu.supervision.api.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(tags = "Application tags")
public class ApplicationTagController {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  private List<ApplicationTagType> ALLOWED_TAG_TYPES = Arrays.asList(
      ApplicationTagType.WAITING,
      ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED,
      ApplicationTagType.STATEMENT_REQUESTED,
      ApplicationTagType.COMPENSATION_CLARIFICATION,
      ApplicationTagType.PAYMENT_BASIS_CORRECTION
  );

  @ApiOperation(value = "Add new tag for an application with given ID. If application already has a tag with given type no new tag is added.",
      notes = "User is allowed to add following tags:"
      + "<ul>"
      + " <li>WAITING</li>"
      + " <li>ADDITIONAL_INFORMATION_REQUESTED</li>"
      + " <li>STATEMENT_REQUESTED</li>"
      + " <li>COMPENSATION_CLARIFICATION</li>"
      + " <li>PAYMENT_BASIS_CORRECTION</li>"
      + "</ul>",
      produces = "application/json",
      consumes = "application/json",
      response = ApplicationTagJson.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Tag added successfully", response = ApplicationTagJson.class),
      @ApiResponse(code = 400, message = "Invalid tag type", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{id}/tags", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ApplicationTagJson> addTag(@PathVariable Integer id, @RequestBody ApplicationTagType tagType) {
    validateTagType(tagType);
    return ResponseEntity.ok(applicationServiceComposer.addTag(id, new ApplicationTagJson(null, tagType, null)));
  }

  @ApiOperation(value = "Remove tag from an application with given ID.",
      notes = "User is allowed to remove following tags:"
      + "<ul>"
      + " <li>WAITING</li>"
      + " <li>ADDITIONAL_INFORMATION_REQUESTED</li>"
      + " <li>STATEMENT_REQUESTED</li>"
      + " <li>COMPENSATION_CLARIFICATION</li>"
      + " <li>PAYMENT_BASIS_CORRECTION</li>"
      + " <li>PAYMENT_BASIS_CORRECTION</li>"
      + "</ul>",
      produces = "application/json",
      consumes = "application/json",
      response = ApplicationTagJson.class,
      authorizations = @Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Tag removed successfully"),
      @ApiResponse(code = 400, message = "Invalid tag type", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{id}/tags", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteTag(@PathVariable Integer id, @RequestBody ApplicationTagType tagType) {
    validateTagType(tagType);
    applicationServiceComposer.removeTag(id, tagType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private void validateTagType(ApplicationTagType tagType) {
    if (!ALLOWED_TAG_TYPES.contains(tagType)) {
      throw new IllegalArgumentException("applicationTag.type.invalid");
    }
  }
}
