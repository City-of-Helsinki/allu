package fi.hel.allu.servicecore.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.collect.Lists;
import fi.hel.allu.common.domain.geometry.Constants;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.domain.history.ApplicationForHistory;
import fi.hel.allu.servicecore.domain.history.ApplicationTagForHistory;
import fi.hel.allu.servicecore.mapper.extension.*;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.servicecore.util.AddressMaker;
import fi.hel.allu.servicecore.util.GeometrySimplifier;
import org.geolatte.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


@Component
public class ApplicationMapper {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationMapper.class);

  private final CustomerMapper customerMapper;
  private final LocationService locationService;
  private final ObjectWriter geometryWriter;
  private final List<ZoomLevelSizeBounds> zoomLevelSizeBoundsList;
  private final CommentMapper commentMapper;
  private final BiConsumer<LocationES, Location> setLocationAddress;
  private final BiConsumer<LocationES, LocationJson> setLocationJsonAddress =
          (locationES, locationJson) -> locationES.setAddress(locationJson.getAddress());

  @Autowired
  public ApplicationMapper(CustomerMapper customerMapper, LocationService locationService,
                           AddressMaker addressMaker, CommentMapper commentMapper) {
    this.customerMapper = customerMapper;
    this.locationService = locationService;
    this.commentMapper = commentMapper;
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule("CustomGeometrySerializer", new Version(1, 0, 0, null, null, null));
    module.addSerializer(Geometry.class, new GeometrySerializerProxy());
    mapper.registerModule(module);
    geometryWriter = mapper.writerFor(Geometry.class);
    zoomLevelSizeBoundsList = GeometrySimplifier.generateZoomLevelSizeBoundsList();
    setLocationAddress = (locationES, location) -> locationES.setAddress(addressMaker.getAddress(location));
  }

  public ApplicationForHistory mapJsonToHistory(ApplicationJson application) {
    ApplicationForHistory history = new ApplicationForHistory();
    history.setId(application.getId());
    history.setApplicationId(application.getApplicationId());
    history.setStatus(application.getStatus());
    history.setType(application.getType());
    history.setKindsWithSpecifiers(application.getKindsWithSpecifiers());
    if (application.getApplicationTags() != null) {
      history.setApplicationTags(application.getApplicationTags()
                                         .stream()
                                         .map(t -> new ApplicationTagForHistory(t.getType()))
                                         .collect(Collectors.toList()));
    }
    history.setMetadataVersion(application.getMetadataVersion());
    history.setCreationTime(application.getCreationTime());
    history.setReceivedTime(application.getReceivedTime());
    history.setStartTime(application.getStartTime());
    history.setEndTime(application.getEndTime());
    history.setRecurringEndTime(application.getRecurringEndTime());
    history.setName(application.getName());
    history.setDecisionTime(application.getDecisionTime());
    history.setDecisionMaker(getUserRealName(application.getDecisionMaker()));
    history.setExtension(application.getExtension());
    history.setDecisionPublicityType(application.getDecisionPublicityType());
    history.setDecisionDistributionList(application.getDecisionDistributionList());
    history.setOwner(getUserRealName(application.getOwner()));
    history.setHandler(getUserRealName(application.getHandler()));
    history.setNotBillable(application.getNotBillable());
    history.setNotBillableReason(application.getNotBillableReason());
    history.setCustomersWithContacts(customersToHistory(application.getCustomersWithContacts()));
    history.setInvoiceRecipientId(application.getInvoiceRecipientId());
    history.setReplacedByApplicationId(application.getReplacedByApplicationId());
    history.setReplacesApplicationId(application.getReplacesApplicationId());
    history.setCustomerReference(application.getCustomerReference());
    history.setInvoicingDate(application.getInvoicingDate());
    history.setInvoiced(application.getInvoiced());
    history.setSkipPriceCalculation(application.getSkipPriceCalculation());
    history.setProject(application.getProject());
    history.setExternalOwnerId(application.getExternalOwnerId());
    history.setIdentificationNumber(application.getIdentificationNumber());
    history.setLocations(application.getLocations());
    return history;
  }

  private Map<CustomerRoleType, CustomerWithContactsJson> customersToHistory(List<CustomerWithContactsJson> customers) {
    Map<CustomerRoleType, CustomerWithContactsJson> customerMap = new HashMap<>();
    if (customers != null) {
      customers.forEach(c -> customerMap.put(c.getRoleType(), c));
    }
    return customerMap;
  }

  private String getUserRealName(UserJson user) {
    if (user != null) {
      return user.getRealName();
    }
    return null;
  }

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
    applicationDomain.setReceivedTime(applicationJson.getReceivedTime());
    applicationDomain.setStartTime(applicationJson.getStartTime());
    applicationDomain.setEndTime(applicationJson.getEndTime());
    applicationDomain.setRecurringEndTime(applicationJson.getRecurringEndTime());
    applicationDomain.setCustomersWithContacts(customerMapper.createWithContactsModel(applicationJson.getCustomersWithContacts()));
    applicationDomain.setOwner(applicationJson.getOwner() != null ? applicationJson.getOwner().getId() : null);
    applicationDomain.setHandler(applicationJson.getHandler() != null ? applicationJson.getHandler().getId() : null);
    applicationDomain.setType(applicationJson.getType());
    applicationDomain.setKindsWithSpecifiers(applicationJson.getKindsWithSpecifiers());
    applicationDomain.setApplicationTags(createTagModel(applicationJson.getApplicationTags()));
    applicationDomain.setMetadataVersion(applicationJson.getMetadataVersion());
    applicationDomain.setStatus(applicationJson.getStatus());
    applicationDomain.setTargetState(applicationJson.getTargetState());
    applicationDomain.setDecisionTime(applicationJson.getDecisionTime());
    if (applicationJson.getExtension() != null) {
      applicationDomain.setExtension(createExtensionModel(applicationJson));
    }
    applicationDomain.setDecisionPublicityType(applicationJson.getDecisionPublicityType());
    applicationDomain.setCalculatedPrice(applicationJson.getCalculatedPrice());
    applicationDomain.setNotBillable(applicationJson.getNotBillable());
    applicationDomain.setNotBillableReason(applicationJson.getNotBillableReason());
    applicationDomain.setInvoiceRecipientId(applicationJson.getInvoiceRecipientId());
    applicationDomain.setCustomerReference(applicationJson.getCustomerReference());
    applicationDomain.setInvoicingDate(applicationJson.getInvoicingDate());
    applicationDomain.setSkipPriceCalculation(applicationJson.getSkipPriceCalculation());
    applicationDomain.setExternalOwnerId(applicationJson.getExternalOwnerId());
    applicationDomain.setClientApplicationData(createClientApplicationDataModel(applicationJson.getClientApplicationData()));
    applicationDomain.setIdentificationNumber(applicationJson.getIdentificationNumber());
    applicationDomain.setLocations(LocationMapper.createLocationModel(applicationJson.getId(), applicationJson.getLocations()));
    applicationDomain.setVersion(applicationJson.getVersion());
    return applicationDomain;
  }

  /**
   * Create a new <code>ApplicationES</code> elasticsearch-domain object from given ui-domain object
   * @param applicationJson Information that is mapped to search-domain object
   * @return created applicationES object
   */
  public ApplicationES createApplicationESModel(ApplicationJson applicationJson) {
    ApplicationES applicationES = createApplicationESGeneric(applicationJson, setLocationJsonAddress);
    if (applicationJson.getProject() != null) {
      CompactProjectES project = new CompactProjectES();
      project.setIdentifier(applicationJson.getProject().getIdentifier());
      project.setId(applicationJson.getProject().getId());
      applicationES.setProject(project);
    }
    applicationES.setOwner(
            applicationJson.getOwner() != null ?
                    new UserES(applicationJson.getOwner().getUserName(), applicationJson.getOwner().getRealName()) : null);
    applicationES.setNrOfComments(applicationJson.getComments() != null ? applicationJson.getComments().size() : 0);
    applicationES.setLatestComment(commentMapper.getLatestComment(applicationJson.getComments()));
    applicationES.setTerminationTime(TimeUtil.dateToMillis(applicationJson.getTerminationTime()));
    return applicationES;
  }

  public ApplicationES createApplicationESModel(Application application) {
    ApplicationES applicationES = createApplicationESGeneric(application, setLocationAddress);
    return applicationES;
  }

  private <T extends ApplicationInterface, U extends LocationInterface> ApplicationES createApplicationESGeneric(
          T application, BiConsumer<LocationES, U> setLocationAddress){
    ApplicationES applicationES = new ApplicationES();
    applicationES.setId(application.getId());
    applicationES.setApplicationId(application.getApplicationId());
    applicationES.setName(application.getName());
    applicationES.setCreationTime(TimeUtil.dateToMillis(application.getCreationTime()));
    applicationES.setReceivedTime(TimeUtil.dateToMillis(application.getReceivedTime()));
    applicationES.setStartTime(TimeUtil.dateToMillis(application.getStartTime()));
    applicationES.setEndTime(TimeUtil.dateToMillis(application.getEndTime()));
    if (application.getStartTime() != null && application.getEndTime() != null) {
      ZonedDateTime recurringEndTime =
              application.getRecurringEndTime() == null ? application.getEndTime() : application.getRecurringEndTime();
      RecurringApplication recurringApplication =
              new RecurringApplication(application.getStartTime(), application.getEndTime(), recurringEndTime);
      applicationES.setRecurringApplication(recurringApplication);
    }
    applicationES.setType(new ApplicationTypeES(application.getType()));
    applicationES.setApplicationTags(createTagES(application.getApplicationTags()));
    applicationES.setStatus(new StatusTypeES(application.getStatus()));
    applicationES.setDecisionTime(TimeUtil.dateToMillis(application.getDecisionTime()));
    applicationES.setApplicationTypeData(createApplicationTypeDataES(application));
    applicationES.setLocations(createLocationES(application.getLocations(), setLocationAddress));
    Map<CustomerRoleType, CustomerWithContactsES> roleToCwcES = application.getCustomersWithContacts().stream()
            .collect(Collectors.toMap(CustomerWithContactsI::getRoleType, customerMapper::createWithContactsES));
    applicationES.setCustomers(new RoleTypedCustomerES(roleToCwcES));
    applicationES.setIdentificationNumber(application.getIdentificationNumber());
    applicationES.setOwnerNotification(application.getOwnerNotification());
    return applicationES;
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object. Does not handle
   * references to other objects like
   * customer.
   *
   * @param application
   */
  public ApplicationJson mapApplicationToJson(Application application) {
    ApplicationJson applicationJson = defaultApplicationToJsonMapping(application);
    applicationJson.setCustomersWithContacts(customerMapper.createWithContactsJson(application));
    applicationJson.setClientApplicationData(createClientApplicationDataJson(application.getClientApplicationData()));
    return applicationJson;
  }

  public ApplicationJson mapApplicationToJson(Application application, Map<Integer, CodeSet> codeSetMap) {
    ApplicationJson applicationJson = defaultApplicationToJsonMapping(application);
    applicationJson.setCustomersWithContacts(customerMapper.createWithContactsJson(application, codeSetMap));
    applicationJson.setClientApplicationData(createClientApplicationDataJson(application.getClientApplicationData(), codeSetMap));
    return applicationJson;
  }

  private ApplicationJson defaultApplicationToJsonMapping(Application application) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setId(application.getId());
    applicationJson.setApplicationId(application.getApplicationId());
    applicationJson.setStatus(application.getStatus());
    applicationJson.setType(application.getType());
    applicationJson.setKindsWithSpecifiers(application.getKindsWithSpecifiers());
    applicationJson.setApplicationTags(createTagsJson(application.getApplicationTags()));
    applicationJson.setMetadataVersion(application.getMetadataVersion());
    applicationJson.setCreationTime(application.getCreationTime());
    applicationJson.setReceivedTime(application.getReceivedTime());
    applicationJson.setStartTime(application.getStartTime());
    applicationJson.setEndTime(application.getEndTime());
    applicationJson.setRecurringEndTime(application.getRecurringEndTime());
    applicationJson.setName(application.getName());
    applicationJson.setDecisionTime(application.getDecisionTime());
    if (application.getExtension() != null) {
      applicationJson.setExtension(createExtensionJson(application));
    }
    applicationJson.setDecisionPublicityType(application.getDecisionPublicityType());
    if (application.getDecisionDistributionList() != null) {
      applicationJson.setDecisionDistributionList(application.getDecisionDistributionList().stream()
                                                          .map(dEntry -> createDistributionEntryJson(dEntry)).collect(
                      Collectors.toList()));
    }
    applicationJson.setCalculatedPrice(application.getCalculatedPrice());
    applicationJson.setNotBillable(application.getNotBillable());
    applicationJson.setNotBillableReason(application.getNotBillableReason());
    applicationJson.setInvoiceRecipientId(application.getInvoiceRecipientId());
    applicationJson.setReplacedByApplicationId(application.getReplacedByApplicationId());
    applicationJson.setReplacesApplicationId(application.getReplacesApplicationId());
    applicationJson.setCustomerReference(application.getCustomerReference());
    applicationJson.setInvoicingDate(application.getInvoicingDate());
    applicationJson.setInvoiced(application.getInvoiced());
    applicationJson.setSkipPriceCalculation(application.getSkipPriceCalculation());
    if (application.getProjectId() != null) {
      ProjectJson project = new ProjectJson();
      project.setId(application.getProjectId());
      applicationJson.setProject(project);
    }
    applicationJson.setExternalOwnerId(application.getExternalOwnerId());
    applicationJson.setIdentificationNumber(application.getIdentificationNumber());
    applicationJson.setLocations(LocationMapper.mapToLocationJsons(application.getLocations()));
    applicationJson.setInvoicingChanged(application.isInvoicingChanged());
    applicationJson.setTargetState(application.getTargetState());
    applicationJson.setExternalApplicationId(application.getExternalApplicationId());
    applicationJson.setInvoicingPeriodLength(application.getInvoicingPeriodLength());
    applicationJson.setVersion(application.getVersion());
    applicationJson.setOwnerNotification(application.getOwnerNotification());
    return applicationJson;
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object
   * @param application
   * @return created Json application extension
   */
  public ApplicationExtensionJson createExtensionJson(Application application) {
    switch (application.getType()) {
    case EVENT:
        return EventMapper.modelToJson(application);
    case SHORT_TERM_RENTAL:
        return ShortTermRentalMapper.modelToJson((ShortTermRental) application.getExtension());
    case CABLE_REPORT:
        return CableReportMapper.modelToJson(application);
    case AREA_RENTAL:
      return AreaRentalMapper.modelToJson((AreaRental) application.getExtension());
    case EXCAVATION_ANNOUNCEMENT:
      return ExcavationAnnouncementMapper.modelToJson((ExcavationAnnouncement) application.getExtension());
    case NOTE:
      return NoteMapper.modelToJson((Note) application.getExtension());
    case PLACEMENT_CONTRACT:
      return PlacementContractMapper.modelToJson((PlacementContract) application.getExtension());
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      return TrafficArrangementMapper.modelToJson((TrafficArrangement) application.getExtension());
      default:
        throw new IllegalArgumentException("No model to json mapper for extension type " + application.getType());
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
      {
        Event event = EventMapper.jsonToModel((EventJson) applicationJson.getExtension());
        // Make sure promotion events have promotion nature:
        Optional.ofNullable(applicationJson.getKindsWithSpecifiers())
            .filter(m -> m.containsKey(ApplicationKind.PROMOTION))
            .ifPresent(m -> event.setNature(EventNature.PROMOTION));
        return event;
      }
      case SHORT_TERM_RENTAL:
        return  ShortTermRentalMapper.jsonToModel((ShortTermRentalJson) applicationJson.getExtension());
      case CABLE_REPORT:
        return CableReportMapper.jsonToModel((CableReportJson) applicationJson.getExtension(), applicationJson.getCustomersWithContacts());
      case AREA_RENTAL:
        return AreaRentalMapper.jsonToModel((AreaRentalJson) applicationJson.getExtension());
      case EXCAVATION_ANNOUNCEMENT:
        return ExcavationAnnouncementMapper.jsonToModel((ExcavationAnnouncementJson) applicationJson.getExtension());
      case NOTE:
        return NoteMapper.jsonToModel((NoteJson) applicationJson.getExtension());
      case PLACEMENT_CONTRACT:
        return PlacementContractMapper.jsonToModel((PlacementContractJson) applicationJson.getExtension());
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return TrafficArrangementMapper.jsonToModel((TrafficArrangementJson) applicationJson.getExtension());
      default:
        throw new IllegalArgumentException("No json to model mapper for extension type " + applicationJson.getType());
    }
  }

  /**
   * Create a new <code>ApplicationTypeDataES</code> search-domain object from given ui-domain object.
   * @param application Information that is mapped to search-domain object
   * @return created ApplicationTypeDataES object
   */
  public <T extends ApplicationInterface> List<ESFlatValue> createApplicationTypeDataES(T application) {
    return createApplicationTypeDataES(application.getExtension(),
                                       "Unexpected error while mapping Application Type of " + application
                                               + " as application type", application.getType().name());
  }

  public List<ESFlatValue> createApplicationTypeDataES(Object applicationExtension, String errorMessage, String typeName) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    String json;
    try {
      json = objectMapper.writeValueAsString(applicationExtension);
    } catch (JsonProcessingException e) {
      logger.error(errorMessage);
      throw new RuntimeException(e);
    }

    Map<String, Object> flattenedMap = new JsonFlattener(json).withSeparator('-').flattenAsMap();
    Map<String, Object> flattenedMapNoNulls = flattenedMap.entrySet().stream()
            .filter(e -> e.getValue() != null)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    List<ESFlatValue> flatList = flattenedMapNoNulls.entrySet().stream()
            .map(e -> ESFlatValue.mapValue(typeName, e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    return flatList;
  }

  public DistributionEntry createDistributionEntryModel(DistributionEntryJson distributionEntryJson) {
    DistributionEntry distributionEntry = new DistributionEntry();
    distributionEntry.setDistributionType(distributionEntryJson.getDistributionType());
    distributionEntry.setName(distributionEntryJson.getName());
    distributionEntry.setEmail(distributionEntryJson.getEmail());
    distributionEntry.setPostalAddress(ApplicationCommonMapper.createPostalAddressModel(distributionEntryJson.getPostalAddress()));
    return distributionEntry;
  }

  public List<ApplicationTag> createTagModel(List<ApplicationTagJson> tagJsons) {
    if (tagJsons == null) {
      return null;
    }
    return tagJsons.stream()
            .map(t -> new ApplicationTag(t.getAddedBy(), t.getType(), t.getCreationTime()))
            .collect(Collectors.toList());
  }

  public List<ApplicationTagJson> createTagsJson(List<ApplicationTag> tags) {
    if (tags == null) {
      return null;
    }
    return tags.stream()
            .map(this::createTagJson)
            .collect(Collectors.toList());
  }

  public ApplicationTagJson createTagJson(ApplicationTag tag) {
    return new ApplicationTagJson(tag.getAddedBy(), tag.getType(), tag.getCreationTime());
  }

  public <T extends ApplicationTagInterface> List<String> createTagES(List<T> tagJsons) {
    if (tagJsons == null) {
      return null;
    }

    return tagJsons.stream().map(tag -> tag.getType().toString()).collect(Collectors.toList());
  }

  public ApplicationIdentifierJson mapApplicationIdentifierToJson(ApplicationIdentifier applicationIdentifier) {
    return new ApplicationIdentifierJson(applicationIdentifier.getId(),
                                         applicationIdentifier.getApplicationId(),
                                         applicationIdentifier.getIdentificationNumber());
  }

  private <T extends LocationInterface> List<LocationES> createLocationES(List<T> locations, BiConsumer<LocationES, T> setLocationAddress) {
    if (locations != null) {
      List<LocationES> locationESList = new ArrayList<>();
      locations.forEach(location -> locationESList.addAll(this.createLocationESSimplified(location, setLocationAddress)));
      return locationESList;
    } else {
      return null;
    }
  }

  private <T extends LocationInterface> List<LocationES> createLocationESSimplified(T location, BiConsumer<LocationES, T> setLocationAddress) {
    if (location.getGeometry().getNumPoints() == 1) {
      return Lists.newArrayList(createLocationESFromLocation(location, setLocationAddress));
    }
    List<LocationES> locationsWithSimplifiedGeo = new ArrayList<>();
    // Go through all complexities
    for (int i = GeometryComplexity.FULL.ordinal(); i >= GeometryComplexity.POINT.ordinal(); i--) {
      LocationES locationESWithZoom = createLocationESFromLocation(location, setLocationAddress, i);
      if (locationESWithZoom == null) {
        logger.debug("No need to add complexity " + GeometryComplexity.values()[i]);
        continue;
      }
      locationsWithSimplifiedGeo.add(locationESWithZoom);
    }
    return locationsWithSimplifiedGeo;
  }

  private <T extends LocationInterface> LocationES createLocationESFromLocation(T location, BiConsumer<LocationES, T> setLocationAddress) {
    return createLocationES(location, setLocationAddress, null);
  }

  private <T extends LocationInterface> LocationES createLocationESFromLocation(T location, BiConsumer<LocationES, T> setLocationAddress, Integer complexity) {
    return createLocationES(location, setLocationAddress, complexity);
  }

  private <T extends LocationInterface> LocationES createLocationES(T location, BiConsumer<LocationES, T> setLocationAddress, Integer complexity) {
    LocationES locationEs = new LocationES();
    locationEs.setLocationKey(location.getLocationKey());
    Optional.ofNullable(location.getPostalAddress()).ifPresent(address -> {
      locationEs.setStreetAddress(address.getStreetAddress());
      locationEs.setPostalCode(address.getPostalCode());
      locationEs.setCity(address.getCity());
    });
    locationEs.setCityDistrictId(getCityDistrictId(location));
    locationEs.setAdditionalInfo(location.getAdditionalInfo());
    setLocationAddress.accept(locationEs, location);
    Geometry jsonGeometry = location.getGeometry();
    if (!simplifyGeometry(locationEs, jsonGeometry, location.getGeometry(), complexity)) {
      return null;
    }
    Geometry searchGeometry = null;
    try { // needed to put on place because some geometry locations give on transformation POLYGON((∞ ∞,∞ ∞,∞ ∞,∞ ∞,∞ ∞)
      searchGeometry = locationService.transformCoordinates(jsonGeometry, Constants.ELASTIC_SEARCH_SRID);
    } catch (RestClientException e) {
      logger.error("Location geometry transformation failed. location_id: {}, geometry: {}", location.getId(), jsonGeometry.asText());
    }
    locationEs.setSearchGeometry(searchGeometry);
    return locationEs;
  }

  /**
   * Handle simplification of geometry if such is needed.
   * @return boolean telling if location should be added to location list
   */
  private boolean simplifyGeometry(
    LocationES locationEs, Geometry jsonGeometry, Geometry geometry, Integer complexity
  ) {
    ShouldSimplifyWithMinZoomLevel simplifyWithMinZoomLevel = (complexity == null) ? null :
      GeometrySimplifier.shouldSimplifyWithComplexity(geometry, complexity, zoomLevelSizeBoundsList);
    if (complexity != null){
      if (simplifyWithMinZoomLevel == null) {
        logger.debug("No geometry to add to locationES");
        return false;
      }
      if (simplifyWithMinZoomLevel.shouldSimplify()) {
        // Get actual simplified geometry
        jsonGeometry = locationService.simplifyGeometry(geometry, simplifyWithMinZoomLevel.getMinZoomLevel());
      } else {
        if (simplifyWithMinZoomLevel.getGeometry() != null) {
          // Use a Point geometry of the provided geometry
          jsonGeometry = simplifyWithMinZoomLevel.getGeometry();
        } else {
          jsonGeometry = geometry;
        }
      }
    }
    locationEs.setGeometry(toJsonString(jsonGeometry));
    // If here geometryWithMinZoom == null, then complexity == null -> we want full geometry with zoom 1
    locationEs.setZoom(simplifyWithMinZoomLevel == null ? 1 : Optional.of(simplifyWithMinZoomLevel.getMinZoomLevel()).orElse(1));
    return true;
  }

  private String toJsonString(Geometry geometry) {
     try {
      return geometryWriter.writeValueAsString(geometry);
    } catch (JsonProcessingException e) {
      logger.warn("Failed to write geometry to ES model", e);
      return null;
    }
  }

  private Integer getCityDistrictId(LocationInterface location) {
    return Optional.ofNullable(location.getCityDistrictIdOverride()).orElse(location.getCityDistrictId());
  }

  public List<DistributionEntryJson> createDistributionEntryJsonList(List<DistributionEntry> entries) {
    if (entries == null) {
      return Collections.emptyList();
    }
    return entries.stream().map(this::createDistributionEntryJson).collect(Collectors.toList());
  }

  public DistributionEntryJson createDistributionEntryJson(DistributionEntry distributionEntry) {
    DistributionEntryJson distributionEntryJson = new DistributionEntryJson();
    distributionEntryJson.setId(distributionEntry.getId());
    distributionEntryJson.setDistributionType(distributionEntry.getDistributionType());
    distributionEntryJson.setName(distributionEntry.getName());
    distributionEntryJson.setEmail(distributionEntry.getEmail());
    distributionEntryJson.setPostalAddress(ApplicationCommonMapper.createPostalAddressJson(distributionEntry.getPostalAddress()));
    return distributionEntryJson;
  }

  private ClientApplicationData createClientApplicationDataModel(ClientApplicationDataJson clientApplicationDataJson) {
    ClientApplicationData data = null;
    if (clientApplicationDataJson != null) {
      CustomerWithContacts customer = customerMapper.createSingleCustomerWithContactsModel(clientApplicationDataJson.getCustomer());
      CustomerWithContacts representative = customerMapper.createSingleCustomerWithContactsModel(clientApplicationDataJson.getRepresentative());
      CustomerWithContacts contractor = customerMapper.createSingleCustomerWithContactsModel(clientApplicationDataJson.getContractor());
      CustomerWithContacts propertyDeveloper = customerMapper.createSingleCustomerWithContactsModel(clientApplicationDataJson.getPropertyDeveloper());
      Customer invoicingCustomer = customerMapper.createCustomerModel(clientApplicationDataJson.getInvoicingCustomer());
      data = new ClientApplicationData(customer, invoicingCustomer, representative, contractor, propertyDeveloper,
          clientApplicationDataJson.getClientApplicationKind());
    }
    return data;
  }

  private ClientApplicationDataJson createClientApplicationDataJson(ClientApplicationData clientApplicationData, Map<Integer, CodeSet> codesets) {
    ClientApplicationDataJson result = null;
    if (clientApplicationData != null) {
      CustomerWithContactsJson customer = customerMapper.createWithContactsJson(clientApplicationData.getCustomer(), codesets);
      CustomerJson invoicingCustomer = customerMapper.createCustomerJson(clientApplicationData.getInvoicingCustomer(), codesets);
      CustomerWithContactsJson representative = customerMapper.createWithContactsJson(clientApplicationData.getRepresentative(), codesets);
      CustomerWithContactsJson contractor = customerMapper.createWithContactsJson(clientApplicationData.getContractor(), codesets);
      CustomerWithContactsJson propertyDeveloper = customerMapper.createWithContactsJson(clientApplicationData.getPropertyDeveloper(), codesets);
      result = new ClientApplicationDataJson(customer, invoicingCustomer, representative, clientApplicationData.getClientApplicationKind());
      result.setContractor(contractor);
      result.setPropertyDeveloper(propertyDeveloper);
    }
    return result;
  }

  private ClientApplicationDataJson createClientApplicationDataJson(ClientApplicationData clientApplicationData) {
    ClientApplicationDataJson result = null;
    if (clientApplicationData != null) {
      CustomerWithContactsJson customer = customerMapper.createWithContactsJson(clientApplicationData.getCustomer());
      CustomerJson invoicingCustomer = customerMapper.createCustomerJson(clientApplicationData.getInvoicingCustomer());
      CustomerWithContactsJson representative = customerMapper.createWithContactsJson(clientApplicationData.getRepresentative());
      CustomerWithContactsJson contractor = customerMapper.createWithContactsJson(clientApplicationData.getContractor());
      CustomerWithContactsJson propertyDeveloper = customerMapper.createWithContactsJson(clientApplicationData.getPropertyDeveloper());
      result = new ClientApplicationDataJson(customer, invoicingCustomer, representative, clientApplicationData.getClientApplicationKind());
      result.setContractor(contractor);
      result.setPropertyDeveloper(propertyDeveloper);
    }
    return result;
  }

  /**
   *
   * @param <T>
   * @param createJson
   * @return
   */
  public <T extends CreateApplicationJson> ApplicationJson mapCreateJsonToApplicationJson(T createJson) {
    // map base class part
    ApplicationJson applicationJson = new ApplicationJson(createJson);

    // map simple id reference properties
    applicationJson.setProject(new ProjectJson(createJson.getProjectId()));
    applicationJson.setOwner(new UserJson(createJson.getOwnerId()));
    applicationJson.setHandler(new UserJson(createJson.getHandlerId()));

    // map customers
    Map<CustomerRoleType, CreateCustomerWithContactsJson> customersByRoleType = createJson.getAllCustomersWithContactsByCustomerRoleType();

    List<CustomerWithContactsJson> customersWithContacts = customersByRoleType.entrySet().stream()
      .map(entry -> customerMapper.createCustomerWithContactsJson(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());

    applicationJson.setCustomersWithContacts(customersWithContacts);

    return applicationJson;
  }

  public static AnonymizableApplicationJson mapToAnonymizableApplicationJson(AnonymizableApplication anonymizableApplication) {
    return new AnonymizableApplicationJson(
      anonymizableApplication.getId(),
      anonymizableApplication.getApplicationId(),
      anonymizableApplication.getApplicationType(),
      anonymizableApplication.getStartTime(),
      anonymizableApplication.getEndTime(),
      anonymizableApplication.getChangeType(),
      anonymizableApplication.getChangeTime()
    );
  }
}
