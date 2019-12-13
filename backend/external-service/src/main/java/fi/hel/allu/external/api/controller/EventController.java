package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.EventExt;
import fi.hel.allu.external.mapper.EventExtMapper;
import io.swagger.annotations.Api;

@RestController
@RequestMapping({"/v1/events", "/v2/events"})
@Api(tags = "Events")
public class EventController extends BaseApplicationController<EventExt, EventExtMapper>{

  @Autowired
  private EventExtMapper eventMapper;

  @Override
  protected EventExtMapper getMapper() {
    return eventMapper;
  }



}
