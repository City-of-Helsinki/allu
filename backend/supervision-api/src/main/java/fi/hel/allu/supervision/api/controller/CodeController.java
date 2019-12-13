package fi.hel.allu.supervision.api.controller;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.*;
import fi.hel.allu.supervision.api.domain.CodeMetadata;
import fi.hel.allu.supervision.api.domain.CodeType;
import fi.hel.allu.supervision.api.translation.EnumTranslator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
  public ResponseEntity<Map<ApplicationType, CodeMetadata>> getApplicationTypes() {
    return ResponseEntity.ok(getCodeMetadata(ApplicationType.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing application status codes with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationstatustypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<StatusType, CodeMetadata>> getApplicationStatusTypes() {
    return ResponseEntity.ok(getCodeMetadata(StatusType.values(), a -> CodeType.SYSTEM));
  }

  @ApiOperation(value = "Gets map containing application kinds with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationkinds", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationKind, CodeMetadata>> getApplicationKinds(
      @ApiParam(value = "Optional application type parameter. If given, only kinds allowed for given application type are returned")
      @RequestParam(required = false) ApplicationType applicationType) {
    ApplicationKind[] kinds = Optional.ofNullable(applicationType)
        .map(t -> getKindsForApplicationType(t))
        .orElse(ApplicationKind.values());
    return ResponseEntity.ok(getCodeMetadata(kinds, a -> CodeType.USER));
  }

  private ApplicationKind[] getKindsForApplicationType(ApplicationType type) {
    return ApplicationKind.forApplicationType(type).toArray(new ApplicationKind[0]);
  }

  @ApiOperation(value = "Gets map containing application specifiers with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationspecifiers", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationSpecifier, CodeMetadata>> getApplicationSpecifiers(
      @ApiParam(value = "Optional application kind parameter. If given, only specifiers allowed for given kind are returned")
      @RequestParam(required = false) ApplicationKind applicationKind) {
    ApplicationSpecifier[] specifiers = Optional.ofNullable(applicationKind)
        .map(k -> getSpecifiersForKind(k))
        .orElse(ApplicationSpecifier.values());
    return ResponseEntity.ok(getCodeMetadata(specifiers, a -> CodeType.USER));
  }

  private ApplicationSpecifier[] getSpecifiersForKind(ApplicationKind kind) {
    return ApplicationSpecifier.forApplicationKind(kind).toArray(new ApplicationSpecifier[0]);
  }

  @ApiOperation(value = "Gets map containing supervision task types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/supervisiontasktypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<SupervisionTaskType, CodeMetadata>> getSupervisionTaskTypes() {
    return ResponseEntity.ok(getCodeMetadata(SupervisionTaskType.values(), a -> a.isManuallyAdded() ? CodeType.USER : CodeType.SYSTEM));
  }

  @ApiOperation(value = "Gets map containing supervision task status types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/supervisiontaskstatustypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<SupervisionTaskStatusType, CodeMetadata>> getSupervisionTaskStatusTypes() {
    return ResponseEntity.ok(getCodeMetadata(SupervisionTaskStatusType.values(), a -> CodeType.SYSTEM));
  }

  @ApiOperation(value = "Gets map containing comment types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/commenttypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<CommentType, CodeMetadata>> getCommentTypes() {
    return ResponseEntity.ok(getCodeMetadata(CommentType.values(), a -> a.isManuallyAdded() ? CodeType.USER : CodeType.SYSTEM));
  }

  @ApiOperation(value = "Gets map containing customer types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/customertypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<CustomerType, CodeMetadata>> getCustomerTypes() {
    return ResponseEntity.ok(getCodeMetadata(CustomerType.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing customer role types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/customerroletypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<CustomerRoleType, CodeMetadata>> getCustomerRoleTypes() {
    return ResponseEntity.ok(getCodeMetadata(CustomerRoleType.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing attachment types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/attachmenttypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<AttachmentType, CodeMetadata>> getAttachmentTypes() {
    return ResponseEntity.ok(getCodeMetadata(AttachmentType.values(), a -> a.isDefaultAttachment() ? CodeType.SYSTEM: CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing decision distribution types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/distributiontypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<DistributionType, CodeMetadata>> getDistributionTypes() {
    return ResponseEntity.ok(getCodeMetadata(DistributionType.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing decision publicity types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/publicitytypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<PublicityType, CodeMetadata>> getPublicityTypes() {
    return ResponseEntity.ok(getCodeMetadata(PublicityType.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing traffic arrangement impediment types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/trafficarrangementimpedimenttypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<TrafficArrangementImpedimentType, CodeMetadata>> getTrafficArrangementImpedimentTypes() {
    return ResponseEntity.ok(getCodeMetadata(TrafficArrangementImpedimentType.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets map containing application tag types with descriptions ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/applicationtagtypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ApplicationTagType, CodeMetadata>> getApplicationTagTypes() {
    return ResponseEntity.ok(getCodeMetadata(ApplicationTagType.values(), a -> a.isManuallyAdded() ? CodeType.USER : CodeType.SYSTEM));
  }

  @ApiOperation(value = "Gets cable info types",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/cableinfotypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<DefaultTextType, CodeMetadata>> getCableInfoTypes() {
    return ResponseEntity.ok(getCodeMetadata(DefaultTextType.getCableInfoTypes(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets event natures",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/eventnatures", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<EventNature, CodeMetadata>> getEventNatures() {
    return ResponseEntity.ok(getCodeMetadata(EventNature.values(), a -> a == EventNature.PROMOTION ? CodeType.SYSTEM : CodeType.USER));
  }

  @ApiOperation(value = "Gets charge basis types",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/chargebasistypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ChargeBasisType, CodeMetadata>> getChargeBasisTypes() {
    return ResponseEntity.ok(getCodeMetadata(ChargeBasisType.values(), a -> a == ChargeBasisType.CALCULATED ? CodeType.SYSTEM : CodeType.USER));
  }

  @ApiOperation(value = "Gets charge basis units",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/chargebasisunits", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<ChargeBasisUnit, CodeMetadata>> getChargeBasisUnits() {
    return ResponseEntity.ok(getCodeMetadata(ChargeBasisUnit.values(), a -> CodeType.USER));
  }

  @ApiOperation(value = "Gets surface hardness types",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/surfacehardnesstypes", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Map<SurfaceHardness, CodeMetadata>> getSurfaceHardnessTypes() {
    return ResponseEntity.ok(getCodeMetadata(SurfaceHardness.values(), a -> CodeType.USER));
  }

  private <T extends Enum<T>> Map<T, CodeMetadata> getCodeMetadata(T[] values, Function<T, CodeType> typeFunction) {
    return Stream.of(values)
        .collect(Collectors.toMap(a -> a, a -> new CodeMetadata(enumTranslator.getTranslation(a), typeFunction.apply(a))));
  }

}
