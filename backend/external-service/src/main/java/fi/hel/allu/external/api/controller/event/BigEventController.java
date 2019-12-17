package fi.hel.allu.external.api.controller.event;

import fi.hel.allu.external.api.controller.BaseApplicationController;
import fi.hel.allu.external.domain.BigEventExt;
import fi.hel.allu.external.mapper.event.BigEventExtMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/bigevents")
@Api(tags = "Events")
public class BigEventController extends BaseApplicationController<BigEventExt, BigEventExtMapper> {

  @Autowired
  private BigEventExtMapper eventMapper;

  @Override
  protected BigEventExtMapper getMapper() {
    return eventMapper;
  }
}
