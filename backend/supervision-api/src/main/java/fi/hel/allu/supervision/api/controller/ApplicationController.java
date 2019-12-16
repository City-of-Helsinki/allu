package fi.hel.allu.supervision.api.controller;


import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.supervision.api.domain.ApplicationSearchParameters;
import fi.hel.allu.supervision.api.domain.ApplicationSearchResult;
import fi.hel.allu.supervision.api.domain.BaseApplication;
import fi.hel.allu.supervision.api.mapper.ApplicationSearchParameterMapper;
import fi.hel.allu.supervision.api.mapper.ApplicationSearchResultMapper;
import fi.hel.allu.supervision.api.mapper.MapperUtil;
import io.swagger.annotations.*;

/**
 * Common operations for all application types
 */
@RestController
@RequestMapping("/v1")
@Api(tags = "Applications")
public class ApplicationController {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @ApiOperation(value = "Update application owner.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application owner updated successfully"),
  })
  @RequestMapping(value = "/applications/{id}/owner", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> updateOwner(@PathVariable Integer id,
      @ApiParam(value = "Id of the new owner") @RequestParam Integer ownerId) {
    applicationServiceComposer.updateApplicationOwner(ownerId, Collections.singletonList(id), true);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Remove application owner.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application owner removed successfully"),
  })
  @RequestMapping(value = "/applications/{id}/owner", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> removeOwner(@PathVariable Integer id) {
    applicationServiceComposer.removeApplicationOwner(Collections.singletonList(id), true);
    return ResponseEntity.ok().build();
  }
}
