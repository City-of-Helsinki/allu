package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.servicecore.domain.EventJson;

public class EventMapper {
  public static EventJson modelToJson(Application application) {
    Event event = (Event) application.getExtension();
    EventJson eventJson = new EventJson();
    eventJson.setUrl(event.getUrl());
    eventJson.setNature(event.getNature());eventJson.setEventStartTime(event.getEventStartTime());
    eventJson.setEventEndTime(event.getEventEndTime());
    eventJson.setAttendees(event.getAttendees());
    eventJson.setDescription(event.getDescription());
    eventJson.setTimeExceptions(event.getTimeExceptions());
    eventJson.setEcoCompass(event.isEcoCompass());
    eventJson.setStructureArea(event.getStructureArea());
    eventJson.setStructureDescription(event.getStructureDescription());
    eventJson.setEntryFee(event.getEntryFee());
    eventJson.setFoodProviders(event.getFoodProviders());
    eventJson.setMarketingProviders(event.getMarketingProviders());
    eventJson.setFoodSales(event.isFoodSales());
    return ApplicationExtensionMapper.modelToJson(event, eventJson);
  }

  public static Event jsonToModel(EventJson eventJson) {
    Event event = new Event();
    event.setDescription(eventJson.getDescription());
    event.setNature(eventJson.getNature());
    event.setUrl(eventJson.getUrl());
    event.setAttendees(eventJson.getAttendees());
    event.setEventStartTime(eventJson.getEventStartTime());
    event.setEventEndTime(eventJson.getEventEndTime());
    event.setFoodSales(eventJson.isFoodSales());
    event.setMarketingProviders(eventJson.getMarketingProviders());
    event.setFoodProviders(eventJson.getFoodProviders());
    event.setEntryFee(eventJson.getEntryFee());
    event.setEcoCompass(eventJson.isEcoCompass());
    event.setStructureArea(eventJson.getStructureArea());
    event.setStructureDescription(eventJson.getStructureDescription());
    event.setTimeExceptions(eventJson.getTimeExceptions());
    return ApplicationExtensionMapper.jsonToModel(eventJson, event);
  }
}
