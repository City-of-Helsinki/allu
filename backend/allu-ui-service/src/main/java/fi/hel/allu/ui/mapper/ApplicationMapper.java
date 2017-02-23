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
    applicationDomain.setCreationTime(applicationJson.getCreationTime());
    applicationDomain.setStartTime(applicationJson.getStartTime());
    applicationDomain.setEndTime(applicationJson.getEndTime());
    applicationDomain.setApplicantId(applicationJson.getApplicant().getId());
    applicationDomain.setHandler(applicationJson.getHandler() != null ? applicationJson.getHandler().getId() : null);
    applicationDomain.setType(applicationJson.getType());
    applicationDomain.setKind(applicationJson.getKind());
    if (applicationJson.getApplicationTags() != null) {
      applicationDomain.setApplicationTags(applicationJson.getApplicationTags().stream()
          .map(t -> new ApplicationTag(t.getAddedBy(), t.getType(), t.getCreationTime())).collect(Collectors.toList()));
    }
    applicationDomain.setMetadataVersion(applicationJson.getMetadata().getVersion());
    applicationDomain.setStatus(applicationJson.getStatus());
    applicationDomain.setDecisionTime(applicationJson.getDecisionTime());
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
    applicationES.setCreationTime(applicationJson.getCreationTime());
    applicationES.setStartTime(applicationJson.getStartTime());
    applicationES.setEndTime(applicationJson.getEndTime());
    applicationES.setHandler(
        applicationJson.getHandler() != null ?
            new UserES(applicationJson.getHandler().getUserName(), applicationJson.getHandler().getRealName()) : null);
    applicationES.setType(new ApplicationTypeES(applicationJson.getType()));
    if (applicationJson.getApplicationTags() != null) {
      applicationES.setApplicationTags(
          applicationJson.getApplicationTags().stream().map(tag -> tag.getType().toString()).collect(Collectors.toList()));
    }
    applicationES.setStatus(new StatusTypeES(applicationJson.getStatus()));
    applicationES.setDecisionTime(applicationJson.getDecisionTime());
    applicationES.setApplicationTypeData(createApplicationTypeDataES(applicationJson));
    applicationES.setLocations(createLocationES(applicationJson.getLocations()));
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
    if (application.getApplicationTags() != null) {
      applicationJson.setApplicationTags(application.getApplicationTags().stream()
          .map(t -> new ApplicationTagJson(t.getAddedBy(), t.getType(), t.getCreationTime())).collect(Collectors.toList()));
    }
    applicationJson.setCreationTime(application.getCreationTime());
    applicationJson.setStartTime(application.getStartTime());
    applicationJson.setEndTime(application.getEndTime());
    applicationJson.setName(application.getName());
    applicationJson.setDecisionTime(application.getDecisionTime());
    if (application.getExtension() != null) {
      mapModelToJson(applicationJson, application);
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
  public void mapModelToJson(ApplicationJson applicationJson, Application application) {
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
      applicationJson.setExtension(createCableReportJson((CableReport) application.getExtension()));
      break;
    case AREA_RENTAL:
      break;
    case EXCAVATION_ANNOUNCEMENT:
      applicationJson
          .setExtension(createExcavationAnnouncementJson((ExcavationAnnouncement) application.getExtension()));
      break;
    case NOTE:
      applicationJson.setExtension(createNoteJson((Note) application.getExtension()));
      break;
    case PLACEMENT_CONTRACT:
      applicationJson.setExtension(createPlacementContractJson((PlacementContract) application.getExtension()));
      break;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      applicationJson.setExtension(createTrafficArrangementJson((TrafficArrangement) application.getExtension()));
      break;
    default:
      break;
    }

    if (applicationJson.getExtension() != null) {
      applicationJson.getExtension().setSpecifiers(
        application.getExtension() != null ? application.getExtension().getSpecifiers() : null);
    }
  }

  /**
   * Create a new <code>ApplicationExtension</code> model-domain object from given ui-domain object based on application type.
   * @param applicationJson Information that is mapped to model-domain object
   * @return created event object
   */
  public ApplicationExtension createExtensionModel(ApplicationJson applicationJson) {
    ApplicationExtension applicationExtension = null;
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
        applicationExtension = event;
      break;
      // short term rentals
    case SHORT_TERM_RENTAL:
        ShortTermRentalJson shortTermRentalJson = (ShortTermRentalJson) applicationJson.getExtension();
        ShortTermRental shortTermRental = new ShortTermRental();
        shortTermRental.setDescription(shortTermRentalJson.getDescription());
        shortTermRental.setCommercial(shortTermRentalJson.getCommercial());
        shortTermRental.setLargeSalesArea(shortTermRentalJson.getLargeSalesArea());
        applicationExtension = shortTermRental;
      break;
    case CABLE_REPORT:
      applicationExtension = createCableReportModel((CableReportJson) applicationJson.getExtension());
      break;
    case AREA_RENTAL:
      break;
    case EXCAVATION_ANNOUNCEMENT:
      applicationExtension = createExcavationAnnouncementModel(
          (ExcavationAnnouncementJson) applicationJson.getExtension());
      break;
    case NOTE:
      applicationExtension = createNoteModel((NoteJson) applicationJson.getExtension());
      break;
    case PLACEMENT_CONTRACT:
      applicationExtension = createPlacementContractModel((PlacementContractJson) applicationJson.getExtension());
      break;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      applicationExtension = createTrafficArrangementModel((TrafficArrangementJson) applicationJson.getExtension());
      break;
    default:
      break;
    }
    if (applicationExtension != null) {
      applicationExtension.setSpecifiers(applicationJson.getExtension().getSpecifiers());
    }
    return applicationExtension;
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

  /*
   * Map CableReportJson to CableReport
   */
  private CableReport createCableReportModel(CableReportJson cableReportJson) {
    CableReport cableReport = new CableReport();
    cableReport.setCableReportId(cableReportJson.getCableReportId());
    cableReport.setWorkDescription(cableReportJson.getWorkDescription());
    Optional.ofNullable(cableReportJson.getOwner())
        .ifPresent(owner -> cableReport.setOwner(createApplicantModel(owner)));
    Optional.ofNullable(cableReportJson.getContact())
        .ifPresent(contact -> cableReport.setContact(createContactModel(contact)));
    cableReport.setMapExtractCount(cableReportJson.getMapExtractCount());
    cableReport.setCableSurveyRequired(cableReportJson.getCableSurveyRequired());
    List<CableInfoEntry> infoEntries = Optional.ofNullable(cableReportJson.getInfoEntries())
        .orElse(Collections.emptyList()).stream().map(i -> createCableInfoEntryModel(i)).collect(Collectors.toList());
    cableReport.setInfoEntries(infoEntries);
    cableReport.setMapUpdated(cableReportJson.getMapUpdated());
    cableReport.setConstructionWork(cableReportJson.getConstructionWork());
    cableReport.setMaintenanceWork(cableReportJson.getMaintenanceWork());
    cableReport.setEmergencyWork(cableReportJson.getEmergencyWork());
    cableReport.setPropertyConnectivity(cableReportJson.getPropertyConnectivity());
    return cableReport;
  }

  /*
   * Map CableReport to CableReportJson
   */
  private CableReportJson createCableReportJson(CableReport cableReport) {
    CableReportJson cableReportJson = new CableReportJson();
    cableReportJson.setCableReportId(cableReport.getCableReportId());
    cableReportJson.setWorkDescription(cableReport.getWorkDescription());
    Optional.ofNullable(cableReport.getOwner())
        .ifPresent(owner -> cableReportJson.setOwner(createApplicantJson(owner)));
    Optional.ofNullable(cableReport.getContact())
        .ifPresent(contact -> cableReportJson.setContact(createContactJson(contact)));
    cableReportJson.setMapExtractCount(cableReport.getMapExtractCount());
    cableReportJson.setCableSurveyRequired(cableReport.isCableSurveyRequired());
    List<CableInfoEntryJson> infoEntries = Optional.ofNullable(cableReport.getInfoEntries())
        .orElse(Collections.emptyList()).stream().map(i -> createCableInfoEntryJson(i)).collect(Collectors.toList());
    cableReportJson.setInfoEntries(infoEntries);
    cableReportJson.setMapUpdated(cableReport.getMapUpdated());
    cableReportJson.setConstructionWork(cableReport.getConstructionWork());
    cableReportJson.setMaintenanceWork(cableReport.getMaintenanceWork());
    cableReportJson.setEmergencyWork(cableReport.getEmergencyWork());
    cableReportJson.setPropertyConnectivity(cableReport.getPropertyConnectivity());
    return cableReportJson;
  }

  private ExcavationAnnouncementJson createExcavationAnnouncementJson(ExcavationAnnouncement model) {
    ExcavationAnnouncementJson json = new ExcavationAnnouncementJson();
    json.setAdditionalInfo(model.getAdditionalInfo());
    json.setCableReportId(model.getCableReportId());
    json.setContractor(createApplicantJson(model.getContractor()));
    json.setGuaranteeEndTime(model.getGuaranteeEndTime());
    json.setResponsiblePerson(createContactJson(model.getResponsiblePerson()));
    Optional.ofNullable(model.getPropertyDeveloper())
        .map(developer -> createApplicantJson(developer))
        .ifPresent(developer -> json.setPropertyDeveloper(developer));
    Optional.ofNullable(model.getPropertyDeveloperContact())
        .map(contact -> createContactJson(contact))
        .ifPresent(contact -> json.setPropertyDeveloperContact(contact));
    json.setSummerTimeOperation(model.getSummerTimeOperation());
    json.setWinterTimeOperation(model.getWinterTimeOperation());
    json.setWorkFinished(model.getWorkFinished());
    json.setTrafficArrangements(model.getTrafficArrangements());
    json.setPksCard(model.getPksCard());
    json.setConstructionWork(model.getConstructionWork());
    json.setMaintenanceWork(model.getMaintenanceWork());
    json.setEmergencyWork(model.getEmergencyWork());
    json.setPropertyConnectivity(model.getPropertyConnectivity());
    json.setUnauthorizedWorkStartTime(model.getUnauthorizedWorkStartTime());
    json.setUnauthorizedWorkEndTime(model.getUnauthorizedWorkEndTime());
    return json;
  }

  private ExcavationAnnouncement createExcavationAnnouncementModel(ExcavationAnnouncementJson excavationAnnouncementJson) {
    ExcavationAnnouncement excavationAnnouncement = new ExcavationAnnouncement();
    excavationAnnouncement.setAdditionalInfo(excavationAnnouncementJson.getAdditionalInfo());
    excavationAnnouncement.setCableReportId(excavationAnnouncementJson.getCableReportId());
    excavationAnnouncement.setContractor(createApplicantModel(excavationAnnouncementJson.getContractor()));
    excavationAnnouncement.setGuaranteeEndTime(excavationAnnouncementJson.getGuaranteeEndTime());
    excavationAnnouncement.setResponsiblePerson(createContactModel(excavationAnnouncementJson.getResponsiblePerson()));
    Optional.ofNullable(excavationAnnouncementJson.getPropertyDeveloper())
        .map(developer -> createApplicantModel(developer))
        .ifPresent(developer -> excavationAnnouncement.setPropertyDeveloper(developer));
    Optional.ofNullable(excavationAnnouncementJson.getPropertyDeveloperContact())
        .map(contact -> createContactModel(contact))
        .ifPresent(contact -> excavationAnnouncement.setPropertyDeveloperContact(contact));
    excavationAnnouncement.setSummerTimeOperation(excavationAnnouncementJson.getSummerTimeOperation());
    excavationAnnouncement.setWinterTimeOperation(excavationAnnouncementJson.getWinterTimeOperation());
    excavationAnnouncement.setWorkFinished(excavationAnnouncementJson.getWorkFinished());
    excavationAnnouncement.setTrafficArrangements(excavationAnnouncementJson.getTrafficArrangements());
    excavationAnnouncement.setPksCard(excavationAnnouncementJson.getPksCard());
    excavationAnnouncement.setConstructionWork(excavationAnnouncementJson.getConstructionWork());
    excavationAnnouncement.setMaintenanceWork(excavationAnnouncementJson.getMaintenanceWork());
    excavationAnnouncement.setEmergencyWork(excavationAnnouncementJson.getEmergencyWork());
    excavationAnnouncement.setPropertyConnectivity(excavationAnnouncementJson.getPropertyConnectivity());
    excavationAnnouncement.setUnauthorizedWorkStartTime(excavationAnnouncementJson.getUnauthorizedWorkStartTime());
    excavationAnnouncement.setUnauthorizedWorkEndTime(excavationAnnouncementJson.getUnauthorizedWorkEndTime());
    return excavationAnnouncement;
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

  private NoteJson createNoteJson(Note note) {
    NoteJson noteJson = new NoteJson();
    noteJson.setDescription(note.getDescription());
    noteJson.setReoccurring(note.getReoccurring());
    return noteJson;
  }

  private Note createNoteModel(NoteJson noteJson) {
    Note note = new Note();
    note.setDescription(noteJson.getDescription());
    note.setReoccurring(noteJson.getReoccurring());
    return note;
  }

  private ApplicationExtensionJson createPlacementContractJson(PlacementContract placementContract) {
    PlacementContractJson placementContractJson = new PlacementContractJson();
    Optional.ofNullable(placementContract.getRepresentative())
            .map(representative -> createApplicantJson(representative))
            .ifPresent(representative -> placementContractJson.setRepresentative(representative));
    Optional.ofNullable(placementContract.getContact())
            .map(contact -> createContactJson(contact))
            .ifPresent(contact -> placementContractJson.setContact(contact));
    placementContractJson.setDiaryNumber(placementContract.getDiaryNumber());
    placementContractJson.setAdditionalInfo(placementContract.getAdditionalInfo());
    placementContractJson.setGeneralTerms(placementContract.getGeneralTerms());
    return placementContractJson;
  }

  private ApplicationExtension createPlacementContractModel(PlacementContractJson placementContractJson) {
    PlacementContract placementContract = new PlacementContract();
    Optional.ofNullable(placementContractJson.getRepresentative())
            .map(representative -> createApplicantModel(representative))
            .ifPresent(representative -> placementContract.setRepresentative(representative));
    Optional.ofNullable(placementContractJson.getContact())
            .map(contact -> createContactModel(contact))
            .ifPresent(contact -> placementContract.setContact(contact));
    placementContract.setDiaryNumber(placementContractJson.getDiaryNumber());
    placementContract.setAdditionalInfo(placementContractJson.getAdditionalInfo());
    placementContract.setGeneralTerms(placementContractJson.getGeneralTerms());
    return placementContract;
  }

  private TrafficArrangementJson createTrafficArrangementJson(TrafficArrangement trafficArrangement) {
    TrafficArrangementJson trafficArrangementJson = new TrafficArrangementJson();
    trafficArrangementJson.setContractor(createApplicantJson(trafficArrangement.getContractor()));
    trafficArrangementJson.setResponsiblePerson(createContactJson(trafficArrangement.getResponsiblePerson()));
    trafficArrangementJson.setPksCard(trafficArrangement.getPksCard());
    trafficArrangementJson.setWorkFinished(trafficArrangement.getWorkFinished());
    trafficArrangementJson.setAdditionalInfo(trafficArrangement.getAdditionalInfo());
    trafficArrangementJson.setTrafficArrangements(trafficArrangement.getTrafficArrangements());
    return trafficArrangementJson;
  }

  private TrafficArrangement createTrafficArrangementModel(TrafficArrangementJson trafficArrangementJson) {
    TrafficArrangement trafficArrangement = new TrafficArrangement();
    trafficArrangement.setContractor(createApplicantModel(trafficArrangementJson.getContractor()));
    trafficArrangement.setResponsiblePerson(createContactModel(trafficArrangementJson.getResponsiblePerson()));
    trafficArrangement.setPksCard(trafficArrangementJson.getPksCard());
    trafficArrangement.setWorkFinished(trafficArrangementJson.getWorkFinished());
    trafficArrangement.setAdditionalInfo(trafficArrangementJson.getAdditionalInfo());
    trafficArrangement.setTrafficArrangements(trafficArrangementJson.getTrafficArrangements());
    return trafficArrangement;
  }

  private ApplicantJson createApplicantJson(Applicant applicant) {
    ApplicantJson applicantJson = new ApplicantJson();
    mapApplicantToJson(applicantJson, applicant);
    return applicantJson;
  }

  private List<LocationES> createLocationES(List<LocationJson> locationJsons) {
    if (locationJsons != null) {
      return locationJsons.stream()
          .filter(l -> l.getPostalAddress() != null)
          .map(json -> new LocationES(
              json.getPostalAddress().getStreetAddress(),
              json.getPostalAddress().getPostalCode(),
              json.getPostalAddress().getCity(),
              getCityDistrictId(json)))
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  private Integer getCityDistrictId(LocationJson locationJson) {
    return Optional.ofNullable(locationJson.getCityDistrictIdOverride()).orElse(locationJson.getCityDistrictId());
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
