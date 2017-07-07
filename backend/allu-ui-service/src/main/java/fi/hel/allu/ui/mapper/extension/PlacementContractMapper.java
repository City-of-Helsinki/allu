package fi.hel.allu.ui.mapper.extension;

import fi.hel.allu.model.domain.PlacementContract;
import fi.hel.allu.servicecore.domain.PlacementContractJson;

public class PlacementContractMapper {
  public static PlacementContractJson modelToJson(PlacementContract placementContract) {
    PlacementContractJson placementContractJson = new PlacementContractJson();
    placementContractJson.setDiaryNumber(placementContract.getDiaryNumber());
    placementContractJson.setAdditionalInfo(placementContract.getAdditionalInfo());
    placementContractJson.setGeneralTerms(placementContract.getGeneralTerms());
    return ApplicationExtensionMapper.modelToJson(placementContract, placementContractJson);
  }

  public static PlacementContract jsonToModel(PlacementContractJson json) {
    PlacementContract placementContract = new PlacementContract();
    placementContract.setDiaryNumber(json.getDiaryNumber());
    placementContract.setAdditionalInfo(json.getAdditionalInfo());
    placementContract.setGeneralTerms(json.getGeneralTerms());
    return ApplicationExtensionMapper.jsonToModel(json, placementContract);
  }
}