package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.ApplicationChange;
import fi.hel.allu.model.domain.ApplicationFieldChange;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationChangeJson;
import fi.hel.allu.ui.domain.ApplicationFieldChangeJson;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.util.ObjectComparer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

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
  private Pattern skipFieldPattern;

  // regex to control which change fields should be skipped.
  private static final String SKIP_FIELDS_RE = "(/applicationTags/[^/]+/id)|(/extension/infoEntries/[^/]+/id)";

  /* List of mappings for abbreviated history keys */
  private static final Map<Pattern, String> ABBREV_MAP = new HashMap<>();

  static {
    ABBREV_MAP.put(Pattern.compile("/locations/([^/]*)/geometry/.*"), "/locations/$1/geometry");
  }

  @Autowired
  public ApplicationHistoryService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  @PostConstruct
  public void setupPattern() {
    skipFieldPattern = Pattern.compile(SKIP_FIELDS_RE);
  }

  /**
   * Get change items for an application
   *
   * @param applicationId
   *          application ID
   * @return list of changes ordered from oldest to newest
   */
  public List<ApplicationChangeJson> getChanges(Integer applicationId) {
    return Arrays.stream(restTemplate.getForObject(applicationProperties.getApplicationHistoryUrl(),
        ApplicationChange[].class, applicationId)).map(c -> mapToJson(c)).collect(Collectors.toList());
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
    ObjectComparer comparer = new ObjectComparer();

    Set<String> abbreviated = new HashSet<>();

    List<ApplicationFieldChange> fieldChanges = comparer.compare(oldApplication, newApplication).stream()
        .filter(diff -> !skipFieldPattern.matcher(diff.keyName).matches())
        .filter(diff -> !shouldAbbreviate(diff.keyName, abbreviated))
        .map(diff -> new ApplicationFieldChange(diff.keyName, diff.oldValue, diff.newValue))
        .collect(Collectors.toList());

    abbreviated.forEach(fieldName -> fieldChanges.add(new ApplicationFieldChange(fieldName, "..", "..")));

    if (!fieldChanges.isEmpty()) {
      ApplicationChange change = new ApplicationChange();
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
    ApplicationChange change = new ApplicationChange();
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
    ApplicationChange change = new ApplicationChange();
    change.setChangeType(ChangeType.CREATED);
    addChangeItem(applicationId, change);
  }

  /*
   * Make the REST call to add given application change for given application
   * ID.
   */
  private void addChangeItem(Integer applicationId, ApplicationChange change) {
    change.setChangeTime(ZonedDateTime.now());
    change.setUserId(userService.getCurrentUser().getId());
    restTemplate.postForObject(applicationProperties.getAddApplicationHistoryUrl(), change, Void.class, applicationId);
  }

  /*
   * Map a change item from model space to UI space
   */
  private ApplicationChangeJson mapToJson(ApplicationChange c) {
    return new ApplicationChangeJson(c.getUserId(), c.getChangeType(), c.getNewStatus(), c.getChangeTime(),
        mapToJson(c.getFieldChanges()));
  }

  /*
   * Map a list of field changes from model space to UI space
   */
  private List<ApplicationFieldChangeJson> mapToJson(List<ApplicationFieldChange> fieldChanges) {
    return fieldChanges.stream().map(fc -> mapToJson(fc)).collect(Collectors.toList());
  }

  /*
   * Map a single field change from model space to UI space
   */
  private ApplicationFieldChangeJson mapToJson(ApplicationFieldChange fc) {
    return new ApplicationFieldChangeJson(fc.getFieldName(), fc.getOldValue(), fc.getNewValue());
  }
}
