package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.PlacementContract;
import fi.hel.allu.servicecore.domain.PlacementContractJson;

public class PlacementContractMapper {
  public static PlacementContractJson modelToJson(PlacementContract placementContract) {
    PlacementContractJson placementContractJson = new PlacementContractJson();
    placementContractJson.setPropertyIdentificationNumber(placementContract.getPropertyIdentificationNumber());
    placementContractJson.setAdditionalInfo(placementContract.getAdditionalInfo());
    placementContractJson.setContractText(placementContract.getContractText());
    placementContractJson.setTerminationDate(placementContract.getTerminationDate());
    placementContractJson.setSectionNumber(placementContract.getSectionNumber());
    placementContractJson.setRationale(placementContract.getRationale());
    return ApplicationExtensionMapper.modelToJson(placementContract, placementContractJson);
  }

  public static PlacementContract jsonToModel(PlacementContractJson json) {
    PlacementContract placementContract = new PlacementContract();
    placementContract.setPropertyIdentificationNumber(json.getPropertyIdentificationNumber());
    placementContract.setAdditionalInfo(json.getAdditionalInfo());
    placementContract.setContractText(json.getContractText());
    placementContract.setTerminationDate(json.getTerminationDate());
    placementContract.setSectionNumber(json.getSectionNumber());
    placementContract.setRationale(json.getRationale());
    return ApplicationExtensionMapper.jsonToModel(json, placementContract);
  }
}
