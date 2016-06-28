package fi.hel.allu.ui.mapper;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.OutdoorEvent;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.OutdoorEventJson;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ApplicationMapper {

  /**
   * Create a new <code>Application</code> model-domain object from given ui-domain object
   * @param applicationJson Information that is mapped to model-domain object
   * @return created application object
   */
  public Application createApplicationModel(ApplicationJson applicationJson) {
    Application applicationDomain = new Application();
    if (applicationJson.getId() != null) {
      applicationDomain.setId(applicationJson.getId());
    }
    applicationDomain.setName(applicationJson.getName());
    applicationDomain.setProjectId(applicationJson.getProject().getId());
    applicationDomain.setCreationTime(ZonedDateTime.now());
    applicationDomain.setCustomerId(applicationJson.getCustomer().getId());
    applicationDomain.setApplicantId(applicationJson.getApplicant().getId());
    applicationDomain.setHandler(applicationJson.getHandler());
    applicationDomain.setType(applicationJson.getType());
    applicationDomain.setStatus(applicationJson.getStatus());

    if (applicationJson.getLocation() != null && applicationJson.getLocation().getId() != null) {
      applicationDomain.setLocationId(applicationJson.getLocation().getId());
    }
    if (applicationJson.getEvent() != null) {
      applicationDomain.setEvent(createEventModel(applicationJson));
    }
    return applicationDomain;
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object
   * @param applicationJson
   * @param application
   */
  public void mapApplicationToJson(ApplicationJson applicationJson, Application application) {
    applicationJson.setId(application.getId());
    applicationJson.setStatus(application.getStatus());
    applicationJson.setType(application.getType());
    applicationJson.setHandler(application.getHandler());
    applicationJson.setCreationTime(application.getCreationTime());
    applicationJson.setName(application.getName());
    if (application.getEvent() != null) {
      mapEventToJson(applicationJson, application);
    }
  }


  /**
   * Transfer the information from the given model-domain object to given ui-domain object
   * @param applicationJson
   * @param application
   */
  public void mapEventToJson(ApplicationJson applicationJson, Application application) {
    switch (applicationJson.getType()) {
      case OutdoorEvent:
        OutdoorEvent outdoorEvent = (OutdoorEvent) application.getEvent();
        OutdoorEventJson outdoorEventJson = new OutdoorEventJson();
        outdoorEventJson.setUrl(outdoorEvent.getUrl());
        outdoorEventJson.setNature(outdoorEvent.getNature());
        outdoorEventJson.setStartTime(outdoorEvent.getStartTime());
        outdoorEventJson.setEndTime(outdoorEvent.getEndTime());
        outdoorEventJson.setAttendees(outdoorEvent.getAttendees());
        outdoorEventJson.setDescription(outdoorEvent.getDescription());
        outdoorEventJson.setTimeExceptions(outdoorEvent.getTimeExceptions());
        outdoorEventJson.setEcoCompass(outdoorEvent.isEcoCompass());
        outdoorEventJson.setStructureArea(outdoorEvent.getStructureArea());
        outdoorEventJson.setStructureDescription(outdoorEvent.getStructureDescription());
        outdoorEventJson.setStructureEndTime(outdoorEvent.getStructureEndTime());
        outdoorEventJson.setStructureStartTime(outdoorEvent.getStructureStartTime());
        outdoorEventJson.setEntryFee(outdoorEvent.getEntryFee());
        outdoorEventJson.setFoodProviders(outdoorEvent.getFoodProviders());
        outdoorEventJson.setMarketingProviders(outdoorEvent.getMarketingProviders());
        outdoorEventJson.setPricing(outdoorEvent.getPricing());
        outdoorEventJson.setSalesActivity(outdoorEvent.isSalesActivity());
        applicationJson.setEvent(outdoorEventJson);
        break;
    }
  }

  /**
   * Create a new <code>Event</code> model-domain object from given ui-domain object based on application type.
   * @param applicationJson Information that is mapped to model-domain object
   * @return created event object
   */
  public Event createEventModel(ApplicationJson applicationJson) {
    switch (applicationJson.getType()) {
      case OutdoorEvent:
        OutdoorEventJson outdoorEventJson = (OutdoorEventJson) applicationJson.getEvent();
        OutdoorEvent outdoorEvent = new OutdoorEvent();
        outdoorEvent.setDescription(outdoorEventJson.getDescription());
        outdoorEvent.setNature(outdoorEventJson.getNature());
        outdoorEvent.setUrl(outdoorEventJson.getUrl());
        outdoorEvent.setAttendees(outdoorEventJson.getAttendees());
        outdoorEvent.setEndTime(outdoorEventJson.getEndTime());
        outdoorEvent.setStartTime(outdoorEventJson.getStartTime());
        outdoorEvent.setSalesActivity(outdoorEventJson.isSalesActivity());
        outdoorEvent.setMarketingProviders(outdoorEventJson.getMarketingProviders());
        outdoorEvent.setPricing(outdoorEventJson.getPricing());
        outdoorEvent.setFoodProviders(outdoorEventJson.getFoodProviders());
        outdoorEvent.setEntryFee(outdoorEventJson.getEntryFee());
        outdoorEvent.setEcoCompass(outdoorEventJson.isEcoCompass());
        outdoorEvent.setStructureArea(outdoorEventJson.getStructureArea());
        outdoorEvent.setStructureEndTime(outdoorEventJson.getStructureEndTime());
        outdoorEvent.setStructureStartTime(outdoorEventJson.getStructureStartTime());
        outdoorEvent.setStructureDescription(outdoorEventJson.getStructureDescription());
        outdoorEvent.setTimeExceptions(outdoorEventJson.getTimeExceptions());
        return outdoorEvent;
    }
    return null;
  }
}
