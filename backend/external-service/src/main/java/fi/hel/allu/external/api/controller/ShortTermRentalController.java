package fi.hel.allu.external.api.controller;

import fi.hel.allu.common.exception.ErrorInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.ShortTermRentalExt;
import fi.hel.allu.external.mapper.ShortTermRentalExtMapper;
import fi.hel.allu.external.validation.ShortTermRentalExtValidator;

@RestController
@RequestMapping({"/v1/shorttermrentals", "/v2/shorttermrentals"})
@Api(tags = "Short term rentals")
public class ShortTermRentalController extends BaseApplicationController<ShortTermRentalExt, ShortTermRentalExtMapper> {

  @Autowired
  private ShortTermRentalExtMapper shortTermRentalMapper;

  @Autowired
  private ShortTermRentalExtValidator validator;

  @Override
  protected void addApplicationTypeSpecificValidators(WebDataBinder binder) {
    binder.addValidators(validator);
  }

  @Override
  protected ShortTermRentalExtMapper getMapper() {
    return shortTermRentalMapper;
  }

  @ApiOperation(value = "Gets termination document for application with given ID",
    authorizations = @Authorization(value ="api_key"),
    response = byte.class,
    responseContainer = "Array")
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Termination document retrieved successfully", response = byte.class, responseContainer = "Array"),
    @ApiResponse(code = 404, message = "No termination document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/termination", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getTermination(@PathVariable Integer id) {
    return getTerminationDocument(id);
  }
}
