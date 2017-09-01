package fi.hel.allu.external.mapper;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.external.domain.*;
import fi.hel.allu.external.mapper.extension.ExcavationAnnouncementExtMapper;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.servicecore.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapping between <code>ApplicationJson</code> and <code>ApplicationExt</code> classes.
 */
@Component
public class ApplicationExtMapper {

  private CustomerService customerService;
  private ContactService contactService;
  private ProjectService projectService;

  @Autowired
  public ApplicationExtMapper(
      CustomerService customerService,
      ContactService contactService,
      ProjectService projectService) {
    this.customerService = customerService;
    this.contactService = contactService;
    this.projectService = projectService;
  }

  /**
   * Creates new application from given external application.
   *
   * @param   applicationExt  Application used to create new application.
   * @return  New application.
   */
  public ApplicationJson createApplicationJson(ApplicationExt applicationExt) {
    ApplicationJson applicationJson = new ApplicationJson();

    applicationJson.setProject(mapProjectJson(applicationExt.getProjectId()));
    applicationJson.setCustomersWithContacts(mapCustomerWithContactsJsons(applicationExt.getCustomersWithContacts()));
    applicationJson.setLocations(Optional.ofNullable(applicationExt.getLocations())
        .map(locations -> locations.stream().map(l -> LocationExtMapper.createLocationJson(l)).collect(Collectors.toList())).orElse(null));
    applicationJson.setStatus(StatusType.PENDING);
    applicationJson.setType(applicationExt.getType());
    applicationJson.setApplicationTags(null);
    applicationJson.setName(applicationExt.getName());
    applicationJson.setCreationTime(ZonedDateTime.now());
    applicationJson.setExtension(mapApplicationExtensionJson(applicationExt));
    applicationJson.setKindsWithSpecifiers(applicationExt.getKindsWithSpecifiers());
    applicationJson.setDecisionDistributionType(DistributionType.EMAIL);
    applicationJson.setDecisionPublicityType(PublicityType.PUBLIC);

    return applicationJson;
  }

  public ApplicationExt mapApplicationExt(ApplicationJson applicationJson) {
    ApplicationExt applicationExt = new ApplicationExt();

    applicationExt.setId(applicationJson.getId());
    applicationExt.setProjectId(Optional.ofNullable(applicationJson.getProject()).map(project -> project.getId()).orElse(null));
    applicationExt.setCustomersWithContacts(mapCustomerWithContactsExts(applicationJson.getCustomersWithContacts()));
    applicationExt.setLocations(Optional.ofNullable(applicationJson.getLocations())
        .map(locations -> locations.stream().map(l -> LocationExtMapper.mapLocationExt(l)).collect(Collectors.toList())).orElse(null));
    applicationExt.setStatus(applicationJson.getStatus());
    applicationExt.setType(applicationJson.getType());
    applicationExt.setKindsWithSpecifiers(applicationJson.getKindsWithSpecifiers());
    applicationExt.setApplicationTags(Optional.ofNullable(applicationJson.getApplicationTags()).orElse(Collections.emptyList())
        .stream().map(t -> mapApplicationTagExt(t)).collect(Collectors.toList()));
    applicationExt.setName(applicationJson.getName());
    applicationExt.setCreationTime(applicationJson.getCreationTime());
    applicationExt.setStartTime(applicationJson.getStartTime());
    applicationExt.setEndTime(applicationJson.getEndTime());
    applicationExt.setExtension(mapApplicationExtensionExt(applicationJson));
    applicationExt.setDecisionTime(applicationJson.getDecisionTime());

    return applicationExt;
  }

  /**
   * Merges given external application's data into existing application
   *
   * @param existingApplicationJson Existing application getting merged values.
   * @param applicationExt Application providing values to be merged.
   */
  public void mergeApplicationJson(ApplicationJson existingApplicationJson, ApplicationExt applicationExt) {
    existingApplicationJson.setProject(mapProjectJson(applicationExt.getProjectId()));
    existingApplicationJson
        .setCustomersWithContacts(mapCustomerWithContactsJsons(applicationExt.getCustomersWithContacts()));
    existingApplicationJson.setLocations(Optional.ofNullable(applicationExt.getLocations()).map(
        locations -> locations.stream().map(l -> LocationExtMapper.createLocationJson(l)).collect(Collectors.toList()))
        .orElse(null));
    existingApplicationJson.setType(applicationExt.getType());
    existingApplicationJson.setName(applicationExt.getName());
    existingApplicationJson.setExtension(mapApplicationExtensionJson(applicationExt));
    existingApplicationJson.setKindsWithSpecifiers(applicationExt.getKindsWithSpecifiers());
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object
   * @param application
   * @return created Json application extension
   */
  public ApplicationExtensionJson mapApplicationExtensionJson(ApplicationExt application) {
    switch (application.getType()) {
      case EXCAVATION_ANNOUNCEMENT:
        return ExcavationAnnouncementExtMapper.extToJson((ExcavationAnnouncementExt) application.getExtension());
      default:
        throw new IllegalArgumentException("No model to json mapper for extension type " + application.getType());
    }
  }

  /**
   * Transfer the information from the given model-domain object to given ui-domain object
   * @param application
   * @return created Json application extension
   */
  public ApplicationExtensionExt mapApplicationExtensionExt(ApplicationJson application) {
    switch (application.getType()) {
      case EXCAVATION_ANNOUNCEMENT:
        return ExcavationAnnouncementExtMapper.jsonToExt((ExcavationAnnouncementJson) application.getExtension());
      default:
        throw new IllegalArgumentException("No model to json mapper for extension type " + application.getType());
    }
  }

  private List<CustomerWithContactsJson> mapCustomerWithContactsJsons(List<CustomerWithContactsExt> customerWithContactsExts) {
    Set<Integer> customerIds = customerWithContactsExts.stream().map(CustomerWithContactsExt::getCustomer).collect(Collectors.toSet());
    Set<Integer> contactIds =
        customerWithContactsExts.stream().map(CustomerWithContactsExt::getContacts).flatMap(List::stream).collect(Collectors.toSet());
    Map<Integer, CustomerJson> idToCustomerJson = customerService.getCustomersById(new ArrayList<>(customerIds))
        .stream().collect(Collectors.toMap(customerJson -> customerJson.getId(), customerJson -> customerJson));
    Map<Integer, ContactJson> idToContactJson = contactService.getContactsById(new ArrayList<>(contactIds))
        .stream().collect(Collectors.toMap(contactJson -> contactJson.getId(), contactJson -> contactJson));

    // make sure all customers and contacts are found from database
    if (customerIds.size() != idToCustomerJson.size() || contactIds.size() != idToContactJson.size()) {
      throw new NoSuchEntityException("Given customer or contact not found");
    }

    ArrayList<CustomerWithContactsJson> customerWithContactsJsons = new ArrayList<>();
    for (CustomerWithContactsExt customerWithContactsExt : customerWithContactsExts) {
      CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
      customerWithContactsJson.setRoleType(customerWithContactsExt.getRoleType());
      customerWithContactsJson.setCustomer(idToCustomerJson.get(customerWithContactsExt.getCustomer()));
      customerWithContactsJson.setContacts(
          customerWithContactsExt.getContacts().stream().map(c -> idToContactJson.get(c)).collect(Collectors.toList()));
      customerWithContactsJsons.add(customerWithContactsJson);
    }

    return customerWithContactsJsons;
  }

  private List<CustomerWithContactsExt> mapCustomerWithContactsExts(List<CustomerWithContactsJson> customerWithContactsJsons) {
    List<CustomerWithContactsExt> customerWithContactsExts = new ArrayList<>();

    for (CustomerWithContactsJson customerWithContactsJson : customerWithContactsJsons) {
      CustomerWithContactsExt customerWithContactsExt = new CustomerWithContactsExt();
      customerWithContactsExt.setRoleType(customerWithContactsJson.getRoleType());
      customerWithContactsExt.setCustomer(customerWithContactsJson.getCustomer().getId());
      customerWithContactsExt.setContacts(customerWithContactsJson.getContacts().stream().map(c -> c.getId()).collect(Collectors.toList()));
      customerWithContactsExts.add(customerWithContactsExt);
    }

    return customerWithContactsExts;
  }

  private ApplicationTagExt mapApplicationTagExt(ApplicationTagJson applicationTagJson) {
    ApplicationTagExt applicationTagExt = new ApplicationTagExt();
    applicationTagExt.setType(applicationTagJson.getType());
    applicationTagExt.setCreationTime(applicationTagJson.getCreationTime());
    return applicationTagExt;
  }

  private ProjectJson mapProjectJson(Integer projectId) {
    // TODO: validation that the project is owned by customer should be done
    if (projectId == null) {
      return null;
    } else {
      return projectService.findByIds(Collections.singletonList(projectId)).stream().findFirst()
          .orElseThrow(() -> new NoSuchEntityException("No such project", Integer.toString(projectId)));
    }
  }
}
