package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.servicecore.domain.AreaRentalJson;

public class AreaRentalMapper {
  public static AreaRentalJson modelToJson(AreaRental areaRental) {
    AreaRentalJson areaRentalJson = new AreaRentalJson();
    areaRentalJson.setPksCard(areaRental.getPksCard());
    areaRentalJson.setWorkPurpose(areaRental.getWorkPurpose());
    areaRentalJson.setAdditionalInfo(areaRental.getAdditionalInfo());
    areaRentalJson.setTrafficArrangements(areaRental.getTrafficArrangements());
    areaRentalJson.setTrafficArrangementImpedimentType(areaRental.getTrafficArrangementImpedimentType());
    areaRentalJson.setWorkFinished(areaRental.getWorkFinished());
    areaRentalJson.setCustomerWorkFinished(areaRental.getCustomerWorkFinished());
    areaRentalJson.setWorkFinishedReported(areaRental.getWorkFinishedReported());
    return ApplicationExtensionMapper.modelToJson(areaRental, areaRentalJson);
  }

  public static AreaRental jsonToModel(AreaRentalJson json) {
    AreaRental areaRental = new AreaRental();
    areaRental.setPksCard(json.getPksCard());
    areaRental.setWorkPurpose(json.getWorkPurpose());
    areaRental.setAdditionalInfo(json.getAdditionalInfo());
    areaRental.setTrafficArrangements(json.getTrafficArrangements());
    areaRental.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    areaRental.setWorkFinished(json.getWorkFinished());
    areaRental.setCustomerWorkFinished(json.getCustomerWorkFinished());
    areaRental.setWorkFinishedReported(json.getWorkFinishedReported());
    return ApplicationExtensionMapper.jsonToModel(json, areaRental);
  }
}
