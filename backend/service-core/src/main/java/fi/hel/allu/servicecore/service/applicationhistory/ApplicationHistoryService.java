package fi.hel.allu.servicecore.service.applicationhistory;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.util.ObjectComparer;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.model.domain.changehistory.CustomerChange;
import fi.hel.allu.model.domain.changehistory.HistorySearchCriteria;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.domain.history.ApplicationForHistory;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;
import fi.hel.allu.servicecore.service.UserService;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class responsible for application history management.
 */
@Service
public class ApplicationHistoryService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final UserService userService;
  private final ObjectComparer comparer;
  private final Pattern skipFieldPattern;
  private final ChangeHistoryMapper changeHistoryMapper;
  private final ApplicationMapper applicationMapper;

  // regex to skip id fields since they are needed for comparison but no need to show them to user.
  // TODO: add cable report validityTime as skipped field too
  private static final String SKIP_FIELDS_RE = "(/.*/[^/]+/id)"; // Skip all id-fields

  /* List of mappings for abbreviated history keys */
  private static final Map<Pattern, String> ABBREV_MAP = new HashMap<>();

  static {
    ABBREV_MAP.put(Pattern.compile("/locations/([^/]*)/geometry/.*"), "/locations/$1/geometry");
  }

  @Autowired
  public ApplicationHistoryService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      UserService userService, ChangeHistoryMapper changeHistoryMapper, ApplicationMapper applicationMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.changeHistoryMapper = changeHistoryMapper;
    this.applicationMapper = applicationMapper;
    this.skipFieldPattern = Pattern.compile(SKIP_FIELDS_RE);

    this.comparer = new ObjectComparer();
    comparer.addMixin(ContactJson.class, ContactSimpleMixIn.class);
    comparer.addMixin(CustomerJson.class, CustomerSimpleMixin.class);
    comparer.addMixin(ApplicationJson.class, ApplicationMixIn.class);
    comparer.addMixin(CustomerWithContactsJson.class, CustomerWithContactsSimpleMixin.class);
  }

  /**
   * Get change items for an application
   *
   * @param applicationId
   *          application ID
   * @return list of changes ordered from oldest to newest
   */
  public List<ChangeHistoryItemJson> getChanges(Integer applicationId) {
    return Arrays.stream(restTemplate.getForObject(applicationProperties.getApplicationHistoryUrl(),
        ChangeHistoryItem[].class, applicationId)).map(c -> changeHistoryMapper.mapToJson(c))
        .collect(Collectors.toList());
  }

  public List<ChangeHistoryItemJson> getStatusChanges(Integer applicationId) {
    return Arrays.stream(restTemplate.getForObject(applicationProperties.getApplicationHistoryUrl(),
        ChangeHistoryItem[].class, applicationId))
          .filter(c -> c.getChangeType() == ChangeType.STATUS_CHANGED)
          .map(c -> changeHistoryMapper.mapToJson(c))
          .collect(Collectors.toList());
  }

  public boolean hasStatusInHistory(Integer applicationId, StatusType status) {
    return getStatusChanges(applicationId).stream()
        .anyMatch(change -> status.name().equals(change.getChangeSpecifier()));
  }

  public void addInvoiceRecipientChange(Integer applicationId, CustomerJson oldInvoiceRecipient, CustomerJson newInvoiceRecipient) {
    final Integer userId = userService.getCurrentUser().getId();
    final Integer oldCustomerId = oldInvoiceRecipient != null ? oldInvoiceRecipient.getId() : null;
    final String oldCustomerName = oldInvoiceRecipient != null ? oldInvoiceRecipient.getName() : null;
    final Integer newCustomerId = newInvoiceRecipient != null ? newInvoiceRecipient.getId() : null;
    final String newCustomerName = newInvoiceRecipient != null ? newInvoiceRecipient.getName() : null;

    addChangeItem(applicationId, userId, new CustomerChange(oldCustomerId, oldCustomerName),
        new CustomerChange(newCustomerId, newCustomerName), ChangeType.CUSTOMER_CHANGED, "INVOICE_RECIPIENT");
  }

  /**
   * Compare two applications and add a change item that describes their
   * differences
   *
   * @param applicationId
   *          application ID for which to add the change item
   * @param oldApplication
   *          old contents
   * @param newApplication
   *          new contents
   */
  public void addFieldChanges(Integer applicationId, ApplicationJson oldApplication, ApplicationJson newApplication) {
    final Integer userId = userService.getCurrentUser().getId();
    final ApplicationForHistory oldHistoryApplication = applicationMapper.mapJsonToHistory(oldApplication);
    final ApplicationForHistory newHistoryApplication = applicationMapper.mapJsonToHistory(newApplication);
    final Map<CustomerRoleType, CustomerWithContactsJson> oldCustomers = oldHistoryApplication.getCustomersWithContacts();
    final Map<CustomerRoleType, CustomerWithContactsJson> newCustomers = newHistoryApplication.getCustomersWithContacts();
    oldHistoryApplication.setCustomersWithContacts(null);
    newHistoryApplication.setCustomersWithContacts(null);
    oldHistoryApplication.setInvoiceRecipientId(null); // Invoice recipient change is saved separately
    newHistoryApplication.setInvoiceRecipientId(null);

    // Area rental locations are saved separately as area rental can have several areas and thus area
    // key must be saved also, so that it is possible to see which area was changed
    if (getApplicationType(oldApplication, newApplication) == ApplicationType.AREA_RENTAL) {
      addLocationChanges(applicationId, oldApplication.getLocations(), newApplication.getLocations());
      oldHistoryApplication.setLocations(null);
      newHistoryApplication.setLocations(null);
    }
    addContentsChange(applicationId, oldHistoryApplication, newHistoryApplication);
    for (CustomerRoleType role : CustomerRoleType.values()) {
      addCustomerChange(applicationId, userId, oldCustomers.get(role), newCustomers.get(role), role);
    }
  }

  public void addLocationChanges(Integer applicationId, LocationJson oldLocation, LocationJson newLocation) {
    final String prefix = "/locations/" + getLocationValue(oldLocation, newLocation, LocationJson::getId);
    final List<FieldChange> fieldChanges = findFieldChanges(oldLocation, newLocation, prefix);

    if (!fieldChanges.isEmpty()) {
      ChangeHistoryItem change = new ChangeHistoryItem();
      change.setChangeType(ChangeType.LOCATION_CHANGED);
      change.setFieldChanges(fieldChanges);
      change.setChangeSpecifier(Integer.toString(getLocationValue(oldLocation, newLocation, LocationJson::getLocationKey)));
      addChangeItem(applicationId, change);
    }
  }

  private void addLocationChanges(Integer applicationId, List<LocationJson> oldLocations, List<LocationJson> newLocations) {
    final Set<Integer> locationIds = oldLocations.stream().map(l -> l.getId()).collect(Collectors.toSet());
    locationIds.addAll(newLocations.stream().map(l -> l.getId()).collect(Collectors.toSet()));
    locationIds.stream()
        .map(id -> Pair.of(
            oldLocations.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null),
            newLocations.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null)))
        .collect(Collectors.toList())
        .forEach(l -> addLocationChanges(applicationId, l.getLeft(), l.getRight()));
  }

  private <T> T getLocationValue(LocationJson l1, LocationJson l2, Function<LocationJson, T> valueGetter) {
    if (l1 != null && valueGetter.apply(l1) != null) {
      return valueGetter.apply(l1);
    } else if (l2 != null) {
      return valueGetter.apply(l2);
    }
    return null;
  }

  private ApplicationType getApplicationType(ApplicationJson app1, ApplicationJson app2) {
    if (app1 != null) {
      return app1.getType();
    } else {
      return app2.getType();
    }
  }

  public void addCustomerChange(Integer applicationId, CustomerWithContactsJson oldCustomer, CustomerWithContactsJson newCustomer, CustomerRoleType role) {
    final Integer userId = userService.getCurrentUser().getId();
    addCustomerChange(applicationId, userId, oldCustomer, newCustomer, role);
  }

  private void addCustomerChange(Integer applicationId, Integer userId, CustomerWithContactsJson oldCustomer, CustomerWithContactsJson newCustomer, CustomerRoleType role) {
    final Integer oldCustomerId = oldCustomer != null ? oldCustomer.getCustomer().getId() : null;
    final String oldCustomerName = oldCustomer != null ? oldCustomer.getCustomer().getName() : null;
    final List<ContactJson> oldContacts = oldCustomer != null ? oldCustomer.getContacts() : null;
    final Integer newCustomerId = newCustomer != null ? newCustomer.getCustomer().getId() : null;
    final String newCustomerName = newCustomer != null ? newCustomer.getCustomer().getName() : null;
    final List<ContactJson> newContacts = newCustomer != null ? newCustomer.getContacts() : null;

    if (!Objects.equals(oldCustomerId, newCustomerId)) {
      addChangeItem(applicationId, userId, new CustomerChange(oldCustomerId, oldCustomerName),
          new CustomerChange(newCustomerId, newCustomerName), ChangeType.CUSTOMER_CHANGED, role.name());
    }
    addChangeItem(applicationId, userId, oldContacts, newContacts, ChangeType.CONTACT_CHANGED, role.name());
  }

  private void addContentsChange(Integer applicationId, ApplicationForHistory oldApplication, ApplicationForHistory newApplication) {
    final List<FieldChange> fieldChanges = findFieldChanges(oldApplication, newApplication, "");
    if (!fieldChanges.isEmpty()) {
      ChangeHistoryItem change = new ChangeHistoryItem();
      change.setChangeType(ChangeType.CONTENTS_CHANGED);
      change.setFieldChanges(fieldChanges);
      addChangeItem(applicationId, change);
    }
  }

  private List<FieldChange> findFieldChanges(Object obj1, Object obj2, String prefix) {
    final Set<String> abbreviated = new HashSet<>();
    final List<FieldChange> fieldChanges = comparer.compare(obj1, obj2).stream()
        .filter(diff -> !skipFieldPattern.matcher(prefix + diff.keyName).matches())
        .filter(diff -> !shouldAbbreviate(prefix + diff.keyName, abbreviated))
        .map(diff -> new FieldChange(prefix + diff.keyName, diff.oldValue, diff.newValue))
        .collect(Collectors.toList());

    abbreviated.forEach(fieldName -> fieldChanges.add(new FieldChange(fieldName, "..", "..")));
    return fieldChanges;
  }

  /*
   * Test if given key name should be abbreviated. If yes, store the matching
   * abbreviation and return true, else just return false.
   */
  private boolean shouldAbbreviate(String keyName, Set<String> abbreviated) {
    for (Entry<Pattern, String> entry : ABBREV_MAP.entrySet()) {
      Matcher m = entry.getKey().matcher(keyName);
      if (m.matches()) {
        abbreviated.add(m.replaceAll(entry.getValue()));
        return true;
      }
    }
    return false;
  }

  /**
   * Add a change item that describes application status change.
   *
   * @param applicationId
   *          application ID
   * @param newStatus
   *          new status
   */
  public void addStatusChange(Integer applicationId, StatusType newStatus, StatusType targetStatus) {
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(ChangeType.STATUS_CHANGED);
    change.setChangeSpecifier(newStatus.name());
    Optional.ofNullable(targetStatus).ifPresent(t -> change.setChangeSpecifier2(t.name()));
    addChangeItem(applicationId, change);
  }

  public void addContractStatusChange(Integer applicationId, ContractStatusType contractStatus) {
    addChange(applicationId, ChangeType.CONTRACT_STATUS_CHANGED, contractStatus.name());
  }

  public void addCommentAdded(Integer applicationId, CommentType type) {
    addChange(applicationId, ChangeType.COMMENT_ADDED, type.name());
  }

  public void addCommentRemoved(Integer applicationId) {
    addChange(applicationId, ChangeType.COMMENT_REMOVED, null);
  }

  public void addAttachmentAdded(Integer applicationId, String attachmentName) {
    addChange(applicationId, ChangeType.ATTACHMENT_ADDED, attachmentName);
  }

  public void addAttachmentRemoved(Integer applicationId, String attachmentName) {
    addChange(applicationId, ChangeType.ATTACHMENT_REMOVED, attachmentName);
  }

  public void addSupervisionAdded(Integer applicationId, SupervisionTaskType taskType) {
    addChange(applicationId, ChangeType.SUPERVISION_ADDED, taskType.name());
  }

  public void addSupervisionUpdated(Integer applicationId, SupervisionTaskType taskType) {
    addChange(applicationId, ChangeType.SUPERVISION_UPDATED, taskType.name());
  }

  public void addSupervisionRemoved(Integer applicationId, SupervisionTaskType taskType) {
    addChange(applicationId, ChangeType.SUPERVISION_REMOVED, taskType.name());
  }

  public void addSupervisionApproved(Integer applicationId, SupervisionTaskType taskType) {
    addChange(applicationId, ChangeType.SUPERVISION_APPROVED, taskType.name());
  }

  public void addSupervisionRejected(Integer applicationId, SupervisionTaskType taskType) {
    addChange(applicationId, ChangeType.SUPERVISION_REJECTED, taskType.name());
  }

  private void addChange(Integer applicationId, ChangeType type, String changeSpecifier) {
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(type);
    change.setChangeSpecifier(changeSpecifier);
    addChangeItem(applicationId, change);
  }

  public void addOwnerChange(Integer applicationId, Integer newOwnerId) {
    String username = Optional.ofNullable(newOwnerId)
        .map(id -> userService.findUserById(newOwnerId).getUserName())
        .orElse(null);
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(ChangeType.OWNER_CHANGED);
    change.setFieldChanges(Collections.singletonList(new FieldChange("owner", null, username)));
    addChangeItem(applicationId, change);
  }

  /**
   * Add a change item that describes application creation.
   *
   * @param applicationId
   *          The application's ID.
   */
  public void addApplicationCreated(Integer applicationId) {
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(ChangeType.CREATED);
    addChangeItem(applicationId, change);
  }

  /**
   * Add change item that describes application replacement
   * @param applicationId
   *          The applications ID
   *
   */
  public void addApplicationReplaced(Integer applicationId) {
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(ChangeType.REPLACED);
    addChangeItem(applicationId, change);
  }

  private void addChangeItem(Integer applicationId, int userId, Object oldData, Object newData, ChangeType changeType, String newStatus) {
    final List<FieldChange> fieldChanges = comparer.compare(oldData, newData).stream()
        .map(d -> new FieldChange(d.keyName, d.oldValue, d.newValue)).collect(Collectors.toList());
    if (!fieldChanges.isEmpty()) {
      final ChangeHistoryItem change = new ChangeHistoryItem(userId, null, changeType, newStatus, ZonedDateTime.now(), fieldChanges);
      restTemplate.postForObject(applicationProperties.getAddApplicationHistoryUrl(), change, Void.class, applicationId);
    }
  }

  /*
   * Make the REST call to add given application change for given application
   * ID.
   */
  private void addChangeItem(Integer applicationId, ChangeHistoryItem change) {
    change.setChangeTime(ZonedDateTime.now());
    change.setUserId(userService.getCurrentUser().getId());
    restTemplate.postForObject(applicationProperties.getAddApplicationHistoryUrl(), change, Void.class, applicationId);
  }

  public Map<Integer, List<ChangeHistoryItem>> getExternalOwnerApplicationHistory(Integer externalOwnerId, HistorySearchCriteria searchCriteria) {
    ParameterizedTypeReference<Map<Integer, List<ChangeHistoryItem>>> typeRef = new ParameterizedTypeReference<Map<Integer, List<ChangeHistoryItem>>>() {};
    return restTemplate.exchange(applicationProperties.getExternalOwnerApplicationHistoryUrl(), HttpMethod.POST,
        new HttpEntity<>(searchCriteria), typeRef, externalOwnerId).getBody();
  }

}
