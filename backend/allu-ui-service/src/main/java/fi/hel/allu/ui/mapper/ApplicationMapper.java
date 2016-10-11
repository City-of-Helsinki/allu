package fi.hel.allu.ui.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wnameless.json.flattener.JsonFlattener;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.OutdoorEvent;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.ui.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApplicationMapper {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationMapper.class);


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
    applicationDomain.setApplicationId(applicationJson.getApplicationId());
    applicationDomain.setName(applicationJson.getName());
    applicationDomain.setProjectId(applicationJson.getProject().getId());
    applicationDomain.setCreationTime(ZonedDateTime.now());
    applicationDomain.setStartTime(applicationJson.getStartTime());
    applicationDomain.setEndTime(applicationJson.getEndTime());
    applicationDomain.setApplicantId(applicationJson.getApplicant().getId());
    applicationDomain.setHandler(applicationJson.getHandler() != null ? applicationJson.getHandler().getId() : null);
    applicationDomain.setType(applicationJson.getType());
    applicationDomain.setMetadataVersion(applicationJson.getMetadata().getVersion());
    applicationDomain.setStatus(applicationJson.getStatus());
    applicationDomain.setDecisionTime(applicationJson.getDecisionTime());
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
    applicationES.setApplicationId(applicationJson.getApplicationId());
    applicationES.setName(applicationJson.getName());
    applicationES.setCreationTime(ZonedDateTime.now());
    applicationES.setStartTime(applicationJson.getStartTime());
    applicationES.setEndTime(applicationJson.getEndTime());
    applicationES.setHandler(applicationJson.getHandler() != null ? applicationJson.getHandler().getId() : null);
    applicationES.setType(applicationJson.getType());
    applicationES.setStatus(applicationJson.getStatus());
    applicationES.setDecisionTime(applicationJson.getDecisionTime());
    applicationES.setApplicationTypeData(createApplicationTypeDataES(applicationJson));
    applicationES.setLocation(createLocationES(applicationJson.getLocation()));
    applicationES.setContacts(createContactES(applicationJson.getContactList()));
    applicationES.setApplicant(createApplicantES(applicationJson.getApplicant()));
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
    applicationJson.setApplicationId(application.getApplicationId());
    applicationJson.setStatus(application.getStatus());
    applicationJson.setType(application.getType());
    applicationJson.setCreationTime(application.getCreationTime());
    applicationJson.setStartTime(application.getStartTime());
    applicationJson.setEndTime(application.getEndTime());
    applicationJson.setName(application.getName());
    applicationJson.setDecisionTime(application.getDecisionTime());
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
      case OUTDOOREVENT:
        OutdoorEvent outdoorEvent = (OutdoorEvent) application.getEvent();
        OutdoorEventJson outdoorEventJson = new OutdoorEventJson();
        outdoorEventJson.setUrl(outdoorEvent.getUrl());
        outdoorEventJson.setNature(outdoorEvent.getNature());
        outdoorEventJson.setEventStartTime(outdoorEvent.getEventStartTime());
        outdoorEventJson.setEventEndTime(outdoorEvent.getEventEndTime());
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
        outdoorEventJson.setHeavyStructure(outdoorEvent.isHeavyStructure());
        outdoorEventJson.setFoodSales(outdoorEvent.isFoodSales());
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
      case OUTDOOREVENT:
        OutdoorEventJson outdoorEventJson = (OutdoorEventJson) applicationJson.getEvent();
        OutdoorEvent outdoorEvent = new OutdoorEvent();
        outdoorEvent.setDescription(outdoorEventJson.getDescription());
        outdoorEvent.setNature(outdoorEventJson.getNature());
        outdoorEvent.setUrl(outdoorEventJson.getUrl());
        outdoorEvent.setAttendees(outdoorEventJson.getAttendees());
        outdoorEvent.setEventEndTime(outdoorEventJson.getEventEndTime());
        outdoorEvent.setEventStartTime(outdoorEventJson.getEventStartTime());
        outdoorEvent.setFoodSales(outdoorEventJson.isFoodSales());
        outdoorEvent.setMarketingProviders(outdoorEventJson.getMarketingProviders());
        outdoorEvent.setPricing(outdoorEventJson.getPricing());
        outdoorEvent.setSalesActivity(outdoorEventJson.isSalesActivity());
        outdoorEvent.setHeavyStructure(outdoorEventJson.isHeavyStructure());
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
  public List<ESFlatValue> createApplicationTypeDataES(ApplicationJson applicationJson) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String json;
    try {
      json = objectMapper.writeValueAsString(applicationJson.getEvent());
    } catch (JsonProcessingException e) {
      logger.error("Unexpected error while mapping {} as JSON", applicationJson);
      throw new RuntimeException(e);
    }

    Map<String, Object> flattenedMap = new JsonFlattener(json).withSeparator('-').flattenAsMap();
    Map<String, Object> flattenedMapNoNulls = flattenedMap.entrySet().stream()
        .filter(e -> e.getValue() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    List<ESFlatValue> flatList = flattenedMapNoNulls.entrySet().stream()
        .map(e -> ESFlatValue.mapValue(applicationJson.getType().name(), e.getKey(), e.getValue()))
        .collect(Collectors.toList());
    return flatList;
  }

  /**
   * Map a model-domain AttachmentInfo to ui-domain AttachmentInfoJson
   *
   * @param attachmentInfoJson
   * @param attachmentInfo
   */
  public void mapAttachmentInfoToJson(AttachmentInfoJson attachmentInfoJson, AttachmentInfo attachmentInfo) {
    attachmentInfoJson.setId(attachmentInfo.getId());
    attachmentInfoJson.setName(attachmentInfo.getName());
    attachmentInfoJson.setDescription(attachmentInfo.getDescription());
    attachmentInfoJson.setSize(attachmentInfo.getSize());
    attachmentInfoJson.setCreationTime(attachmentInfo.getCreationTime());
  }

  private ProjectES createProjectES(ApplicationJson applicationJson) {
    ProjectES projectES = new ProjectES();
    projectES.setId(applicationJson.getProject().getId());
    projectES.setName(applicationJson.getProject().getName());
    projectES.setInformation(applicationJson.getProject().getInformation());
    projectES.setType(applicationJson.getProject().getType());
    return projectES;
  }

  private LocationES createLocationES(LocationJson locationJson) {
    if (locationJson != null && locationJson.getPostalAddress() != null) {
      return new LocationES(
          locationJson.getPostalAddress().getStreetAddress(),
          locationJson.getPostalAddress().getPostalCode(),
          locationJson.getPostalAddress().getCity());
    } else {
      return null;
    }
  }

  private List<ContactES> createContactES(List<ContactJson> contacts) {
    if (contacts != null) {
      return contacts.stream()
          .map(c -> new ContactES(c.getName()))
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  private ApplicantES createApplicantES(ApplicantJson applicantJson) {
    if (applicantJson != null) {
      return new ApplicantES(applicantJson.getName());
    } else {
      return null;
    }
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
