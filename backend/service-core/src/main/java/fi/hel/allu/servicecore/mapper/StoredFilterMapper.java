package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.StoredFilter;
import fi.hel.allu.servicecore.domain.StoredFilterJson;

public class StoredFilterMapper {
  public static StoredFilter toModel(StoredFilterJson json) {
    StoredFilter model = new StoredFilter();
    model.setId(json.getId());
    model.setType(json.getType());
    model.setName(json.getName());
    model.setDefaultFilter(json.isDefaultFilter());
    model.setFilter(json.getFilter());
    model.setUserId(json.getUserId());
    return model;
  }

  public static StoredFilterJson toJson(StoredFilter model) {
    StoredFilterJson json = new StoredFilterJson();
    json.setId(model.getId());
    json.setType(model.getType());
    json.setName(model.getName());
    json.setDefaultFilter(model.isDefaultFilter());
    json.setFilter(model.getFilter());
    json.setUserId(model.getUserId());
    return json;
  }
}
