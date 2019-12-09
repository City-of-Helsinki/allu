package fi.hel.allu.external.api.controller;

import fi.hel.allu.external.domain.TrafficArrangementExt;
import fi.hel.allu.external.mapper.TrafficArrangementExtMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/trafficarrangements")
@Api(tags = "Traffic arrangements")
public class TrafficArrangementController extends BaseApplicationController<TrafficArrangementExt, TrafficArrangementExtMapper> {

  @Autowired
  private TrafficArrangementExtMapper trafficArrangementMapper;

  @Override
  protected TrafficArrangementExtMapper getMapper() {
    return trafficArrangementMapper;
  }
}
