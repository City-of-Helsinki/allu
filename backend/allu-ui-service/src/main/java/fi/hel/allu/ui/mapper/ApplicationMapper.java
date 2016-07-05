package fi.hel.allu.ui.mapper;

import java.time.ZonedDateTime;

import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.ui.domain.ProjectJson;
import org.springframework.stereotype.Component;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.OutdoorEvent;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationTypeDataES;
import fi.hel.allu.search.domain.OutdoorEventES;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.OutdoorEventJson;

@Component
public class ApplicationMapper {
  private static final String SALES_ACTIVITY = "Myyntitoimintaa";
  private static final String ECO_COMPASS = "Ekokompassi";


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
    if (applicationJson.getCustomer() != null) {
      applicationDomain.setCustomerId(applicationJson.getCustomer().getId());
    }
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
   * Create a new <code>ApplicationES</code> elasticsearch-domain object from given ui-domain object
   * @param applicationJson Information that is mapped to search-domain object
   * @return created applicationES object
   */
  public ApplicationES createApplicationESModel(ApplicationJson applicationJson) {
    ApplicationES applicationES = new ApplicationES();
    applicationES.setId(applicationJson.getId());
    applicationES.setName(applicationJson.getName());
    applicationES.setCreationTime(ZonedDateTime.now());
    applicationES.setHandler(applicationJson.getHandler());
    applicationES.setType(applicationJson.getType());
    applicationES.setStatus(applicationJson.getStatus());
    applicationES.setApplicationTypeData(createApplicationTypeDataES(applicationJson));
    if (applicationJson.getProject() != null) {
      applicationES.setProject(createProjectES(applicationJson));
    }
    return applicationES;
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
   * Transfer the information from the given search-domain object to given ui-domain object
   * @param applicationJson
   * @param applicationES
   */
  public ApplicationJson mapApplicationESToJson(ApplicationJson applicationJson, ApplicationES applicationES) {
    applicationJson.setId(applicationES.getId());
    applicationJson.setStatus(applicationES.getStatus());
    applicationJson.setType(applicationES.getType());
    applicationJson.setHandler(applicationES.getHandler());
    applicationJson.setCreationTime(applicationES.getCreationTime());
    applicationJson.setName(applicationES.getName());
    mapEventESToJson(applicationJson, applicationES);
    mapProjectEStoJson(applicationJson, applicationES);
    return applicationJson;
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
   * Transfer the information from the given search-domain object to given ui-domain object
   * @param applicationJson
   * @param applicationES
   */
  public void mapEventESToJson(ApplicationJson applicationJson, ApplicationES applicationES) {
    switch (applicationJson.getType()) {
      case OutdoorEvent:
        OutdoorEventES outdoorEventES = (OutdoorEventES) applicationES.getApplicationTypeData();
        OutdoorEventJson outdoorEventJson = new OutdoorEventJson();
        outdoorEventJson.setUrl(outdoorEventES.getUrl());
        outdoorEventJson.setNature(outdoorEventES.getNature());
        outdoorEventJson.setStartTime(outdoorEventES.getStartTime());
        outdoorEventJson.setEndTime(outdoorEventES.getEndTime());
        outdoorEventJson.setAttendees(outdoorEventES.getAttendees());
        outdoorEventJson.setDescription(outdoorEventES.getDescription());
        outdoorEventJson.setTimeExceptions(outdoorEventES.getTimeExceptions());
        if (outdoorEventES.getEcoCompass() != null && outdoorEventES.getEcoCompass().equals(ECO_COMPASS)) {
          outdoorEventJson.setEcoCompass(true);
        }
        outdoorEventJson.setStructureArea(outdoorEventES.getStructureArea());
        outdoorEventJson.setStructureDescription(outdoorEventES.getStructureDescription());
        outdoorEventJson.setStructureEndTime(outdoorEventES.getStructureEndTime());
        outdoorEventJson.setStructureStartTime(outdoorEventES.getStructureStartTime());
        outdoorEventJson.setEntryFee(outdoorEventES.getEntryFee());
        outdoorEventJson.setFoodProviders(outdoorEventES.getFoodProviders());
        outdoorEventJson.setMarketingProviders(outdoorEventES.getMarketingProviders());
        outdoorEventJson.setPricing(outdoorEventES.getPricing());
        if (outdoorEventES.getSalesActivity() != null && outdoorEventES.getSalesActivity().equals(SALES_ACTIVITY)) {
          outdoorEventJson.setSalesActivity(true);
        }
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

  /**
   * Create a new <code>ApplicationTypeDataES</code> search-domain object from given ui-domain object.
   * @param applicationJson Information that is mapped to search-domain object
   * @return created ApplicationTypeDataES object
   */
  public ApplicationTypeDataES createApplicationTypeDataES(ApplicationJson applicationJson) {
    switch (applicationJson.getType()) {
      case OutdoorEvent:
        OutdoorEventJson outdoorEventJson = (OutdoorEventJson) applicationJson.getEvent();

        OutdoorEventES outdoorEvent = new OutdoorEventES();
        outdoorEvent.setDescription(outdoorEventJson.getDescription());
        outdoorEvent.setNature(outdoorEventJson.getNature());
        outdoorEvent.setUrl(outdoorEventJson.getUrl());
        outdoorEvent.setAttendees(outdoorEventJson.getAttendees());
        outdoorEvent.setEndTime(outdoorEventJson.getEndTime());
        outdoorEvent.setStartTime(outdoorEventJson.getStartTime());
        outdoorEvent.setEndTime(outdoorEventJson.getEndTime());
        outdoorEvent.setPricing(outdoorEventJson.getPricing());
        outdoorEvent.setTimeExceptions(outdoorEventJson.getTimeExceptions());
        outdoorEvent.setStructureArea(outdoorEventJson.getStructureArea());
        outdoorEvent.setStructureDescription(outdoorEventJson.getStructureDescription());
        outdoorEvent.setStructureStartTime(outdoorEventJson.getStructureStartTime());
        outdoorEvent.setStructureEndTime(outdoorEventJson.getStructureEndTime());
        if (outdoorEventJson.isSalesActivity()) {
          outdoorEvent.setSalesActivity(SALES_ACTIVITY);
        }
        if (outdoorEventJson.isEcoCompass()) {
          outdoorEvent.setEcoCompass(ECO_COMPASS);
        }
        outdoorEvent.setMarketingProviders(outdoorEventJson.getMarketingProviders());
        outdoorEvent.setFoodProviders(outdoorEventJson.getFoodProviders());
        return outdoorEvent;
    }
    return null;
  }

  private ProjectES createProjectES(ApplicationJson applicationJson) {
    ProjectES projectES = new ProjectES();
    projectES.setId(applicationJson.getProject().getId());
    projectES.setName(applicationJson.getProject().getName());
    projectES.setInformation(applicationJson.getProject().getInformation());
    projectES.setType(applicationJson.getProject().getType());
    return projectES;
  }

  private void mapProjectEStoJson(ApplicationJson applicationJson, ApplicationES applicationES) {
    if (applicationES.getProject() != null) {
      ProjectJson projectJson = new ProjectJson();
      projectJson.setId(applicationES.getProject().getId());
      projectJson.setType(applicationES.getProject().getType());
      projectJson.setInformation(applicationES.getProject().getInformation());
      projectJson.setName(applicationES.getProject().getName());
      applicationJson.setProject(projectJson);
    }
  }
}
