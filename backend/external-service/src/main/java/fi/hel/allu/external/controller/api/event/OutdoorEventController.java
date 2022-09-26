package fi.hel.allu.external.controller.api.event;

import fi.hel.allu.external.controller.api.BaseApplicationController;
import fi.hel.allu.external.domain.OutdoorEventExt;
import fi.hel.allu.external.mapper.event.OutdoorEventExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.Validators;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/outdoorevents")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Events")
public class OutdoorEventController extends BaseApplicationController<OutdoorEventExt, OutdoorEventExtMapper> {

    private final OutdoorEventExtMapper eventMapper;

    public OutdoorEventController(ApplicationServiceExt applicationService, Validators validators,
                                  DecisionService decisionService, TerminationService terminationService,
                                  OutdoorEventExtMapper eventMapper) {
        super(applicationService, decisionService, validators, terminationService);
        this.eventMapper = eventMapper;
    }

    @Override
    protected OutdoorEventExtMapper getMapper() {
        return eventMapper;
    }
}
