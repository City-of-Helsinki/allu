package fi.hel.allu.external.controller.api.event;

import fi.hel.allu.external.controller.api.BaseApplicationController;
import fi.hel.allu.external.domain.BigEventExt;
import fi.hel.allu.external.mapper.event.BigEventExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.Validators;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/bigevents")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Events")
public class BigEventController extends BaseApplicationController<BigEventExt, BigEventExtMapper> {


    private final BigEventExtMapper eventMapper;

    public BigEventController(ApplicationServiceExt applicationService, Validators validators,
                              DecisionService decisionService, TerminationService terminationService,
                              BigEventExtMapper eventMapper) {
        super(applicationService, decisionService, validators, terminationService);
        this.eventMapper = eventMapper;
    }

    @Override
    protected BigEventExtMapper getMapper() {
        return eventMapper;
    }
}
