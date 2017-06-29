package fi.hel.allu.ui.mapper.extension;

import fi.hel.allu.model.domain.TrafficArrangement;
import fi.hel.allu.ui.domain.TrafficArrangementJson;

public class TrafficArrangementMapper {
  public static TrafficArrangementJson modelToJson(TrafficArrangement trafficArrangement) {
    TrafficArrangementJson trafficArrangementJson = new TrafficArrangementJson();
    trafficArrangementJson.setPksCard(trafficArrangement.getPksCard());
    trafficArrangementJson.setWorkFinished(trafficArrangement.getWorkFinished());
    trafficArrangementJson.setAdditionalInfo(trafficArrangement.getAdditionalInfo());
    trafficArrangementJson.setTrafficArrangements(trafficArrangement.getTrafficArrangements());
    trafficArrangementJson.setTrafficArrangementImpedimentType(trafficArrangement.getTrafficArrangementImpedimentType());
    return ApplicationExtensionMapper.modelToJson(trafficArrangement, trafficArrangementJson);
  }

  public static TrafficArrangement jsonToModel(TrafficArrangementJson json) {
    TrafficArrangement trafficArrangement = new TrafficArrangement();
    trafficArrangement.setPksCard(json.getPksCard());
    trafficArrangement.setWorkFinished(json.getWorkFinished());
    trafficArrangement.setAdditionalInfo(json.getAdditionalInfo());
    trafficArrangement.setTrafficArrangements(json.getTrafficArrangements());
    trafficArrangement.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    return ApplicationExtensionMapper.jsonToModel(json, trafficArrangement);
  }
}