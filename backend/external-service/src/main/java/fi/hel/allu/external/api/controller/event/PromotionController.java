package fi.hel.allu.external.api.controller.event;

import fi.hel.allu.external.api.controller.BaseApplicationController;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.external.mapper.event.PromotionExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.Validators;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/promotions")
@Tag(name = "Events")
public class PromotionController extends BaseApplicationController<PromotionExt, PromotionExtMapper> {

  private final PromotionExtMapper eventMapper;

  public PromotionController(ApplicationServiceExt applicationService,
                             Validators validators,
                             DecisionService decisionService,
                             TerminationService terminationService,
                             PromotionExtMapper eventMapper) {
    super(applicationService, decisionService, validators, terminationService);
    this.eventMapper = eventMapper;
  }

  @Override
  protected PromotionExtMapper getMapper() {
    return eventMapper;
  }
}
