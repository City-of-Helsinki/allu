package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.external.mapper.PlacementContractExtMapper;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/placementcontracts")
@Api(value = "v1/placementcontracts")
public class PlacementContractController extends BaseApplicationController<PlacementContractExt, PlacementContractExtMapper>{

  @Autowired
  private PlacementContractExtMapper placementContractMapper;

  @Override
  protected PlacementContractExtMapper getMapper() {
    return placementContractMapper;
  }



}
