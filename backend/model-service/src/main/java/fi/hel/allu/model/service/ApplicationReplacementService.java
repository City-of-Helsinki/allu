package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CommentDao;
import fi.hel.allu.model.dao.DepositDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.*;

/**
 * Service for replacing application (korvaava päätös).
 */
@Service
public class ApplicationReplacementService {
  private static final Set<CommentType> COMMENT_TYPES_NOT_COPIED = new HashSet<>(Arrays.asList(CommentType.PROPOSE_APPROVAL,
      CommentType.PROPOSE_REJECT));

  private ApplicationService applicationService;
  private ApplicationDao applicationDao;
  private CommentDao commentDao;
  private LocationService locationService;
  private SupervisionTaskDao supervisionTaskDao;
  private DepositDao depositDao;

  @Autowired
  public ApplicationReplacementService(ApplicationService applicationService, ApplicationDao applicationDao, CommentDao commentDao,
      LocationService locationService, SupervisionTaskDao supervisionTaskDao, DepositDao depositDao) {
    this.applicationService = applicationService;
    this.locationService = locationService;
    this.applicationDao = applicationDao;
    this.commentDao = commentDao;
    this.supervisionTaskDao = supervisionTaskDao;
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
  public int replaceApplication(int applicationId) {
    // Copy application
    Application applicationToReplace = applicationService.findById(applicationId);
    Application replacingApplication = addReplacingApplication(applicationToReplace);

    copyApplicationRelatedData(applicationId, replacingApplication);

    // Update application statuses
    applicationDao.updateStatus(replacingApplication.getId(), StatusType.HANDLING);
    applicationDao.updateStatus(applicationId, StatusType.REPLACED);
    // Remove replaced application from project
    applicationDao.updateProject(null, Collections.singletonList(applicationToReplace.getId()));
    // Set replaces and replaced by
    applicationDao.setApplicationReplaced(applicationId, replacingApplication.getId());
    return replacingApplication.getId();
  }

  private void copyApplicationRelatedData(int applicationId, Application replacingApplication) {
    commentDao.copyApplicationComments(applicationId, replacingApplication.getId(), COMMENT_TYPES_NOT_COPIED);
    applicationDao.copyApplicationAttachments(applicationId, replacingApplication.getId());
    supervisionTaskDao.copySupervisionTasks(applicationId, replacingApplication.getId());
    depositDao.copyApplicationDeposit(applicationId, replacingApplication.getId());
    locationService.copyApplicationLocations(applicationId, replacingApplication.getId());
  }

  private Application addReplacingApplication(Application applicationToReplace) {
    validateReplacementAllowed(applicationToReplace);
    Application replacingApplication = createReplacingApplication(applicationToReplace);
    replacingApplication = applicationService.insert(replacingApplication);
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
    application.setDecisionDistributionType(applicationToReplace.getDecisionDistributionType());
    application.setDecisionPublicityType(applicationToReplace.getDecisionPublicityType());
    application.setEndTime(applicationToReplace.getEndTime());
    application.setExtension(applicationToReplace.getExtension());
    application.setHandler(applicationToReplace.getHandler());
    application.setInvoiceRecipientId(applicationToReplace.getInvoiceRecipientId());
    application.setName(applicationToReplace.getName());
    application.setNotBillable(applicationToReplace.getNotBillable());
    application.setNotBillableReason(applicationToReplace.getNotBillableReason());
    application.setProjectId(applicationToReplace.getProjectId());
    application.setRecurringEndTime(applicationToReplace.getRecurringEndTime());
    application.setStartTime(applicationToReplace.getStartTime());
    application.setType(applicationToReplace.getType());

    // Application DAO will automatically create copies of following
    application.setApplicationTags(applicationToReplace.getApplicationTags());
    application.getApplicationTags().forEach(t -> t.setCreationTime(ZonedDateTime.now()));
    application.setDecisionDistributionList(applicationToReplace.getDecisionDistributionList());
    application.setKindsWithSpecifiers(applicationToReplace.getKindsWithSpecifiers());
    application.setReplacesApplicationId(applicationToReplace.getId());
    return application;
  }

  private static String generateReplacingApplicationId(Application applicationToReplace) {
    String applicationId = applicationToReplace.getApplicationId();
    boolean firstReplace = applicationToReplace.getReplacesApplicationId() == null;
    return ApplicationIdUtil.generateReplacingApplicationId(applicationId, firstReplace);
  }
}
