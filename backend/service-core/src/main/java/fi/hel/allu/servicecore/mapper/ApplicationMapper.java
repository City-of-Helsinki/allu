package fi.hel.allu.servicecore.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wnameless.json.flattener.JsonFlattener;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.RoleType;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApplicationMapper {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationMapper.class);

  private UserService userService;

  @Autowired
  public ApplicationMapper(UserService userService) {
    this.userService = userService;
  }

  private Set<RoleType> canSeeSsn = new HashSet<>(Arrays.asList(
          RoleType.ROLE_CREATE_APPLICATION,
          RoleType.ROLE_PROCESS_APPLICATION,
          RoleType.ROLE_INVOICING));

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
    applicationDomain.setCustomersWithContacts(createCustomerWithContactsModel(applicationJson.getCustomersWithContacts()));
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
    applicationDomain.setDecisionDistributionType(applicationJson.getDecisionDistributionType());
    applicationDomain.setDecisionPublicityType(applicationJson.getDecisionPublicityType());
    if (applicationJson.getDecisionDistributionList() != null) {
      applicationDomain.setDecisionDistributionList(applicationJson.getDecisionDistributionList().stream()
          .map(dEntry -> createDistributionEntryModel(dEntry)).collect(Collectors.toList()));
    }
    applicationDomain.setCalculatedPrice(applicationJson.getCalculatedPrice());
    applicationDomain.setPriceOverride(applicationJson.getPriceOverride());
    applicationDomain.setPriceOverrideReason(applicationJson.getPriceOverrideReason());
    applicationDomain.setNotBillable(applicationJson.getNotBillable());
    applicationDomain.setNotBillableReason(applicationJson.getNotBillableReason());
    applicationDomain.setInvoiceRecipientId(applicationJson.getInvoiceRecipientId());
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
    applicationES.setHandler(
        applicationJson.getHandler() != null ?
            new UserES(applicationJson.getHandler().getUserName(), applicationJson.getHandler().getRealName()) : null);
    applicationES.setType(new ApplicationTypeES(applicationJson.getType()));
    applicationES.setApplicationTags(createTagES(applicationJson.getApplicationTags()));
    applicationES.setStatus(new StatusTypeES(applicationJson.getStatus()));
    applicationES.setDecisionTime(TimeUtil.dateToMillis(applicationJson.getDecisionTime()));
    applicationES.setApplicationTypeData(createApplicationTypeDataES(applicationJson));
    applicationES.setLocations(createLocationES(applicationJson.getLocations()));
    Map<CustomerRoleType, CustomerWithContactsES> roleToCwcES =
        applicationJson.getCustomersWithContacts().stream().collect(Collectors.toMap(cwc -> cwc.getRoleType(), cwc -> createCustomerWithContactsES(cwc)));
    applicationES.setCustomers(new RoleTypedCustomerES(roleToCwcES));
    if (applicationJson.getProject() != null) {
      applicationES.setProjectId(applicationJson.getProject().getId());
    }
    return applicationES;
  }

  private CustomerWithContactsES createCustomerWithContactsES(CustomerWithContactsJson customerWithContactsJson) {
    CustomerWithContactsES customerWithContactsES = new CustomerWithContactsES();
    customerWithContactsES.setCustomer(createCustomerES(customerWithContactsJson.getCustomer()));
    customerWithContactsES.setContacts(createContactES(customerWithContactsJson.getContacts()));
    return customerWithContactsES;
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
    applicationJson.setApplicationTags(createTagJson(application.getApplicationTags()));
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
    applicationJson.setDecisionDistributionType(application.getDecisionDistributionType());
    applicationJson.setDecisionPublicityType(application.getDecisionPublicityType());
    if (application.getDecisionDistributionList() != null) {
      applicationJson.setDecisionDistributionList(application.getDecisionDistributionList().stream()
          .map(dEntry -> createDistributionEntryJson(dEntry)).collect(Collectors.toList()));
    }
    applicationJson.setCalculatedPrice(application.getCalculatedPrice());
    applicationJson.setPriceOverride(application.getPriceOverride());
    applicationJson.setPriceOverrideReason(application.getPriceOverrideReason());
    applicationJson.setNotBillable(application.getNotBillable());
    applicationJson.setNotBillableReason(application.getNotBillableReason());
    applicationJson.setCustomersWithContacts(createCustomerWithContactsJson(application));
    applicationJson.setInvoiceRecipientId(application.getInvoiceRecipientId());

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
        return EventMapper.jsonToModel((EventJson) applicationJson.getExtension());
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

  public CustomerJson createCustomerJson(Customer customer) {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setId(customer.getId());
    customerJson.setType(customer.getType());
    customerJson.setName(customer.getName());
    customerJson.setRegistryKey(getVisibleRegistryKey(customer.getType(), customer.getRegistryKey()));
    customerJson.setOvt(customer.getOvt());
    customerJson.setPhone(customer.getPhone());
    customerJson.setEmail(customer.getEmail());
    customerJson.setPostalAddress(ApplicationCommonMapper.createPostalAddressJson(customer.getPostalAddress()));
    customerJson.setActive(customer.isActive());
    return customerJson;
  }

  public Customer createCustomerModel(CustomerJson customerJson) {
    Customer customerModel = new Customer();
    customerModel.setId(customerJson.getId());
    customerModel.setType(customerJson.getType());
    customerModel.setName(customerJson.getName());
    customerModel.setRegistryKey(customerJson.getRegistryKey());
    customerModel.setOvt(customerJson.getOvt());
    customerModel.setPhone(customerJson.getPhone());
    customerModel.setEmail(customerJson.getEmail());
    customerModel.setPostalAddress(ApplicationCommonMapper.createPostalAddressModel(customerJson.getPostalAddress()));
    customerModel.setActive(customerJson.isActive());
    return customerModel;
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
    json.setCustomerId(c.getCustomerId());
    json.setName(c.getName());
    if (c.getPostalAddress() != null) {
      // TODO: refactor when contact starts using PostalAddressJson
      json.setStreetAddress(c.getPostalAddress().getStreetAddress());
      json.setPostalCode(c.getPostalAddress().getPostalCode());
      json.setCity(c.getPostalAddress().getCity());
    }
    json.setEmail(c.getEmail());
    json.setPhone(c.getPhone());
    json.setActive(c.isActive());
    return json;
  }

  public Contact createContactModel(ContactJson json) {
    Contact contact = new Contact();
    contact.setId(json.getId());
    contact.setCustomerId(json.getCustomerId());
    contact.setName(json.getName());
    if (json.getStreetAddress() != null || json.getPostalCode() != null || json.getCity() != null) {
      // TODO: refactor when contact starts using PostalAddressJson
      contact.setPostalAddress(new PostalAddress(json.getStreetAddress(), json.getPostalCode(), json.getCity()));
    }
    contact.setEmail(json.getEmail());
    contact.setPhone(json.getPhone());
    contact.setIsActive(json.isActive());
    return contact;
  }

  public CustomerES createCustomerES(CustomerJson customerJson) {
    if (customerJson != null) {
      return new CustomerES(
          customerJson.getId(), customerJson.getName(), customerJson.getRegistryKey(), customerJson.getType(), customerJson.isActive());
    } else {
      return null;
    }
  }

  public List<ContactES> createContactES(List<ContactJson> contacts) {
    if (contacts != null) {
      return contacts.stream()
          .map(c -> new ContactES(c.getId(), c.getName(), c.isActive()))
          .collect(Collectors.toList());
    } else {
      return null;
    }
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

  public List<ApplicationTagJson> createTagJson(List<ApplicationTag> tags) {
    if (tags == null) {
      return null;
    }
    return tags.stream()
            .map(t -> new ApplicationTagJson(t.getAddedBy(), t.getType(), t.getCreationTime()))
            .collect(Collectors.toList());
  }

  public List<String> createTagES(List<ApplicationTagJson> tagJsons) {
    if (tagJsons == null) {
      return null;
    }

    return tagJsons.stream().map(tag -> tag.getType().toString()).collect(Collectors.toList());
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

  private List<CustomerWithContactsJson> createCustomerWithContactsJson(Application application) {
    List<CustomerWithContacts> customersWithContacts = application.getCustomersWithContacts();
    List<CustomerWithContactsJson> customerWithContactsJsons = new ArrayList<>();

    customersWithContacts.forEach(cwc -> {
      CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
      customerWithContactsJson.setContacts(cwc.getContacts().stream()
              .map(c -> createContactJson(c))
              .collect(Collectors.toList()));
      customerWithContactsJson.setCustomer(createCustomerJson(cwc.getCustomer()));
      customerWithContactsJson.setRoleType(cwc.getRoleType());
      customerWithContactsJsons.add(customerWithContactsJson);
    });
    return customerWithContactsJsons;
  }

  private List<CustomerWithContacts> createCustomerWithContactsModel(List<CustomerWithContactsJson> customersWithContactsJson) {
    List<CustomerWithContacts> customerWithContacts = new ArrayList<>();
    customersWithContactsJson.forEach(cwcJson -> {
      customerWithContacts.add(new CustomerWithContacts(
          cwcJson.getRoleType(),
          createCustomerModel(cwcJson.getCustomer()),
          cwcJson.getContacts().stream().map(cJson -> createContactModel(cJson)).collect(Collectors.toList())));
    });
    return customerWithContacts;
  }

  static final String SSN_REPLACEMENT = "***********";

  private String getVisibleRegistryKey(CustomerType type, String registryKey) {
    if (!userCanSeeSsn() && CustomerType.PERSON.equals(type)) {
      return SSN_REPLACEMENT;
    } else {
      return registryKey;
    }
  }

  private boolean userCanSeeSsn() {
    UserJson user = userService.getCurrentUser();
    return user.getAssignedRoles().stream().anyMatch(canSeeSsn::contains);
  }
}
