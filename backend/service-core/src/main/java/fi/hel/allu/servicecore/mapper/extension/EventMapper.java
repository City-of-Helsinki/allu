package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.servicecore.domain.EventJson;

import java.time.temporal.ChronoUnit;

public class EventMapper {
  public static EventJson modelToJson(Application application) {
    Event event = (Event) application.getExtension();
    EventJson eventJson = new EventJson();
    eventJson.setUrl(event.getUrl());
    eventJson.setNature(event.getNature());
    if (event.getBuildSeconds() != 0 && application.getStartTime() != null) {
      eventJson.setEventStartTime(application.getStartTime().plusSeconds(event.getBuildSeconds()));
      eventJson.setStructureStartTime(application.getStartTime());
    } else {
      eventJson.setEventStartTime(application.getStartTime());
      eventJson.setStructureStartTime(null);
    }
    if (event.getTeardownSeconds() != 0 && application.getEndTime() != null) {
      eventJson.setEventEndTime(application.getEndTime().minusSeconds(event.getTeardownSeconds()));
      eventJson.setStructureEndTime(application.getEndTime());
    } else {
      eventJson.setEventEndTime(application.getEndTime());
      eventJson.setStructureEndTime(null);
    }
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
    if (eventJson.getStructureStartTime() != null && eventJson.getEventStartTime() != null) {
      event.setBuildSeconds(eventJson.getStructureStartTime().until(eventJson.getEventStartTime(), ChronoUnit.SECONDS));
    }
    if (eventJson.getStructureEndTime() != null && eventJson.getEventEndTime() != null) {
      event.setTeardownSeconds(eventJson.getEventEndTime().until(eventJson.getStructureEndTime(), ChronoUnit.SECONDS));
    }
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
