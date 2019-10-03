package fi.hel.allu.external.mapper;

import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.servicecore.domain.PlacementContractJson;

@Component
public class PlacementContractExtMapper extends ApplicationExtMapper<PlacementContractExt> {

  @Override
  protected PlacementContractJson createExtension(PlacementContractExt placementContract) {
    PlacementContractJson extension = new PlacementContractJson();
    extension.setPropertyIdentificationNumber(placementContract.getPropertyIdentificationNumber());
    extension.setAdditionalInfo(placementContract.getWorkDescription());
    return extension;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.PLACEMENT_CONTRACT;
  }

  @Override
  protected String getClientApplicationKind(PlacementContractExt placementContract) {
    return placementContract.getClientApplicationKind();
  }
}
