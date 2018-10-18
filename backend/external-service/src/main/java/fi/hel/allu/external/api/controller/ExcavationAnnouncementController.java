package fi.hel.allu.external.api.controller;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.external.domain.ValidityPeriodExt;
import fi.hel.allu.external.mapper.ExcavationAnnouncementExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.servicecore.service.ExcavationAnnouncementService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/excavationannouncements")
@Api(value = "v1/excavationannouncements")
public class ExcavationAnnouncementController
    extends BaseApplicationController<ExcavationAnnouncementExt, ExcavationAnnouncementExtMapper> {

  @Autowired
  private ExcavationAnnouncementExtMapper mapper;

  @Autowired
  private ExcavationAnnouncementService excavationAnnouncementService;

  @Autowired
  private ApplicationServiceExt applicationService;

  @Override
  protected ExcavationAnnouncementExtMapper getMapper() {
    return mapper;
  }

  @ApiOperation(value = "Report work finished date for excavation announcement specified by ID parameter.",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Date reported successfully", response = Void.class),
  })
  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportWorkFinished(
      @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
      @ApiParam(value = "Work finished date") @RequestBody @NotNull ZonedDateTime workFinishedDate) {
    applicationService.validateOwnedByExternalUser(id);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), workFinishedDate, null);
    excavationAnnouncementService.reportCustomerWorkFinished(id, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Report operational condition date for excavation announcement specified by ID parameter.",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Date reported successfully", response = Void.class),
  })
  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportOperationalCondition(
      @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
      @ApiParam(value = "Operational condition date") @RequestBody @NotNull ZonedDateTime operationalConditionDate) {
    applicationService.validateOwnedByExternalUser(id);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), operationalConditionDate, null);
    excavationAnnouncementService.reportCustomerOperationalCondition(id, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Report change in application validity period",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Validity period change reported successfully", response = Void.class),
  })
  @RequestMapping(value = "/{id}/validityperiod", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportValidityPeriod(
      @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
      @ApiParam(value = "Work finished date") @RequestBody @Valid ValidityPeriodExt validityPeriod) {
    applicationService.validateOwnedByExternalUser(id);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(),
        validityPeriod.getValidityPeriodStart(), validityPeriod.getValidityPeriodEnd());
    excavationAnnouncementService.reportCustomerValidity(id, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
