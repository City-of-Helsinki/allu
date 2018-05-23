package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CommentDao;
import fi.hel.allu.model.dao.DepositDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.*;

import static fi.hel.allu.common.domain.types.ApplicationTagType.*;

/**
 * Service for replacing application (korvaava päätös).
 */
@Service
public class ApplicationReplacementService {
  private static final Set<CommentType> COMMENT_TYPES_NOT_COPIED = new HashSet<>(Arrays.asList(CommentType.PROPOSE_APPROVAL,
      CommentType.PROPOSE_REJECT));

  private static final Set<ApplicationTagType> TAG_TYPES_NOT_COPIED = new HashSet<>(Arrays.asList(
      // If SAP ID missing also from replacing application,
      // this is generated when its' moved to decision state
      SAP_ID_MISSING,
      // Supervision tasks not copied to replacing application
      // so don't copy supervision task related tags either.
      PRELIMINARY_SUPERVISION_REQUESTED,
      PRELIMINARY_SUPERVISION_REJECTED,
      PRELIMINARY_SUPERVISION_DONE,
      SUPERVISION_REQUESTED,
      SUPERVISION_REJECTED,
      SUPERVISION_DONE,
      OPERATIONAL_CONDITION_REPORTED,
      OPERATIONAL_CONDITION_REJECTED,
      OPERATIONAL_CONDITION_ACCEPTED,
      FINAL_SUPERVISION_REQUESTED,
      FINAL_SUPERVISION_REJECTED,
      FINAL_SUPERVISION_ACCEPTED
  ));


  private final ApplicationService applicationService;
  private final ApplicationDao applicationDao;
  private final CommentDao commentDao;
  private final LocationService locationService;
  private final DepositDao depositDao;

  @Autowired
  public ApplicationReplacementService(ApplicationService applicationService, ApplicationDao applicationDao, CommentDao commentDao,
      LocationService locationService, DepositDao depositDao) {
    this.applicationService = applicationService;
    this.locationService = locationService;
    this.applicationDao = applicationDao;
    this.commentDao = commentDao;
    this.depositDao = depositDao;
  }

  /**
   * Replace application with given ID.
   * <ul>
   * <li>Creates a copy from application with given ID</li>
   * <li>Replaced application must be in {@link StatusType#DECISION}-state</li>
   * <li>Sets original application to {@link StatusType#REPLACED}-state and new application to {@link StatusType#HANDLING}-state</li>
   * <li>Removes original application from project</li>
   * <li>Sets decision fields (decision maker and decision time) of replacing application to null
   * <li>Copies following data from original application to replacing application
   *  <ul>
   *  <li>{@link Location}</li>
   *  <li>{@link Comment}</li>
   *  <li>{@link ApplicationTag}</li>
   *  <li>{@link SupervisionTask}</li>
   *  <li>{@link Deposit}</li>
   *  <li>{@link AttachmentInfo}</li>
   *  </ul>
   * </li>
   * </ul>
   *
   * @param applicationId Id of the application to replace
   * @return ID of the replacing application.
   */
  @Transactional
  public int replaceApplication(int applicationId, int userId) {
    // Copy application
    Application applicationToReplace = applicationService.findById(applicationId);
    Application replacingApplication = addReplacingApplication(applicationToReplace, userId);

    copyApplicationRelatedData(applicationId, replacingApplication, userId);

    // Update application status
    applicationDao.updateStatus(replacingApplication.getId(), StatusType.HANDLING);
    // Remove replaced application from project
    applicationDao.updateProject(null, Collections.singletonList(applicationToReplace.getId()));
    // Set replaces and replaced by
    applicationDao.setApplicationReplaced(applicationId, replacingApplication.getId());
    return replacingApplication.getId();
  }

  private void copyApplicationRelatedData(int applicationId, Application replacingApplication, int userId) {
    commentDao.copyApplicationComments(applicationId, replacingApplication.getId(), COMMENT_TYPES_NOT_COPIED);
    applicationDao.copyApplicationAttachments(applicationId, replacingApplication.getId());
    depositDao.copyApplicationDeposit(applicationId, replacingApplication.getId());
  }

  private Application addReplacingApplication(Application applicationToReplace, int userId) {
    validateReplacementAllowed(applicationToReplace);
    Application replacingApplication = createReplacingApplication(applicationToReplace);
    replacingApplication = applicationService.insert(replacingApplication, userId);
    return replacingApplication;
  }

  private void validateReplacementAllowed(Application applicationToReplace) {
    if (!StatusType.DECISION.equals(applicationToReplace.getStatus())) {
      throw new IllegalArgumentException("Application in invalid state, replacement not allowed");
    }
  }

  private Application createReplacingApplication(Application applicationToReplace) {
    Application application = new Application();
    application.setApplicationId(generateReplacingApplicationId(applicationToReplace));
    application.setCustomersWithContacts(applicationToReplace.getCustomersWithContacts());
    application.setDecisionPublicityType(applicationToReplace.getDecisionPublicityType());
    application.setEndTime(applicationToReplace.getEndTime());
    application.setExtension(applicationToReplace.getExtension());
    application.setOwner(applicationToReplace.getOwner());
    application.setInvoiceRecipientId(applicationToReplace.getInvoiceRecipientId());
    application.setName(applicationToReplace.getName());
    application.setNotBillable(applicationToReplace.getNotBillable());
    application.setNotBillableReason(applicationToReplace.getNotBillableReason());
    application.setProjectId(applicationToReplace.getProjectId());
    application.setRecurringEndTime(applicationToReplace.getRecurringEndTime());
    application.setStartTime(applicationToReplace.getStartTime());
    application.setType(applicationToReplace.getType());
    application.setCustomerReference(applicationToReplace.getCustomerReference());
    application.setInvoicingDate(applicationToReplace.getInvoicingDate());
    application.setIdentificationNumber(applicationToReplace.getIdentificationNumber());
    setApplicationLocations(applicationToReplace, application);
    // Application DAO will automatically create copies of following
    application.setApplicationTags(applicationToReplace.getApplicationTags().stream()
        .filter(t -> !TAG_TYPES_NOT_COPIED.contains(t.getType())).collect(Collectors.toList()));
    application.getApplicationTags().forEach(t -> t.setCreationTime(ZonedDateTime.now()));
    application.setDecisionDistributionList(applicationToReplace.getDecisionDistributionList());
    application.setKindsWithSpecifiers(applicationToReplace.getKindsWithSpecifiers());
    application.setReplacesApplicationId(applicationToReplace.getId());
    return application;
  }

  public void setApplicationLocations(Application applicationToReplace, Application application) {
    List<Location> locations = locationService.findByApplicationId(applicationToReplace.getId());
    locations.forEach(l -> clearIds(l));
    application.setLocations(locations);
  }

  private void clearIds(Location location) {
    location.setId(null);
    location.setApplicationId(null);
  }

  private String generateReplacingApplicationId(Application applicationToReplace) {
    String applicationId = applicationToReplace.getApplicationId();

    final List<ApplicationIdentifier> appIds = applicationDao.findByApplicationIdStartingWith(
            ApplicationIdUtil.getBaseApplicationId(applicationId));
    if (!appIds.isEmpty()) {
      // Find latest application ID
      Collections.sort(appIds, (a1, a2) -> Integer.valueOf(a2.getId()).compareTo(a1.getId()));
      applicationId = appIds.get(0).getApplicationId();
    }
    return ApplicationIdUtil.generateReplacingApplicationId(applicationId);
  }
}
