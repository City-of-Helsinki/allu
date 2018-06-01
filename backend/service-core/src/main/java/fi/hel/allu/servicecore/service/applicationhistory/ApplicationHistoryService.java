package fi.hel.allu.servicecore.service.applicationhistory;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.util.ObjectComparer;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;

import fi.hel.allu.servicecore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service class responsible for application history management.
 */
@Service
public class ApplicationHistoryService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;
  private ObjectComparer comparer;
  private Pattern skipFieldPattern;
  private ChangeHistoryMapper changeHistoryMapper;

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
      UserService userService, ChangeHistoryMapper changeHistoryMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.changeHistoryMapper = changeHistoryMapper;
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
    Set<String> abbreviated = new HashSet<>();

    List<FieldChange> fieldChanges = comparer.compare(oldApplication, newApplication).stream()
        .filter(diff -> !skipFieldPattern.matcher(diff.keyName).matches())
        .filter(diff -> !shouldAbbreviate(diff.keyName, abbreviated))
        .map(diff -> new FieldChange(diff.keyName, diff.oldValue, diff.newValue))
        .collect(Collectors.toList());

    abbreviated.forEach(fieldName -> fieldChanges.add(new FieldChange(fieldName, "..", "..")));

    if (!fieldChanges.isEmpty()) {
      ChangeHistoryItem change = new ChangeHistoryItem();
      change.setChangeType(ChangeType.CONTENTS_CHANGED);
      change.setFieldChanges(fieldChanges);
      addChangeItem(applicationId, change);
    }
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
  public void addStatusChange(Integer applicationId, StatusType newStatus) {
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(ChangeType.STATUS_CHANGED);
    change.setNewStatus(newStatus);
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

  /*
   * Make the REST call to add given application change for given application
   * ID.
   */
  private void addChangeItem(Integer applicationId, ChangeHistoryItem change) {
    change.setChangeTime(ZonedDateTime.now());
    change.setUserId(userService.getCurrentUser().getId());
    restTemplate.postForObject(applicationProperties.getAddApplicationHistoryUrl(), change, Void.class, applicationId);
  }

  public Map<Integer, List<ChangeHistoryItem>> getExternalOwnerApplicationHistory(Integer externalOwnerId, ZonedDateTime eventsAfter,
      List<Integer> includedApplicationIds) {
    Map<String, Integer> uriParams = new HashMap<>();
    uriParams.put("externalownerid", externalOwnerId);
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getExternalOwnerApplicationHistoryUrl())
        .queryParam("eventsafter", eventsAfter)
        .buildAndExpand(uriParams).toUri();
    ParameterizedTypeReference<Map<Integer, List<ChangeHistoryItem>>> typeRef = new ParameterizedTypeReference<Map<Integer, List<ChangeHistoryItem>>>() {};
    return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(includedApplicationIds), typeRef).getBody();
  }

}
