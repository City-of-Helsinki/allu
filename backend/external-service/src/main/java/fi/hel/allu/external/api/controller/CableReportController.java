package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.CableReportExt;
import fi.hel.allu.external.mapper.CableReportExtMapper;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/cablereports")
@Api(value = "v1/cablereports")
public class CableReportController extends BaseApplicationController<CableReportExt, CableReportExtMapper>{

  @Autowired
  private CableReportExtMapper mapper;

  @Override
  protected CableReportExtMapper getMapper() {
    return mapper;
  }

}
