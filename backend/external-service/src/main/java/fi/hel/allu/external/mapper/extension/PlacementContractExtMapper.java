package fi.hel.allu.external.mapper.extension;

import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.servicecore.domain.PlacementContractJson;

public class PlacementContractExtMapper {

  public static PlacementContractJson extToJson(PlacementContractExt placementContractExt) {
    PlacementContractJson placementContractJson = new PlacementContractJson();
    placementContractJson.setAdditionalInfo(placementContractExt.getAdditionalInfo());
    placementContractJson.setDiaryNumber(placementContractExt.getDiaryNumber());
    placementContractJson.setGeneralTerms(placementContractExt.getGeneralTerms());
    return ApplicationExtensionExtMapper.modelToJson(placementContractExt, placementContractJson);
  }

  public static PlacementContractExt jsonToExt(PlacementContractJson placementContractJson) {
    PlacementContractExt placementContractExt = new PlacementContractExt();
    placementContractExt.setAdditionalInfo(placementContractJson.getAdditionalInfo());
    placementContractExt.setDiaryNumber(placementContractJson.getDiaryNumber());
    placementContractExt.setGeneralTerms(placementContractJson.getGeneralTerms());
    return ApplicationExtensionExtMapper.jsonToModel(placementContractJson, placementContractExt);
  }
}
