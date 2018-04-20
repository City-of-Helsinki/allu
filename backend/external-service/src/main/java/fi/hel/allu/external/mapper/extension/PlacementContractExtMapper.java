package fi.hel.allu.external.mapper.extension;

import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.servicecore.domain.PlacementContractJson;

public class PlacementContractExtMapper {

  public static PlacementContractJson extToJson(PlacementContractExt placementContractExt) {
    PlacementContractJson placementContractJson = new PlacementContractJson();
    placementContractJson.setAdditionalInfo(placementContractExt.getAdditionalInfo());
    placementContractJson.setIdentificationNumber(placementContractExt.getIdentificationNumber());
    placementContractJson.setPropertyIdentificationNumber(placementContractExt.getPropertyIdentificationNumber());
    placementContractJson.setContractText(placementContractExt.getContractText());
    placementContractJson.setTerminationDate(placementContractExt.getTerminationDate());
    return ApplicationExtensionExtMapper.modelToJson(placementContractExt, placementContractJson);
  }

  public static PlacementContractExt jsonToExt(PlacementContractJson placementContractJson) {
    PlacementContractExt placementContractExt = new PlacementContractExt();
    placementContractExt.setAdditionalInfo(placementContractJson.getAdditionalInfo());
    placementContractExt.setIdentificationNumber(placementContractJson.getIdentificationNumber());
    placementContractExt.setPropertyIdentificationNumber(placementContractJson.getPropertyIdentificationNumber());
    placementContractExt.setContractText(placementContractJson.getContractText());
    placementContractExt.setTerminationDate(placementContractJson.getTerminationDate());
    return ApplicationExtensionExtMapper.jsonToModel(placementContractJson, placementContractExt);
  }
}
