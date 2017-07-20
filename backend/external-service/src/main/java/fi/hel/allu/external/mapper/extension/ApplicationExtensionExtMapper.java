package fi.hel.allu.external.mapper.extension;


import fi.hel.allu.external.domain.ApplicationExtensionExt;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;

public class ApplicationExtensionExtMapper {
  public static <JSON extends ApplicationExtensionJson> JSON modelToJson(ApplicationExtensionExt model, JSON json) {
    json.setTerms(model.getTerms());
    return json;
  }

  public static <MODEL extends ApplicationExtensionExt> MODEL jsonToModel(ApplicationExtensionJson json, MODEL model) {
    model.setTerms(json.getTerms());
    return model;
  }
}
