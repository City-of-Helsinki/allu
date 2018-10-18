package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.external.mapper.ExcavationAnnouncementExtMapper;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/excavationannouncements")
@Api(value = "v1/excavationannouncements")
public class ExcavationAnnouncementController
    extends BaseApplicationController<ExcavationAnnouncementExt, ExcavationAnnouncementExtMapper> {

  @Autowired
  private ExcavationAnnouncementExtMapper mapper;

  @Override
  protected ExcavationAnnouncementExtMapper getMapper() {
    return mapper;
  }

}
