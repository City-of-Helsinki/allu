package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.*;
import fi.hel.allu.supervision.api.domain.CodeMetadata;
import fi.hel.allu.supervision.api.domain.CodeType;
import fi.hel.allu.supervision.api.translation.EnumTranslator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/v1/codes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Codes")
public class CodeController {

    private final EnumTranslator enumTranslator;

    public CodeController(EnumTranslator enumTranslator) {
        this.enumTranslator = enumTranslator;
    }

    @Operation(summary = "Gets map containing application type codes with descriptions ")
    @GetMapping(value = "/applicationtypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<ApplicationType, CodeMetadata>> getApplicationTypes() {
        return ResponseEntity.ok(getCodeMetadata(ApplicationType.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets map containing application status codes with descriptions ")
    @GetMapping(value = "/applicationstatustypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<StatusType, CodeMetadata>> getApplicationStatusTypes() {
        return ResponseEntity.ok(getCodeMetadata(StatusType.values(), a -> CodeType.SYSTEM));
    }

    @Operation(summary = "Gets map containing application kinds with descriptions ")
    @GetMapping(value = "/applicationkinds", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<ApplicationKind, CodeMetadata>> getApplicationKinds(
            @Parameter(description = "Optional application type parameter. If given, only kinds allowed for given " +
                    "application type are returned")
            @RequestParam(required = false) ApplicationType applicationType) {
        ApplicationKind[] kinds = Optional.ofNullable(applicationType)
                .map(this::getKindsForApplicationType)
                .orElse(ApplicationKind.values());
        return ResponseEntity.ok(getCodeMetadata(kinds, a -> CodeType.USER));
    }

    private ApplicationKind[] getKindsForApplicationType(ApplicationType type) {
        return ApplicationKind.forApplicationType(type).toArray(new ApplicationKind[0]);
    }

    @Operation(summary = "Gets map containing application specifiers with descriptions ")
    @GetMapping(value = "/applicationspecifiers", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<ApplicationSpecifier, CodeMetadata>> getApplicationSpecifiers(
            @Parameter(
                    description = "Optional application kind parameter. If given, only specifiers allowed for given" +
                            " kind are returned")
            @RequestParam(required = false) ApplicationKind applicationKind) {
        ApplicationSpecifier[] specifiers = Optional.ofNullable(applicationKind)
                .map(this::getSpecifiersForKind)
                .orElse(ApplicationSpecifier.values());
        return ResponseEntity.ok(getCodeMetadata(specifiers, a -> CodeType.USER));
    }

    private ApplicationSpecifier[] getSpecifiersForKind(ApplicationKind kind) {
        return ApplicationSpecifier.forApplicationKind(kind).toArray(new ApplicationSpecifier[0]);
    }

    @Operation(summary = "Gets map containing supervision task types with descriptions ")
    @GetMapping(value = "/supervisiontasktypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<SupervisionTaskType, CodeMetadata>> getSupervisionTaskTypes() {
        return ResponseEntity.ok(getCodeMetadata(SupervisionTaskType.values(),
                                                 a -> a.isManuallyAdded() ? CodeType.USER : CodeType.SYSTEM));
    }

    @Operation(summary = "Gets map containing supervision task status types with descriptions ")
    @GetMapping(value = "/supervisiontaskstatustypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<SupervisionTaskStatusType, CodeMetadata>> getSupervisionTaskStatusTypes() {
        return ResponseEntity.ok(getCodeMetadata(SupervisionTaskStatusType.values(), a -> CodeType.SYSTEM));
    }

    @Operation(summary = "Gets map containing comment types with descriptions ")
    @GetMapping(value = "/commenttypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<CommentType, CodeMetadata>> getCommentTypes() {
        return ResponseEntity.ok(
                getCodeMetadata(CommentType.values(), a -> a.isManuallyAdded() ? CodeType.USER : CodeType.SYSTEM));
    }

    @Operation(summary = "Gets map containing customer types with descriptions ")
    @GetMapping(value = "/customertypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<CustomerType, CodeMetadata>> getCustomerTypes() {
        return ResponseEntity.ok(getCodeMetadata(CustomerType.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets map containing customer role types with descriptions ")
    @GetMapping(value = "/customerroletypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<CustomerRoleType, CodeMetadata>> getCustomerRoleTypes() {
        return ResponseEntity.ok(getCodeMetadata(CustomerRoleType.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets map containing attachment types with descriptions ")
    @GetMapping(value = "/attachmenttypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<AttachmentType, CodeMetadata>> getAttachmentTypes() {
        return ResponseEntity.ok(getCodeMetadata(AttachmentType.values(),
                                                 a -> a.isDefaultAttachment() ? CodeType.SYSTEM : CodeType.USER));
    }

    @Operation(summary = "Gets map containing decision distribution types with descriptions ")
    @GetMapping(value = "/distributiontypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<DistributionType, CodeMetadata>> getDistributionTypes() {
        return ResponseEntity.ok(getCodeMetadata(DistributionType.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets map containing decision publicity types with descriptions ")
    @GetMapping(value = "/publicitytypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<PublicityType, CodeMetadata>> getPublicityTypes() {
        return ResponseEntity.ok(getCodeMetadata(PublicityType.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets map containing traffic arrangement impediment types with descriptions ")
    @GetMapping(value = "/trafficarrangementimpedimenttypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<TrafficArrangementImpedimentType, CodeMetadata>> getTrafficArrangementImpedimentTypes() {
        return ResponseEntity.ok(getCodeMetadata(TrafficArrangementImpedimentType.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets map containing application tag types with descriptions ")
    @GetMapping(value = "/applicationtagtypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<ApplicationTagType, CodeMetadata>> getApplicationTagTypes() {
        return ResponseEntity.ok(getCodeMetadata(ApplicationTagType.values(),
                                                 a -> a.isManuallyAdded() ? CodeType.USER : CodeType.SYSTEM));
    }

    @Operation(summary = "Gets cable info types")
    @GetMapping(value = "/cableinfotypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<DefaultTextType, CodeMetadata>> getCableInfoTypes() {
        return ResponseEntity.ok(getCodeMetadata(DefaultTextType.getCableInfoTypes(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets event natures")
    @GetMapping(value = "/eventnatures", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<EventNature, CodeMetadata>> getEventNatures() {
        return ResponseEntity.ok(getCodeMetadata(EventNature.values(),
                                                 a -> a == EventNature.PROMOTION ? CodeType.SYSTEM : CodeType.USER));
    }

    @Operation(summary = "Gets charge basis types")
    @GetMapping(value = "/chargebasistypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<ChargeBasisType, CodeMetadata>> getChargeBasisTypes() {
        return ResponseEntity.ok(getCodeMetadata(ChargeBasisType.values(),
                                                 a -> a == ChargeBasisType.CALCULATED ? CodeType.SYSTEM :
                                                         CodeType.USER));
    }

    @Operation(summary = "Gets charge basis units")
    @GetMapping(value = "/chargebasisunits", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<ChargeBasisUnit, CodeMetadata>> getChargeBasisUnits() {
        return ResponseEntity.ok(getCodeMetadata(ChargeBasisUnit.values(), a -> CodeType.USER));
    }

    @Operation(summary = "Gets surface hardness types")
    @GetMapping(value = "/surfacehardnesstypes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<Map<SurfaceHardness, CodeMetadata>> getSurfaceHardnessTypes() {
        return ResponseEntity.ok(getCodeMetadata(SurfaceHardness.values(), a -> CodeType.USER));
    }

    private <T extends Enum<T>> Map<T, CodeMetadata> getCodeMetadata(T[] values, Function<T, CodeType> typeFunction) {
        return Stream.of(values)
                .collect(Collectors.toMap(a -> a, a -> new CodeMetadata(enumTranslator.getTranslation(a),
                                                                        typeFunction.apply(a))));
    }

}
