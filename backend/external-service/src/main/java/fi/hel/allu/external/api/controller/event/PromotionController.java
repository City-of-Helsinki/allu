package fi.hel.allu.external.api.controller.event;

import fi.hel.allu.external.api.controller.BaseApplicationController;
import fi.hel.allu.external.domain.PromotionExt;
import fi.hel.allu.external.mapper.event.PromotionExtMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/promotions")
@Api(tags = "Events")
public class PromotionController extends BaseApplicationController<PromotionExt, PromotionExtMapper> {

  @Autowired
  private PromotionExtMapper eventMapper;

  @Override
  protected PromotionExtMapper getMapper() {
    return eventMapper;
  }
}
