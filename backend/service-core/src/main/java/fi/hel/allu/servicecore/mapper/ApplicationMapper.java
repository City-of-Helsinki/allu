package fi.hel.allu.servicecore.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wnameless.json.flattener.JsonFlattener;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.extension.*;
import fi.hel.allu.servicecore.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApplicationMapper {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationMapper.class);

  private final CustomerMapper customerMapper;
  private final UserService userService;

  @Autowired
  public ApplicationMapper(CustomerMapper customerMapper, UserService userService) {
    this.customerMapper = customerMapper;
    this.userService = userService;
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
    applicationDomain.setDecisionTime(applicationJson.getDecisionTime());
    if (applicationJson.getExtension() != null) {
      applicationDomain.setExtension(createExtensionModel(applicationJson));
    }
    applicationDomain.setDecisionPublicityType(applicationJson.getDecisionPublicityType());
    if (applicationJson.getDecisionDistributionList() != null) {
      applicationDomain.setDecisionDistributionList(applicationJson.getDecisionDistributionList().stream()
          .map(dEntry -> createDistributionEntryModel(dEntry)).collect(Collectors.toList()));
    }
    applicationDomain.setCalculatedPrice(applicationJson.getCalculatedPrice());
    applicationDomain.setNotBillable(applicationJson.getNotBillable());
    applicationDomain.setNotBillableReason(applicationJson.getNotBillableReason());
    applicationDomain.setInvoiceRecipientId(applicationJson.getInvoiceRecipientId());
    applicationDomain.setCustomerReference(applicationJson.getCustomerReference());
    applicationDomain.setInvoicingDate(applicationJson.getInvoicingDate());
    applicationDomain.setSkipPriceCalculation(applicationJson.getSkipPriceCalculation());
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
    applicationES.setCreationTime(TimeUtil.dateToMillis(applicationJson.getCreationTime()));
    applicationES.setStartTime(TimeUtil.dateToMillis(applicationJson.getStartTime()));
    applicationES.setEndTime(TimeUtil.dateToMillis(applicationJson.getEndTime()));
    if (applicationJson.getStartTime() != null && applicationJson.getEndTime() != null) {
      ZonedDateTime recurringEndTime =
          applicationJson.getRecurringEndTime() == null ? applicationJson.getEndTime() : applicationJson.getRecurringEndTime();
      RecurringApplication recurringApplication =
          new RecurringApplication(applicationJson.getStartTime(), applicationJson.getEndTime(), recurringEndTime);
      applicationES.setRecurringApplication(recurringApplication);
    }
    applicationES.setOwner(
        applicationJson.getOwner() != null ?
            new UserES(applicationJson.getOwner().getUserName(), applicationJson.getOwner().getRealName()) : null);
    applicationES.setType(new ApplicationTypeES(applicationJson.getType()));
    applicationES.setApplicationTags(createTagES(applicationJson.getApplicationTags()));
    applicationES.setStatus(new StatusTypeES(applicationJson.getStatus()));
    applicationES.setDecisionTime(TimeUtil.dateToMillis(applicationJson.getDecisionTime()));
    applicationES.setApplicationTypeData(createApplicationTypeDataES(applicationJson));
    applicationES.setLocations(createLocationES(applicationJson.getLocations()));
    Map<CustomerRoleType, CustomerWithContactsES> roleToCwcES = applicationJson.getCustomersWithContacts().stream()
        .collect(Collectors.toMap(cwc -> cwc.getRoleType(), cwc -> customerMapper.createWithContactsES(cwc)));
    applicationES.setCustomers(new RoleTypedCustomerES(roleToCwcES));
    if (applicationJson.getProject() != null) {
      applicationES.setProjectId(applicationJson.getProject().getId());
    }
    return applicationES;
  }


  /**
   * Transfer the information from the given model-domain object to given ui-domain object. Does not handle references to other objects like
   * customer.
   *
   * @param application
   */
  public ApplicationJson mapApplicationToJson(Application application) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setId(application.getId());
    applicationJson.setApplicationId(application.getApplicationId());
    applicationJson.setStatus(application.getStatus());
    applicationJson.setType(application.getType());
    applicationJson.setKindsWithSpecifiers(application.getKindsWithSpecifiers());
    applicationJson.setApplicationTags(createTagsJson(application.getApplicationTags()));
    applicationJson.setMetadataVersion(application.getMetadataVersion());
    applicationJson.setCreationTime(application.getCreationTime());
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
          .map(dEntry -> createDistributionEntryJson(dEntry)).collect(Collectors.toList()));
    }
    applicationJson.setOwner(createUserJson(application.getOwner()));
    applicationJson.setCalculatedPrice(application.getCalculatedPrice());
    applicationJson.setNotBillable(application.getNotBillable());
    applicationJson.setNotBillableReason(application.getNotBillableReason());
    applicationJson.setCustomersWithContacts(customerMapper.createWithContactsJson(application));
    applicationJson.setInvoiceRecipientId(application.getInvoiceRecipientId());
    applicationJson.setReplacedByApplicationId(application.getReplacedByApplicationId());
    applicationJson.setReplacesApplicationId(application.getReplacesApplicationId());
    applicationJson.setCustomerReference(application.getCustomerReference());
    applicationJson.setInvoicingDate(application.getInvoicingDate());
    applicationJson.setInvoiced(application.getInvoiced());
    applicationJson.setSkipPriceCalculation(application.getSkipPriceCalculation());
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
            .map(t -> createTagJson(t))
            .collect(Collectors.toList());
  }

  public ApplicationTagJson createTagJson(ApplicationTag tag) {
    return new ApplicationTagJson(tag.getAddedBy(), tag.getType(), tag.getCreationTime());
  }

  public List<String> createTagES(List<ApplicationTagJson> tagJsons) {
    if (tagJsons == null) {
      return null;
    }

    return tagJsons.stream().map(tag -> tag.getType().toString()).collect(Collectors.toList());
  }

  public LocationSearchCriteria createLocationSearchCriteria(LocationQueryJson query) {
    LocationSearchCriteria lsc = new LocationSearchCriteria();
    lsc.setIntersects(query.getIntersectingGeometry());
    lsc.setAfter(query.getAfter());
    lsc.setBefore(query.getBefore());
    lsc.setStatusTypes(query.getStatusTypes());
    return lsc;
  }

  public ApplicationIdentifierJson mapApplicationIdentifierToJson(ApplicationIdentifier applicationIdentifier) {
    return new ApplicationIdentifierJson(applicationIdentifier.getId(), applicationIdentifier.getApplicationId());
  }

  private List<LocationES> createLocationES(List<LocationJson> locationJsons) {
    if (locationJsons != null) {
      return locationJsons.stream()
          .filter(l -> l.getPostalAddress() != null)
          .map(json -> new LocationES(
              json.getPostalAddress().getStreetAddress(),
              json.getPostalAddress().getPostalCode(),
              json.getPostalAddress().getCity(),
              getCityDistrictId(json),
              json.getAdditionalInfo()))
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  private Integer getCityDistrictId(LocationJson locationJson) {
    return Optional.ofNullable(locationJson.getCityDistrictIdOverride()).orElse(locationJson.getCityDistrictId());
  }

  private DistributionEntryJson createDistributionEntryJson(DistributionEntry distributionEntry) {
    DistributionEntryJson distributionEntryJson = new DistributionEntryJson();
    distributionEntryJson.setDistributionType(distributionEntry.getDistributionType());
    distributionEntryJson.setName(distributionEntry.getName());
    distributionEntryJson.setEmail(distributionEntry.getEmail());
    distributionEntryJson.setPostalAddress(ApplicationCommonMapper.createPostalAddressJson(distributionEntry.getPostalAddress()));
    return distributionEntryJson;
  }

  private UserJson createUserJson(Integer userId) {
    if (userId == null) {
      return null;
    }
    return userService.findUserById(userId);
  }
}
