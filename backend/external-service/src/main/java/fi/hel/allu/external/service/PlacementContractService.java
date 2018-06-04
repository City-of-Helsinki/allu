package fi.hel.allu.external.service;

import org.springframework.stereotype.Service;

import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.servicecore.domain.ApplicationJson;

@Service
public class PlacementContractService extends ApplicationServiceExt<PlacementContractExt> {

  @Override
  public ApplicationJson getApplicationJson(PlacementContractExt placementContract) {
    return ApplicationFactory.fromPlacementContractExt(placementContract, getExternalUserId());
  }
}
