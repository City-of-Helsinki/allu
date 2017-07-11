package fi.hel.allu.servicecore.mapper.extension;


import fi.hel.allu.model.domain.ApplicationExtension;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;

public class ApplicationExtensionMapper {
  public static <JSON extends ApplicationExtensionJson> JSON modelToJson(ApplicationExtension model, JSON json) {
    json.setSpecifiers(model.getSpecifiers());
    json.setTerms(model.getTerms());
    return json;
  }

  public static <MODEL extends ApplicationExtension> MODEL jsonToModel(ApplicationExtensionJson json, MODEL model) {
    model.setSpecifiers(json.getSpecifiers());
    model.setTerms(json.getTerms());
    return model;
  }
}
