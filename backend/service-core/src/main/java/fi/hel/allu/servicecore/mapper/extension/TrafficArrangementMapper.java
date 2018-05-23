package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.TrafficArrangement;
import fi.hel.allu.servicecore.domain.TrafficArrangementJson;

public class TrafficArrangementMapper {
  public static TrafficArrangementJson modelToJson(TrafficArrangement trafficArrangement) {
    TrafficArrangementJson trafficArrangementJson = new TrafficArrangementJson();
    trafficArrangementJson.setWorkPurpose(trafficArrangement.getWorkPurpose());
    trafficArrangementJson.setTrafficArrangements(trafficArrangement.getTrafficArrangements());
    trafficArrangementJson.setTrafficArrangementImpedimentType(trafficArrangement.getTrafficArrangementImpedimentType());
    return ApplicationExtensionMapper.modelToJson(trafficArrangement, trafficArrangementJson);
  }

  public static TrafficArrangement jsonToModel(TrafficArrangementJson json) {
    TrafficArrangement trafficArrangement = new TrafficArrangement();
    trafficArrangement.setWorkPurpose(json.getWorkPurpose());
    trafficArrangement.setTrafficArrangements(json.getTrafficArrangements());
    trafficArrangement.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    return ApplicationExtensionMapper.jsonToModel(json, trafficArrangement);
  }
}
