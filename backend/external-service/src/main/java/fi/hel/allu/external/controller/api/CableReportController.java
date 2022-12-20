package fi.hel.allu.external.controller.api;

import fi.hel.allu.external.domain.CableReportExt;
import fi.hel.allu.external.mapper.CableReportExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.Validators;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v1/cablereports", "/v2/cablereports"})
@Tag(name = "Cable reports", description = "Cable report application API")
public class CableReportController extends BaseApplicationController<CableReportExt, CableReportExtMapper> {

    private final CableReportExtMapper mapper;

    public CableReportController(ApplicationServiceExt applicationService, Validators validators,
                                 DecisionService decisionService, TerminationService terminationService,
                                 CableReportExtMapper cableReportExtMapper) {
        super(applicationService, decisionService, validators, terminationService);
        this.mapper = cableReportExtMapper;
    }

    @Override
    protected CableReportExtMapper getMapper() {
        return mapper;
    }

}