package fi.hel.allu.supervision.api.controller;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.*;
import fi.hel.allu.supervision.api.translation.EnumTranslator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1/codes")
@Api(tags = "Codes")
public class CodeController {

  @Autowired
  private EnumTranslator enumTranslator;

  @ApiOperation(value = "Gets map containing application type codes with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationtypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationType, String>> getApplicationTypes() {
    return ResponseEntity.ok(getTranslations(ApplicationType.values()));
  }

  @ApiOperation(value = "Gets map containing application status codes with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationstatustypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<StatusType, String>> getApplicationStatusTypes() {
    return ResponseEntity.ok(getTranslations(StatusType.values()));
  }

  @ApiOperation(value = "Gets map containing application kinds with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationkinds", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationKind, String>> getApplicationKinds() {
    return ResponseEntity.ok(getTranslations(ApplicationKind.values()));
  }

  @ApiOperation(value = "Gets map containing application specifiers with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationspecifiers", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationSpecifier, String>> getApplicationSpecifiers() {
    return ResponseEntity.ok(getTranslations(ApplicationSpecifier.values()));
  }

  @ApiOperation(value = "Gets map containing supervision task types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/supervisiontasktypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<SupervisionTaskType, String>> getSupervisionTaskTypes() {
    return ResponseEntity.ok(getTranslations(SupervisionTaskType.values()));
  }

  @ApiOperation(value = "Gets map containing supervision task status types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/supervisiontaskstatustypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<SupervisionTaskStatusType, String>> getSupervisionTaskStatusTypes() {
    return ResponseEntity.ok(getTranslations(SupervisionTaskStatusType.values()));
  }

  @ApiOperation(value = "Gets map containing comment types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/commenttypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<CommentType, String>> getCommentTypes() {
    return ResponseEntity.ok(getTranslations(CommentType.values()));
  }

  @ApiOperation(value = "Gets map containing customer types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/customertypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<CustomerType, String>> getCustomerTypes() {
    return ResponseEntity.ok(getTranslations(CustomerType.values()));
  }

  @ApiOperation(value = "Gets map containing customer role types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/customerroletypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<CustomerRoleType, String>> getCustomerRoleTypes() {
    return ResponseEntity.ok(getTranslations(CustomerRoleType.values()));
  }

  @ApiOperation(value = "Gets map containing attachment types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/attachmenttypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<AttachmentType, String>> getAttachmentTypes() {
    return ResponseEntity.ok(getTranslations(AttachmentType.values()));
  }

  @ApiOperation(value = "Gets map containing decision distribution types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/distributiontypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<DistributionType, String>> getDistributionTypes() {
    return ResponseEntity.ok(getTranslations(DistributionType.values()));
  }

  @ApiOperation(value = "Gets map containing decision publicity types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/publicitytypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<PublicityType, String>> getPublicityTypes() {
    return ResponseEntity.ok(getTranslations(PublicityType.values()));
  }

  @ApiOperation(value = "Gets map containing traffic arrangement impediment types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/trafficarrangementimpedimenttypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<TrafficArrangementImpedimentType, String>> getTrafficArrangementImpedimentTypes() {
    return ResponseEntity.ok(getTranslations(TrafficArrangementImpedimentType.values()));
  }

  @ApiOperation(value = "Gets map containing application tag types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationtagtypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationTagType, String>> getApplicationTagTypes() {
    return ResponseEntity.ok(getTranslations(ApplicationTagType.values()));
  }

  @ApiOperation(value = "Gets cable info types",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/cableinfotypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<DefaultTextType, String>> getCableInfoTypes() {
    return ResponseEntity.ok(getTranslations(DefaultTextType.getCableInfoTypes()));
  }

  @ApiOperation(value = "Gets event natures",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/eventnatures", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<EventNature, String>> getEventNatures() {
    return ResponseEntity.ok(getTranslations(EventNature.values()));
  }

  @ApiOperation(value = "Gets charge basis types",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/chargebasistypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ChargeBasisType, String>> getChargeBasisTypes() {
    return ResponseEntity.ok(getTranslations(ChargeBasisType.values()));
  }

  @ApiOperation(value = "Gets charge basis units",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/chargebasisunits", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ChargeBasisUnit, String>> getChargeBasisUnits() {
    return ResponseEntity.ok(getTranslations(ChargeBasisUnit.values()));
  }

  @ApiOperation(value = "Gets surface hardness types",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/surfacehardnesstypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<SurfaceHardness, String>> getSurfaceHardnessTypes() {
    return ResponseEntity.ok(getTranslations(SurfaceHardness.values()));
  }

  private <T extends Enum<T>> Map<T, String> getTranslations(T[] values) {
    return Stream.of(values)
        .collect(Collectors.toMap(a -> a, a -> enumTranslator.getTranslation(a)));
  }

}
