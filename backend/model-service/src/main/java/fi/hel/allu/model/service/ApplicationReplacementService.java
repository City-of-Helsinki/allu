package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fi.hel.allu.common.domain.types.ApplicationTagType.*;
import static fi.hel.allu.model.common.ApplicationUtil.isRecurringRental;

/**
 * Service for replacing application (korvaava päätös).
 */
@Service
public class ApplicationReplacementService {
  private static final Set<CommentType> COMMENT_TYPES_NOT_COPIED = new HashSet<>(Arrays.asList(CommentType.PROPOSE_APPROVAL,
      CommentType.PROPOSE_REJECT, CommentType.PROPOSE_TERMINATION));

  private static final Set<ApplicationTagType> TAG_TYPES_NOT_COPIED = new HashSet<>(Arrays.asList(
      // If SAP ID missing also from replacing application,
      // this is generated when its' moved to decision state
      SAP_ID_MISSING,
      // Open supervision tasks not copied to replacing application
      // so don't copy supervision task related tags either.
      PRELIMINARY_SUPERVISION_REQUESTED,
      PRELIMINARY_SUPERVISION_REJECTED,
      PRELIMINARY_SUPERVISION_DONE,
      SUPERVISION_REQUESTED,
      SUPERVISION_REJECTED,
      SUPERVISION_DONE,
      OPERATIONAL_CONDITION_REPORTED
  ));

  private static final Set<StatusType> REPLACEMENT_IS_ALLOWED = new HashSet<>(Arrays.asList(
      StatusType.DECISION,
      StatusType.OPERATIONAL_CONDITION
  ));


  private final ApplicationService applicationService;
  private final ApplicationDao applicationDao;
  private final CommentDao commentDao;
  private final LocationDao locationDao;
  private final DepositDao depositDao;
  private final SupervisionTaskDao supervisionTaskDao;
  private final ChargeBasisDao chargeBasisDao;
  private final InvoiceDao invoiceDao;
  private final InvoicingPeriodService invoicingPeriodService;
  private final DistributionEntryDao distributionEntryDao;
  private final InformationRequestDao informationRequestDao;
  private final PricingService pricingService;
  private final ChargeBasisService chargeBasisService;
  private final AttachmentDao attachmentDao;


  @Autowired
  public ApplicationReplacementService(ApplicationService applicationService, ApplicationDao applicationDao, CommentDao commentDao,
      LocationDao locationDao, DepositDao depositDao, SupervisionTaskDao supervisionTaskDao,
      ChargeBasisDao chargeBasisDao, InvoiceDao invoiceDao, InvoicingPeriodService invoicingPeriodService,
      DistributionEntryDao distributionEntryDao, InformationRequestDao informationRequestDao,
      PricingService pricingService, ChargeBasisService chargeBasisService, AttachmentDao attachmentDao) {
    this.applicationService = applicationService;
    this.applicationDao = applicationDao;
    this.commentDao = commentDao;
    this.locationDao = locationDao;
    this.depositDao = depositDao;
    this.supervisionTaskDao = supervisionTaskDao;
    this.chargeBasisDao = chargeBasisDao;
    this.invoiceDao = invoiceDao;
    this.invoicingPeriodService = invoicingPeriodService;
    this.distributionEntryDao = distributionEntryDao;
    this.informationRequestDao = informationRequestDao;
    this.pricingService = pricingService;
    this.chargeBasisService = chargeBasisService;
    this.attachmentDao = attachmentDao;
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

    copyApplicationRelatedData(applicationId, replacingApplication);

    // Update application status
    updateStatus(replacingApplication);

    // Set replaces and replaced by
    applicationDao.setApplicationReplaced(applicationId, replacingApplication.getId());

    createInvoicingPeriods(replacingApplication);

    updateReplacingChargeBasisEntries(replacingApplication);
    updateReplacingApplicationPrice(replacingApplication.getId());

    return replacingApplication.getId();
  }

  private void copyApplicationRelatedData(int applicationId, Application replacingApplication) {
    commentDao.copyApplicationComments(applicationId, replacingApplication.getId(), COMMENT_TYPES_NOT_COPIED);
    copyApplicationAttachments(applicationId, replacingApplication.getId());
    depositDao.copyApplicationDeposit(applicationId, replacingApplication.getId());
    supervisionTaskDao.copyApprovedSupervisionTasks(applicationId, replacingApplication.getId());
    chargeBasisDao.copyManualChargeBasisEntries(applicationId, replacingApplication.getId(), invoiceDao.getInvoicedChargeBasisIds(applicationId));
    distributionEntryDao.copy(applicationId, replacingApplication.getId());
    informationRequestDao.move(applicationId, replacingApplication.getId());

  }

  public void copyApplicationAttachments(Integer copyFromApplicationId, Integer copyToApplicationId) {
    List<AttachmentInfo> infos = attachmentDao.findByApplication(copyFromApplicationId);
    attachmentDao.copyForApplication(infos, copyToApplicationId);
  }

  private Application addReplacingApplication(Application applicationToReplace, int userId) {
    validateReplacementAllowed(applicationToReplace);
    Application replacingApplication = createReplacingApplication(applicationToReplace);
    replacingApplication = applicationService.insert(replacingApplication, userId);
    return replacingApplication;
  }

  private void validateReplacementAllowed(Application applicationToReplace) {
    final boolean replacementAllowedForStatus = REPLACEMENT_IS_ALLOWED.contains(applicationToReplace.getStatus());
    final boolean isReplaced = applicationToReplace.getReplacedByApplicationId() != null;
    if (!replacementAllowedForStatus || isReplaced) {
      throw new IllegalArgumentException("application.replacement.forbidden");
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
    application.setExternalOwnerId(applicationToReplace.getExternalOwnerId());
    application.setExternalApplicationId(applicationToReplace.getExternalApplicationId());
    application.setSkipPriceCalculation(applicationToReplace.getSkipPriceCalculation());
    application.setInvoicingPeriodLength(applicationToReplace.getInvoicingPeriodLength());
    return application;
  }

  public void setApplicationLocations(Application applicationToReplace, Application application) {
    List<Location> locations = locationDao.findByApplicationId(applicationToReplace.getId());
    // Sort locations by id to ensure locations are the same order as when previously created.
    // Ensures tags are same order.
    if (!locations.isEmpty() && locations.get(0).getId() != null)
      locations.sort(Comparator.comparing(AbstractLocation::getId));
    locations.forEach(this::clearIds);
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
      appIds.sort((a1, a2) -> Integer.compare(a2.getId(), a1.getId()));
      applicationId = appIds.get(0).getApplicationId();
    }
    return ApplicationIdUtil.generateReplacingApplicationId(applicationId);
  }

  private void updateStatus(Application replacingApplication) {
    applicationDao.updateStatus(replacingApplication.getId(), StatusType.HANDLING);

    if (ApplicationType.CABLE_REPORT == replacingApplication.getType()) {
      applicationDao.setTargetState(replacingApplication.getId(), StatusType.DECISION);
    }
  }

  private void createInvoicingPeriods(Application replacingApplication) {
    // Invoicing periods need to be re-created when replacing application for types
    // which have invoicing periods
    if (replacingApplication.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      invoicingPeriodService.setExcavationAnnouncementPeriods(replacingApplication.getId());
    } else if (replacingApplication.getType() == ApplicationType.AREA_RENTAL
            && replacingApplication.getInvoicingPeriodLength() != null) {
      invoicingPeriodService.createInvoicingPeriods(replacingApplication.getId(), replacingApplication.getInvoicingPeriodLength());
    } else if (replacingApplication.getType() == ApplicationType.SHORT_TERM_RENTAL
            && isRecurringRental(replacingApplication)) {
      invoicingPeriodService.createRecurringApplicationPeriods(replacingApplication.getId());
    }
  }

  private void updateReplacingChargeBasisEntries(Application replacingApplication) {
    // Copying invoicable and locked values from replaced application to replacing
    List<Integer> invoicedChargeBasisIds = invoiceDao.getInvoicedChargeBasisIds(replacingApplication.getReplacesApplicationId());
    List<ChargeBasisEntry> oldApplicationEntries = chargeBasisDao.getChargeBasis(replacingApplication.getReplacesApplicationId());
    List<ChargeBasisEntry> presentApplicationEntries = chargeBasisDao.getChargeBasis(replacingApplication.getId());
    List<ChargeBasisEntry> oldNonInvoicedEntries = oldApplicationEntries.stream()
      .filter(e -> !invoicedChargeBasisIds.contains(e.getId()))
      .collect(Collectors.toList());
    List<ChargeBasisEntry> oldInvoicedEntries = oldApplicationEntries.stream()
      .filter(e -> invoicedChargeBasisIds.contains(e.getId()))
      .collect(Collectors.toList());

    // Get locations
    Set<Integer> locationIds = new HashSet<>();
    locationIds.addAll(getLocations(oldApplicationEntries));
    locationIds.addAll(getLocations(presentApplicationEntries));
    Map<Integer, Location> locationMap = new HashMap<>();
    // Get distinct list of ids to fetch to minimize db overhead
    locationDao.findByIds(new ArrayList<>(locationIds)).forEach(e -> locationMap.put(e.getId(), e));

    // Handle copying
    List<Integer> updatedUnderpasses = new ArrayList<>();
    handleInvoicedEntries(oldInvoicedEntries, presentApplicationEntries, locationMap, updatedUnderpasses);
    handleNotYetInvoicedEntries(oldNonInvoicedEntries, presentApplicationEntries, locationMap,
      updatedUnderpasses);
    // Update entries
    updateChargeBasisEntries(presentApplicationEntries);
  }

  private Set<Integer> getLocations(List<ChargeBasisEntry> entries){
    return entries.stream()
      .map(ChargeBasisEntry::getLocationId).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  private void handleInvoicedEntries(List<ChargeBasisEntry> oldEntries,
                                     List<ChargeBasisEntry> presentApplicationEntries,
                                     Map<Integer, Location> locationMap, List<Integer> updatedUnderpasses) {

    for (ChargeBasisEntry oldEntry : oldEntries) {
      presentApplicationEntries.stream()
        .filter(presentEntry -> presentEntry.equalContent(oldEntry, locationMap))
        .forEach(presentEntry -> {
          handleEntry(presentEntry, false);
          handleUnderpass(presentEntry, presentApplicationEntries, updatedUnderpasses);
        });
    }
  }

  private void handleNotYetInvoicedEntries(List<ChargeBasisEntry> oldNonInvoicedEntries,
                                           List<ChargeBasisEntry> presentApplicationEntries,
                                           Map<Integer, Location> locationMap,
                                           List<Integer> updatedUnderpasses) {
    for (ChargeBasisEntry oldEntry : oldNonInvoicedEntries) {
      List<ChargeBasisEntry> presentEntries = presentApplicationEntries.stream()
        .filter(presentEntry -> presentEntry.equalContent(oldEntry, locationMap)
          && !updatedUnderpasses.contains(presentEntry.getId())).collect(Collectors.toList());
      if(!presentEntries.isEmpty()){
          presentEntries.forEach(presentEntry -> {
              handleEntry(presentEntry, oldEntry.isInvoicable());
              handleUnderpass(presentEntry, presentApplicationEntries, updatedUnderpasses);
        });
      }
    }
  }
  private void handleUnderpass(ChargeBasisEntry refereedEntry, List<ChargeBasisEntry> presentApplicationEntries,
                               List<Integer> updatedUnderpasses){
    Optional<ChargeBasisEntry> underpass = presentApplicationEntries.stream()
      .filter(e ->e.getReferredTag() != null && StringUtils.equals(refereedEntry.getTag(), e.getReferredTag())
        && e.isUnderPass())
      .findFirst();
    if (underpass.isPresent() ){
      handleEntry(underpass.get(), refereedEntry.isInvoicable());
      updatedUnderpasses.add(underpass.get().getId());
    }
  }

  private void handleEntry(ChargeBasisEntry entry, boolean isInvoicable){
    entry.setInvoicable(isInvoicable);
    entry.setLocked(false);
  }


  private void updateChargeBasisEntries(List<ChargeBasisEntry> entriesToUpdate) {
    // Disable invoicable for those invoiced and those set as not invoicable in replaced application
    List<Integer> idsToNotInvoicable = entriesToUpdate.stream()
      .filter(e->!e.isInvoicable())
      .map(ChargeBasisEntry::getId)
      .collect(Collectors.toList());
    chargeBasisDao.setEntriesInvoicable(idsToNotInvoicable, false);
    // Lock those already invoiced or otherwise already locked in replaced application
    List<Integer> idsToLocked = entriesToUpdate.stream()
      .filter(e-> e.getLocked() != null && e.getLocked())
      .map(ChargeBasisEntry::getId)
      .collect(Collectors.toList());
    chargeBasisDao.setEntriesLocked(idsToLocked, true);
  }

  private void updateReplacingApplicationPrice(int applicationId) {
    applicationDao.updateCalculatedPrice(
      applicationId,
      pricingService.totalPrice(chargeBasisService.getChargeBasis(applicationId)));
  }
}