package fi.hel.allu.ui.mapper.extension;

import fi.hel.allu.model.domain.ShortTermRental;
import fi.hel.allu.ui.domain.ShortTermRentalJson;

public class ShortTermRentalMapper {
  public static ShortTermRentalJson modelToJson(ShortTermRental shortTermRental) {
    ShortTermRentalJson shortTermRentalJson = new ShortTermRentalJson();
    shortTermRentalJson.setDescription(shortTermRental.getDescription());
    shortTermRentalJson.setCommercial(shortTermRental.getCommercial());
    shortTermRentalJson.setLargeSalesArea(shortTermRental.getLargeSalesArea());
    return ApplicationExtensionMapper.modelToJson(shortTermRental, shortTermRentalJson);
  }

  public static ShortTermRental jsonToModel(ShortTermRentalJson json) {
    ShortTermRental shortTermRental = new ShortTermRental();
    shortTermRental.setDescription(json.getDescription());
    shortTermRental.setCommercial(json.getCommercial());
    shortTermRental.setLargeSalesArea(json.getLargeSalesArea());
    return ApplicationExtensionMapper.jsonToModel(json, shortTermRental);
  }
}