package fi.hel.allu.external.api.controller.event;

import fi.hel.allu.external.api.controller.BaseApplicationController;
import fi.hel.allu.external.domain.BigEventExt;
import fi.hel.allu.external.domain.OutdoorEventExt;
import fi.hel.allu.external.mapper.event.BigEventExtMapper;
import fi.hel.allu.external.mapper.event.OutdoorEventExtMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/outdoorevents")
@Api(tags = "Events")
public class OutdoorEventController extends BaseApplicationController<OutdoorEventExt, OutdoorEventExtMapper> {

  @Autowired
  private OutdoorEventExtMapper eventMapper;

  @Override
  protected OutdoorEventExtMapper getMapper() {
    return eventMapper;
  }
}
