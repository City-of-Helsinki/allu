package fi.hel.allu.ui.mapper.extension;

import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.ui.domain.AreaRentalJson;

public class AreaRentalMapper {
  public static AreaRentalJson modelToJson(AreaRental areaRental) {
    AreaRentalJson areaRentalJson = new AreaRentalJson();
    areaRentalJson.setPksCard(areaRental.getPksCard());
    areaRentalJson.setAdditionalInfo(areaRental.getAdditionalInfo());
    areaRentalJson.setTrafficArrangements(areaRental.getTrafficArrangements());
    areaRentalJson.setTrafficArrangementImpedimentType(areaRental.getTrafficArrangementImpedimentType());
    areaRentalJson.setWorkFinished(areaRental.getWorkFinished());
    return ApplicationExtensionMapper.modelToJson(areaRental, areaRentalJson);
  }

  public static AreaRental jsonToModel(AreaRentalJson json) {
    AreaRental areaRental = new AreaRental();
    areaRental.setPksCard(json.getPksCard());
    areaRental.setAdditionalInfo(json.getAdditionalInfo());
    areaRental.setTrafficArrangements(json.getTrafficArrangements());
    areaRental.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    areaRental.setWorkFinished(json.getWorkFinished());
    return ApplicationExtensionMapper.jsonToModel(json, areaRental);

  }
}