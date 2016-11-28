package fi.hel.allu.ui.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wnameless.json.flattener.JsonFlattener;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.ui.domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    if (applicationJson.getProject() != null) {
      applicationDomain.setProjectId(applicationJson.getProject().getId());
    }
    applicationDomain.setCreationTime(ZonedDateTime.now());
    applicationDomain.setStartTime(applicationJson.getStartTime());
    applicationDomain.setEndTime(applicationJson.getEndTime());
    applicationDomain.setApplicantId(applicationJson.getApplicant().getId());
    applicationDomain.setHandler(applicationJson.getHandler() != null ? applicationJson.getHandler().getId() : null);
    applicationDomain.setType(applicationJson.getType());
    applicationDomain.setKind(applicationJson.getKind());
    applicationDomain.setMetadataVersion(applicationJson.getMetadata().getVersion());
    applicationDomain.setStatus(applicationJson.getStatus());
    applicationDomain.setDecisionTime(applicationJson.getDecisionTime());
    if (applicationJson.getLocation() != null && applicationJson.getLocation().getId() != null) {
      applicationDomain.setLocationId(applicationJson.getLocation().getId());
    }
    if (applicationJson.getExtension() != null) {
      applicationDomain.setExtension(createExtensionModel(applicationJson));
    }
    applicationDomain.setCalculatedPrice(applicationJson.getCalculatedPrice());
    applicationDomain.setPriceOverride(applicationJson.getPriceOverride());
    applicationDomain.setPriceOverrideReason(applicationJson.getPriceOverrideReason());
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
    applicationES.setHandler(
        applicationJson.getHandler() != null ?
            new UserES(applicationJson.getHandler().getUserName(), applicationJson.getHandler().getRealName()) : null);
    applicationES.setType(new ApplicationTypeES(applicationJson.getType()));
    applicationES.setStatus(new StatusTypeES(applicationJson.getStatus()));
    applicationES.setDecisionTime(applicationJson.getDecisionTime());
    applicationES.setApplicationTypeData(createApplicationTypeDataES(applicationJson));
    applicationES.setLocation(createLocationES(applicationJson.getLocation()));
    applicationES.setContacts(createContactES(applicationJson.getContactList()));
    applicationES.setApplicant(createApplicantES(applicationJson.getApplicant()));
    if (applicationJson.getProject() != null) {
      applicationES.setProjectId(applicationJson.getProject().getId());
    }
    return applicationES;
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object. Does not handle references to other objects like
   * applicant.
   *
   * @param application
   */
  public ApplicationJson mapApplicationToJson(Application application) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setId(application.getId());
    applicationJson.setApplicationId(application.getApplicationId());
    applicationJson.setStatus(application.getStatus());
    applicationJson.setType(application.getType());
    applicationJson.setKind(application.getKind());
    applicationJson.setCreationTime(application.getCreationTime());
    applicationJson.setStartTime(application.getStartTime());
    applicationJson.setEndTime(application.getEndTime());
    applicationJson.setName(application.getName());
    applicationJson.setDecisionTime(application.getDecisionTime());
    if (application.getExtension() != null) {
      mapEventToJson(applicationJson, application);
    }
    applicationJson.setCalculatedPrice(application.getCalculatedPrice());
    applicationJson.setPriceOverride(application.getPriceOverride());
    applicationJson.setPriceOverrideReason(application.getPriceOverrideReason());

    return applicationJson;
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object
   * @param applicationJson
   * @param application
   */
  public void mapEventToJson(ApplicationJson applicationJson, Application application) {
    switch (applicationJson.getType()) {
    case EVENT:
        Event event = (Event) application.getExtension();
        EventJson eventJson = new EventJson();
        eventJson.setUrl(event.getUrl());
        eventJson.setNature(event.getNature());
        eventJson.setEventStartTime(event.getEventStartTime());
        eventJson.setEventEndTime(event.getEventEndTime());
        eventJson.setAttendees(event.getAttendees());
        eventJson.setDescription(event.getDescription());
        eventJson.setTimeExceptions(event.getTimeExceptions());
        eventJson.setEcoCompass(event.isEcoCompass());
        eventJson.setStructureArea(event.getStructureArea());
        eventJson.setStructureDescription(event.getStructureDescription());
        eventJson.setStructureEndTime(event.getStructureEndTime());
        eventJson.setStructureStartTime(event.getStructureStartTime());
        eventJson.setEntryFee(event.getEntryFee());
        eventJson.setFoodProviders(event.getFoodProviders());
        eventJson.setMarketingProviders(event.getMarketingProviders());
        eventJson.setNoPriceReason(event.getNoPriceReason());
        eventJson.setSalesActivity(event.isSalesActivity());
        eventJson.setHeavyStructure(event.isHeavyStructure());
        eventJson.setFoodSales(event.isFoodSales());
        applicationJson.setExtension(eventJson);
        break;
      // short term rentals
    case SHORT_TERM_RENTAL:
        ShortTermRental shortTermRental = (ShortTermRental) application.getExtension();
        ShortTermRentalJson shortTermRentalJson = new ShortTermRentalJson();
        shortTermRentalJson.setDescription(shortTermRental.getDescription());
        shortTermRentalJson.setCommercial(shortTermRental.getCommercial());
        shortTermRentalJson.setLargeSalesArea(shortTermRental.getLargeSalesArea());
        applicationJson.setExtension(shortTermRentalJson);
        break;
      // cable reports
    case CABLE_REPORT:
        CableReport cableReport = (CableReport) application.getExtension();
        CableReportJson cableReportJson = new CableReportJson();
        cableReportJson.setCableReportId(cableReport.getCableReportId());
        cableReportJson.setWorkDescription(cableReport.getWorkDescription());
        cableReportJson.setOwner(createApplicantJson(cableReport.getOwner()));
        cableReportJson.setContact(createContactJson(cableReport.getContact()));
        cableReportJson.setMapExtractCount(cableReport.getMapExtractCount());
        List<CableInfoEntryJson> infoEntries = Optional.ofNullable(cableReport.getInfoEntries())
          .orElse(Collections.emptyList()).stream().map(i -> createCableInfoEntryJson(i)).collect(Collectors.toList());
        cableReportJson.setInfoEntries(infoEntries);
        applicationJson.setExtension(cableReportJson);
      break;
    case AREA_RENTAL:
      break;
    case EXCAVATION_ANNOUNCEMENT:
      break;
    case NOTE:
      break;
    case PLACEMENT_PERMIT:
      break;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      break;
    default:
      break;
    }
  }

  /**
   * Create a new <code>ApplicationExtension</code> model-domain object from given ui-domain object based on application type.
   * @param applicationJson Information that is mapped to model-domain object
   * @return created event object
   */
  public ApplicationExtension createExtensionModel(ApplicationJson applicationJson) {
    switch (applicationJson.getType()) {
    case EVENT:
        EventJson eventJson = (EventJson) applicationJson.getExtension();
        Event event = new Event();
        event.setDescription(eventJson.getDescription());
        event.setNature(eventJson.getNature());
        event.setUrl(eventJson.getUrl());
        event.setAttendees(eventJson.getAttendees());
        event.setEventEndTime(eventJson.getEventEndTime());
        event.setEventStartTime(eventJson.getEventStartTime());
        event.setFoodSales(eventJson.isFoodSales());
        event.setMarketingProviders(eventJson.getMarketingProviders());
        event.setNoPriceReason(eventJson.getNoPriceReason());
        event.setSalesActivity(eventJson.isSalesActivity());
        event.setHeavyStructure(eventJson.isHeavyStructure());
        event.setFoodProviders(eventJson.getFoodProviders());
        event.setEntryFee(eventJson.getEntryFee());
        event.setEcoCompass(eventJson.isEcoCompass());
        event.setStructureArea(eventJson.getStructureArea());
        event.setStructureEndTime(eventJson.getStructureEndTime());
        event.setStructureStartTime(eventJson.getStructureStartTime());
        event.setStructureDescription(eventJson.getStructureDescription());
        event.setTimeExceptions(eventJson.getTimeExceptions());
        return event;
      // short term rentals
    case SHORT_TERM_RENTAL:
        ShortTermRentalJson shortTermRentalJson = (ShortTermRentalJson) applicationJson.getExtension();
        ShortTermRental shortTermRental = new ShortTermRental();
        shortTermRental.setDescription(shortTermRentalJson.getDescription());
        shortTermRental.setCommercial(shortTermRentalJson.getCommercial());
        shortTermRental.setLargeSalesArea(shortTermRentalJson.getLargeSalesArea());
        return shortTermRental;
    case CABLE_REPORT:
        CableReportJson cableReportJson = (CableReportJson) applicationJson.getExtension();
        CableReport cableReport = new CableReport();
        cableReport.setCableReportId(cableReportJson.getCableReportId());
        cableReport.setWorkDescription(cableReportJson.getWorkDescription());
        cableReport.setOwner(createApplicantModel(cableReportJson.getOwner()));
        cableReport.setContact(createContactModel(cableReportJson.getContact()));
        cableReport.setMapExtractCount(cableReportJson.getMapExtractCount());
        List<CableInfoEntry> infoEntries = Optional.ofNullable(cableReportJson.getInfoEntries())
          .orElse(Collections.emptyList()).stream().map(i -> createCableInfoEntryModel(i)).collect(Collectors.toList());
        cableReport.setInfoEntries(infoEntries);
        return cableReport;
    case AREA_RENTAL:
      break;
    case EXCAVATION_ANNOUNCEMENT:
      break;
    case NOTE:
      break;
    case PLACEMENT_PERMIT:
      break;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      break;
    default:
      break;
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
      json = objectMapper.writeValueAsString(applicationJson.getExtension());
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

  public void mapApplicantToJson(ApplicantJson applicantJson, Applicant applicant) {
    applicantJson.setId(applicant.getId());
    applicantJson.setType(applicant.getType());
    applicantJson.setName(applicant.getName());
    applicantJson.setRegistryKey(applicant.getRegistryKey());
    applicantJson.setPhone(applicant.getPhone());
    applicantJson.setEmail(applicant.getEmail());
    PostalAddressJson postalAddressJson = null;
    if (applicant.getStreetAddress() != null || applicant.getCity() != null || applicant.getPostalCode() != null) {
      postalAddressJson = new PostalAddressJson();
      postalAddressJson.setStreetAddress(applicant.getStreetAddress());
      postalAddressJson.setCity(applicant.getCity());
      postalAddressJson.setPostalCode(applicant.getPostalCode());
    }
    applicantJson.setPostalAddress(postalAddressJson);
  }

  public Applicant createApplicantModel(ApplicantJson applicantJson) {
    Applicant applicantModel = new Applicant();
    applicantModel.setId(applicantJson.getId());
    applicantModel.setType(applicantJson.getType());
    applicantModel.setName(applicantJson.getName());
    applicantModel.setRegistryKey(applicantJson.getRegistryKey());
    applicantModel.setPhone(applicantJson.getPhone());
    applicantModel.setEmail(applicantJson.getEmail());
    if (applicantJson.getPostalAddress() != null) {
      applicantModel.setStreetAddress(applicantJson.getPostalAddress().getStreetAddress());
      applicantModel.setCity(applicantJson.getPostalAddress().getCity());
      applicantModel.setPostalCode(applicantJson.getPostalAddress().getPostalCode());
    }
    return applicantModel;
  }

  /**
   * Map the given Contact object into ContactJson
   *
   * @param c Contact object
   * @return Ui-domain Contact representation of the parameter
   */
  public ContactJson createContactJson(Contact c) {
    ContactJson json = new ContactJson();
    json.setId(c.getId());
    json.setApplicantId(c.getApplicantId());
    json.setName(c.getName());
    json.setStreetAddress(c.getStreetAddress());
    json.setPostalCode(c.getPostalCode());
    json.setCity(c.getCity());
    json.setEmail(c.getEmail());
    json.setPhone(c.getPhone());
    return json;
  }

  public Contact createContactModel(ContactJson json) {
    Contact contact = new Contact();
    contact.setId(json.getId());
    contact.setApplicantId(json.getApplicantId());
    contact.setName(json.getName());
    contact.setStreetAddress(json.getStreetAddress());
    contact.setPostalCode(json.getPostalCode());
    contact.setCity(json.getCity());
    contact.setEmail(json.getEmail());
    contact.setPhone(json.getPhone());
    return contact;
  }

  private CableInfoEntryJson createCableInfoEntryJson(CableInfoEntry cableInfoEntry) {
    CableInfoEntryJson cableInfoEntryJson = new CableInfoEntryJson();
    cableInfoEntryJson.setType(cableInfoEntry.getType());
    cableInfoEntryJson.setAdditionalInfo(cableInfoEntry.getAdditionalInfo());
    return cableInfoEntryJson;
  }

  private CableInfoEntry createCableInfoEntryModel(CableInfoEntryJson cableInfoEntryJson) {
    CableInfoEntry cableInfoEntry = new CableInfoEntry();
    cableInfoEntry.setType(cableInfoEntryJson.getType());
    cableInfoEntry.setAdditionalInfo(cableInfoEntryJson.getAdditionalInfo());
    return cableInfoEntry;
  }

  private ApplicantJson createApplicantJson(Applicant applicant) {
    ApplicantJson applicantJson = new ApplicantJson();
    mapApplicantToJson(applicantJson, applicant);
    return applicantJson;
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
}
