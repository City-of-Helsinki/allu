package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.*;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.TestCommon;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationReplacementServiceTest {

  private static final ZonedDateTime STARTTIME = ZonedDateTime.now().minusDays(2);
  private static final ZonedDateTime ENDTIME = ZonedDateTime.now().plusDays(10);
  @Autowired
  private ApplicationService applicationService;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private CommentDao commentDao;
  @Autowired
  private LocationService locationService;
  @Autowired
  private SupervisionTaskDao supervisionTaskDao;
  @Autowired
  private DepositDao depositDao;
  @Autowired
  private ChargeBasisDao chargeBasisDao;
  @Autowired
  private AttachmentDao attachmentDao;
  @Autowired
  private UserDao userDao;
  @Autowired
  private InvoicingPeriodService invoicingPeriodService;
  @Autowired
  private DistributionEntryDao distributionEntryDao;
  @Autowired
  private InformationRequestDao informationRequestDao;

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private ApplicationReplacementService applicationReplacementService;

  private Application originalApplication;
  private User testUser;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
    testUser = testCommon.insertUser("testUser");
    insertOriginalApplication();
  }

  @Test
  public void shouldCreateReplacementApplication() {
    Application application = replaceApplication();
    validateReplacingApplicationData(application);
  }

  @Test
  public void shouldAddPrefixToApplicationId() {
    Application application = replaceApplication();
    assertEquals(originalApplication.getApplicationId() + "-2", application.getApplicationId());
  }

  @Test
  public void applicationIdIsIncreased() {
    for (int i = 0; i < 12; i++) {
      Application application = replaceApplication();
      assertEquals(ApplicationIdUtil.getBaseApplicationId(originalApplication.getApplicationId()) + "-" + (i + 2), application.getApplicationId());
      originalApplication = application;
    }
  }

  @Test
  public void originalStatusIsKept() {
    replaceApplication();
    Application updatedOriginalApplication = applicationService.findById(originalApplication.getId());
    assertEquals(StatusType.DECISION, updatedOriginalApplication.getStatus());
  }

  @Test
  public void shouldSetTargetStateForCableReport() {
    CustomerWithContacts customer = new CustomerWithContacts(CustomerRoleType.APPLICANT, testCommon.insertPerson(), Collections.emptyList());
    Application cableReport = new Application();
    cableReport.setCustomersWithContacts(Collections.singletonList(customer));
    cableReport.setEndTime(ENDTIME);
    cableReport.setExtension(new CableReport());
    cableReport.setKind(ApplicationKind.ELECTRICITY);
    cableReport.setName("Application name");
    cableReport.setStartTime(STARTTIME);
    cableReport.setType(ApplicationType.CABLE_REPORT);
    cableReport.setNotBillable(false);
    cableReport = applicationDao.insert(cableReport);
    insertLocations(cableReport);

    setToDecisionState(cableReport.getId());
    int applicationId = applicationReplacementService.replaceApplication(cableReport.getId(), testUser.getId());
    Application updatedOriginalApplication = applicationService.findById(applicationId);
    assertEquals(StatusType.DECISION, updatedOriginalApplication.getTargetState());
  }

  @Test
  public void shouldCopyComments() {
    insertComment();
    Application application = replaceApplication();
    Comment replacing = commentDao.findByApplicationId(application.getId()).get(0);
    Comment original = commentDao.findByApplicationId(originalApplication.getId()).get(0);
    assertEquals(original.getText(), replacing.getText());
    assertEquals(original.getType(), replacing.getType());
    assertEquals(original.getUserId(), replacing.getUserId());
  }

  @Test
  public void shouldCopyApprovedSupervisionTasks() {
    insertSupervisionTasks();
    Application application = replaceApplication();
    List<SupervisionTask> replacing = supervisionTaskDao.findByApplicationId(application.getId());
    assertFalse(replacing.isEmpty());
    replacing.forEach(r -> assertEquals(SupervisionTaskStatusType.APPROVED, r.getStatus()));
  }

  @Test
  public void shouldCopyLocations() {
    Application application = replaceApplication();
    Location replacing = locationService.findByApplicationId(application.getId()).get(0);
    Location original = locationService.findByApplicationId(originalApplication.getId()).get(0);
    assertEquals(original.getAdditionalInfo(), replacing.getAdditionalInfo());
    assertEquals(original.getArea(), replacing.getArea());
    assertEquals(original.getEndTime(), replacing.getEndTime());
    assertEquals(original.getGeometry(), replacing.getGeometry());
    assertEquals(original.getStartTime(), replacing.getStartTime());
  }

  @Test
  public void shouldCopyTags() {
    insertTag();
    Application application = replaceApplication();
    ApplicationTag original = applicationDao.findTagsByApplicationId(originalApplication.getId()).get(0);
    ApplicationTag replacing = applicationDao.findTagsByApplicationId(application.getId()).get(0);
    assertEquals(original.getAddedBy(), replacing.getAddedBy());
    assertEquals(original.getType(), replacing.getType());
  }

  @Test
  public void shouldCopyDeposit() {
    insertDeposit();
    Application application = replaceApplication();
    Deposit original = depositDao.findByApplicationId(originalApplication.getId());
    Deposit replacing = depositDao.findByApplicationId(application.getId());
    assertEquals(original.getAmount(), replacing.getAmount());
    assertEquals(original.getStatus(), replacing.getStatus());
  }

  @Test
  public void shouldCopyAttachments() {
    insertAttachment();
    Application application = replaceApplication();
    AttachmentInfo original = attachmentDao.findByApplication(originalApplication.getId()).get(0);
    AttachmentInfo replacing = attachmentDao.findByApplication(application.getId()).get(0);
    assertEquals(original.getType(), replacing.getType());
    assertEquals(original.getDescription(), replacing.getDescription());
    // Attachment data should not be copied.
    assertEquals(original.getAttachmentDataId(), replacing.getAttachmentDataId());
  }

  @Test
  public void shouldCopyManualChargeBasisEntries() {
    insertChargeBasisEntries();
    Application application = replaceApplication();
    List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(application.getId());
    entries.forEach(e -> assertTrue(e.getManuallySet()));
  }


  @Test(expected = IllegalArgumentException.class)
  public void shouldNotReplaceInInvalidState() {
    applicationReplacementService.replaceApplication(originalApplication.getId(), testUser.getId());
  }

  @Test
  public void shouldCreateInvoicingPeriodsForExcavationAnnouncement() {
    Application excavation = insertApplication(ApplicationType.EXCAVATION_ANNOUNCEMENT, new ExcavationAnnouncement());
    setToDecisionState(excavation.getId());
    int applicationId = applicationReplacementService.replaceApplication(excavation.getId(), testUser.getId());
    assertTrue(invoicingPeriodService.findForApplicationId(applicationId).size() > 0);
  }

  @Test
  public void shouldCreateInvoicingPeriodsForAreaRental() {
    Application areaRental = createApplication(ApplicationType.AREA_RENTAL, new AreaRental());
    areaRental.setInvoicingPeriodLength(3);
    Application created = applicationDao.insert(areaRental);
    List<Location> locations = createLocations(created);
    locations.get(0).setEndTime(STARTTIME.plusMonths(8));
    locationService.insert(locations, testUser.getId());

    setToDecisionState(created.getId());
    int applicationId = applicationReplacementService.replaceApplication(created.getId(), testUser.getId());
    // 8 month application should contain 3 periods when period length is 3
    assertEquals(invoicingPeriodService.findForApplicationId(applicationId).size(), 3);
  }

  @Test
  public void shouldNotCreateInvoicingPeriodsForAreaRentalWithoutPeriodLength() {
    Application areaRental = insertApplication(ApplicationType.AREA_RENTAL, new AreaRental());
    setToDecisionState(areaRental.getId());
    int applicationId = applicationReplacementService.replaceApplication(areaRental.getId(), testUser.getId());
    // No periods should have been copied since original did not have any
    assertEquals(invoicingPeriodService.findForApplicationId(applicationId).size(), 0);
  }

  private <E extends ApplicationExtension> Application insertApplication(ApplicationType type, E extension) {
    Application created = applicationDao.insert(createApplication(type, extension));
    insertLocations(created);
    return created;
  }

  private <E extends ApplicationExtension> Application createApplication(ApplicationType type, E extension) {
    CustomerWithContacts customer = new CustomerWithContacts(CustomerRoleType.APPLICANT, testCommon.insertPerson(), Collections.emptyList());
    Application application = new Application();
    application.setCustomersWithContacts(Collections.singletonList(customer));
    application.setEndTime(ENDTIME);
    application.setExtension(extension);
    application.setKind(ApplicationKind.ELECTRICITY);
    application.setName("Application name");
    application.setStartTime(STARTTIME);
    application.setType(type);
    application.setNotBillable(false);
    return application;
  }

  private void insertAttachment() {
    AttachmentInfo attachment = new AttachmentInfo();
    attachment.setDescription("Attachment");
    attachment.setType(AttachmentType.ADDED_BY_CUSTOMER);
    attachment.setUserId(testCommon.insertUser("attachment").getId());
    byte data[] = {1, 2, 3};
    attachmentDao.insert(originalApplication.getId(), attachment, data);
  }

  private void insertChargeBasisEntries() {
    ChargeBasisEntry manual = new ChargeBasisEntry();
    manual.setManuallySet(true);
    manual.setType(ChargeBasisType.ADDITIONAL_FEE);
    manual.setUnit(ChargeBasisUnit.PIECE);
    manual.setUnitPrice(1);
    manual.setNetPrice(1);
    manual.setQuantity(1);
    manual.setText("manual");
    ChargeBasisEntry calculated = new ChargeBasisEntry();
    calculated.setManuallySet(false);
    calculated.setType(ChargeBasisType.AREA_USAGE_FEE);
    calculated.setUnit(ChargeBasisUnit.DAY);
    calculated.setUnitPrice(1);
    calculated.setNetPrice(1);
    calculated.setQuantity(1);
    calculated.setText("calculated");
    chargeBasisDao.setChargeBasis(new ChargeBasisModification(originalApplication.getId(), Collections.singletonList(manual), Collections.emptySet(), Collections.emptyMap(), true));
    chargeBasisDao.setChargeBasis(new ChargeBasisModification(originalApplication.getId(), Collections.singletonList(calculated), Collections.emptySet(), Collections.emptyMap(), false));
  }


  private void insertSupervisionTasks() {
    SupervisionTask supervisionTask = new SupervisionTask();
    supervisionTask.setOwnerId(testCommon.insertUser("supervision").getId());
    supervisionTask.setDescription("Description");
    supervisionTask.setApplicationId(originalApplication.getId());
    supervisionTask.setType(SupervisionTaskType.PRELIMINARY_SUPERVISION);
    supervisionTask.setPlannedFinishingTime(ZonedDateTime.now().plusDays(2));
    SupervisionTask t = supervisionTaskDao.insert(supervisionTask);
    t.setStatus(SupervisionTaskStatusType.APPROVED);
    supervisionTaskDao.update(t);
    supervisionTask.setId(null);
    supervisionTask.setType(SupervisionTaskType.FINAL_SUPERVISION);
    supervisionTask.setPlannedFinishingTime(ZonedDateTime.now().plusDays(2));
    supervisionTaskDao.insert(supervisionTask);
  }

  private void insertDeposit() {
    Deposit deposit = new Deposit();
    deposit.setAmount(123);
    deposit.setStatus(DepositStatusType.UNPAID_DEPOSIT);
    deposit.setApplicationId(originalApplication.getId());
    depositDao.insert(deposit);
  }

  private void insertTag() {
    ApplicationTag tag = new ApplicationTag(testCommon.insertUser("tag").getId(), ApplicationTagType.DECISION_NOT_SENT, ZonedDateTime.now());
    applicationDao.addTag(originalApplication.getId(), tag);
  }

  private void insertComment() {
    Comment original = new Comment();
    original.setText("Comment text");
    original.setType(CommentType.INTERNAL);
    original.setUserId(testCommon.insertUser("comment").getId());
    commentDao.insertForApplication(original, originalApplication.getId());
  }

  private Application replaceApplication() {
    setToDecisionState(originalApplication.getId());
    int applicationId = applicationReplacementService.replaceApplication(originalApplication.getId(), testUser.getId());
    Application application = applicationService.findById(applicationId);
    return application;
  }

  private void setToDecisionState(Integer applicationId) {
    Optional<User> userOpt = userDao.findByUserName("decisionmaker");
    User decisionMaker = userOpt.isPresent() ? userOpt.get() : testCommon.insertUser("decisionmaker");
    applicationDao.updateDecision(applicationId, StatusType.DECISION, decisionMaker.getId(), originalApplication.getHandler());
  }

  private void validateReplacingApplicationData(Application application) {
    // Decision fields should be set to null
    assertNull(application.getDecisionMaker());
    assertNull(application.getDecisionTime());
    // Status should be handling
    assertEquals(StatusType.HANDLING, application.getStatus());
    // Application ID should be updated
    assertNotEquals(originalApplication.getApplicationId(), application.getApplicationId());
    // Should have new ID
    assertNotEquals(originalApplication.getId(), application.getId());
    // Owner should be same as handler
    assertEquals(originalApplication.getHandler(), application.getOwner());

    validateReplacingDecisionDistributionList(application.getDecisionDistributionList());
    validateReplacingExtension(application.getExtension());

    // Following data should be equal with original application
    assertEquals(originalApplication.getCustomersWithContacts().get(0).getCustomer().getId(), application.getCustomersWithContacts().get(0).getCustomer().getId());
    assertEquals(originalApplication.getDecisionPublicityType(), application.getDecisionPublicityType());
    assertEquals(originalApplication.getEndTime(), application.getEndTime());
    assertEquals(originalApplication.getInvoiceRecipientId(), application.getInvoiceRecipientId());
    assertEquals(originalApplication.getKind(), application.getKind());
    assertEquals(originalApplication.getKindsWithSpecifiers(), application.getKindsWithSpecifiers());
    assertEquals(originalApplication.getName(), application.getName());
    assertEquals(originalApplication.getNotBillable(), application.getNotBillable());
    assertEquals(originalApplication.getNotBillableReason(), application.getNotBillableReason());
    assertEquals(originalApplication.getProjectId(), application.getProjectId());
    assertEquals(originalApplication.getRecurringEndTime(), application.getRecurringEndTime());
    assertEquals(originalApplication.getStartTime(), application.getStartTime());
    assertEquals(originalApplication.getType(), application.getType());
  }

  private void validateReplacingExtension(ApplicationExtension extension) {
    Event original = (Event)originalApplication.getExtension();
    Event replacing = (Event)extension;
    assertEquals(original.getApplicationType(), replacing.getApplicationType());
    assertEquals(original.getAttendees(), replacing.getAttendees());
    assertEquals(original.getEventStartTime(), replacing.getEventStartTime());
    assertEquals(original.getEventEndTime(), replacing.getEventEndTime());
    assertEquals(original.getDescription(), replacing.getDescription());
    assertEquals(original.isFoodSales(), replacing.isFoodSales());
  }

  private void validateReplacingDecisionDistributionList(List<DistributionEntry> decisionDistributionList) {
    assertEquals(originalApplication.getDecisionDistributionList().size(), decisionDistributionList.size());
    DistributionEntry original = originalApplication.getDecisionDistributionList().get(0);
    DistributionEntry replacing = decisionDistributionList.get(0);
    assertEquals(original.getEmail(), replacing.getEmail());
    assertEquals(original.getDistributionType(), replacing.getDistributionType());
    assertEquals(original.getPostalAddress(), replacing.getPostalAddress());
    assertNotEquals(original.getApplicationId(), replacing.getApplicationId());
    assertNotEquals(original.getId(), replacing.getId());
  }

  private void insertOriginalApplication() {
    CustomerWithContacts customer = new CustomerWithContacts(CustomerRoleType.APPLICANT, testCommon.insertPerson(), Collections.emptyList());
    originalApplication = new Application();
    originalApplication.setCustomersWithContacts(Collections.singletonList(customer));
    originalApplication.setDecisionPublicityType(PublicityType.CONFIDENTIAL_PARTIALLY);
    originalApplication.setEndTime(ENDTIME);
    originalApplication.setExtension(createExtension());
    originalApplication.setHandler(testCommon.insertUser("Handler").getId());
    originalApplication.setOwner(testCommon.insertUser("Owner").getId());
    originalApplication.setInvoiceRecipientId(testCommon.insertPerson().getId());
    originalApplication.setKind(ApplicationKind.AGILE_KIOSK_AREA);
    originalApplication.setKindsWithSpecifiers(createKindsWithSpecifiers());
    originalApplication.setName("Application name");
    originalApplication.setNotBillable(false);
    originalApplication.setNotBillableReason("Not billable reason");
    originalApplication.setProjectId(testCommon.insertProject("orig"));
    originalApplication.setRecurringEndTime(ZonedDateTime.now().plusDays(22));
    originalApplication.setStartTime(STARTTIME);
    originalApplication.setType(ApplicationType.EVENT);
    originalApplication = applicationDao.insert(originalApplication);
    insertLocations(originalApplication);
    insertDistribution(originalApplication);
  }

  private void insertDistribution(Application originalApplication) {
    List<DistributionEntry> distributionEntries = Collections.singletonList(createDistributionEntry(originalApplication.getId()));
    List<DistributionEntry> inserted = this.distributionEntryDao.insert(distributionEntries);
    this.originalApplication.setDecisionDistributionList(inserted);

  }

  private void insertLocations(Application originalApplication) {
    List<Location> locations = createLocations(originalApplication);
    locationService.insert(locations, testUser.getId());
  }

  private List<Location> createLocations(Application originalApplication) {
    Location location = new Location();
    location.setAdditionalInfo("Location info");
    location.setApplicationId(originalApplication.getId());
    location.setArea(Double.valueOf("1224.3"));
    location.setEndTime(ENDTIME);
    location.setGeometry(geometrycollection(3879, polygon(ring(c(5, 5), c(5, 7), c(7, 7), c(7, 5), c(5, 5)))));
    location.setStartTime(STARTTIME);
    location.setPaymentTariff("1");
    return Collections.singletonList(location);
  }

  private Map<ApplicationKind, List<ApplicationSpecifier>> createKindsWithSpecifiers() {
    Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
    kindsWithSpecifiers.put(ApplicationKind.AGILE_KIOSK_AREA, Collections.singletonList(ApplicationSpecifier.ASPHALT));
    return kindsWithSpecifiers;
  }

  private Event createExtension() {
    Event extension = new Event();
    extension.setAttendees(29);
    extension.setEventStartTime(STARTTIME);
    extension.setDescription("extension description");
    extension.setFoodSales(true);
    return extension;
  }

  private DistributionEntry createDistributionEntry(int applicationId) {
    DistributionEntry distributionEntry = new DistributionEntry();
    distributionEntry.setApplicationId(applicationId);
    distributionEntry.setEmail("foo@bar.fi");
    distributionEntry.setDistributionType(DistributionType.EMAIL);
    return distributionEntry;
  }
}
