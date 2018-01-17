package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.TestCommon;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.*;

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
  private AttachmentDao attachmentDao;

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private ApplicationReplacementService applicationReplacementService;

  private Application originalApplication;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
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
  public void shouldCopySupervisionTasks() {
    insertSupervisionTask();
    Application application = replaceApplication();
    SupervisionTask original = supervisionTaskDao.findByApplicationId(originalApplication.getId()).get(0);
    SupervisionTask replacing = supervisionTaskDao.findByApplicationId(application.getId()).get(0);
    assertEquals(original.getHandlerId(), replacing.getHandlerId());
    assertEquals(original.getStatus(), replacing.getStatus());
    assertEquals(original.getDescription(), replacing.getDescription());
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

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotReplaceInInvalidState() {
    applicationReplacementService.replaceApplication(originalApplication.getId());
  }

  private void insertAttachment() {
    AttachmentInfo attachment = new AttachmentInfo();
    attachment.setDescription("Attachment");
    attachment.setType(AttachmentType.ADDED_BY_CUSTOMER);
    attachment.setUserId(testCommon.insertUser("attachment").getId());
    byte data[] = {1, 2, 3};
    attachmentDao.insert(originalApplication.getId(), attachment, data);
  }

  private void insertSupervisionTask() {
    SupervisionTask supervisionTask = new SupervisionTask();
    supervisionTask.setHandlerId(testCommon.insertUser("supervision").getId());
    supervisionTask.setDescription("Description");
    supervisionTask.setStatus(SupervisionTaskStatusType.OPEN);
    supervisionTask.setApplicationId(originalApplication.getId());
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
    commentDao.insert(original, originalApplication.getId());
  }

  private Application replaceApplication() {
    setToDecisionState(originalApplication.getId());
    int applicationId = applicationReplacementService.replaceApplication(originalApplication.getId());
    Application application = applicationService.findById(applicationId);
    return application;
  }

  private void setToDecisionState(Integer applicationId) {
    applicationDao.updateDecision(applicationId, StatusType.DECISION, testCommon.insertUser("decisionmaker").getId());
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

    validateReplacingDecisionDistributionList(application.getDecisionDistributionList());
    validateReplacingExtension(application.getExtension());

    // Following data should be equal with original application
    assertEquals(originalApplication.getCustomersWithContacts().get(0).getCustomer().getId(), application.getCustomersWithContacts().get(0).getCustomer().getId());
    assertEquals(originalApplication.getDecisionPublicityType(), application.getDecisionPublicityType());
    assertEquals(originalApplication.getEndTime(), application.getEndTime());
    assertEquals(originalApplication.getOwner(), application.getOwner());
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
    assertEquals(original.getBuildSeconds(), replacing.getBuildSeconds());
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
    originalApplication.setDecisionDistributionList(Collections.singletonList(createDistributionEntry()));
    originalApplication.setDecisionPublicityType(PublicityType.CONFIDENTIAL_PARTIALLY);
    originalApplication.setEndTime(ENDTIME);
    originalApplication.setExtension(createExtension());
    originalApplication.setOwner(testCommon.insertUser("Owner").getId());
    originalApplication.setInvoiceRecipientId(testCommon.insertPerson().getId());
    originalApplication.setKind(ApplicationKind.AGILE_KIOSK_AREA);
    originalApplication.setKindsWithSpecifiers(createKindsWithSpecifiers());
    originalApplication.setName("Application name");
    originalApplication.setNotBillable(false);
    originalApplication.setNotBillableReason("Not billable reason");
    originalApplication.setProjectId(testCommon.insertProject());
    originalApplication.setRecurringEndTime(ZonedDateTime.now().plusDays(22));
    originalApplication.setStartTime(STARTTIME);
    originalApplication.setType(ApplicationType.EVENT);
    originalApplication = applicationDao.insert(originalApplication);
    insertLocations(originalApplication);
  }

  private void insertLocations(Application originalApplication) {
    Location location = new Location();
    location.setAdditionalInfo("Location info");
    location.setApplicationId(originalApplication.getId());
    location.setArea(Double.valueOf("1224.3"));
    location.setEndTime(ENDTIME);
    location.setGeometry(geometrycollection(3879, polygon(ring(c(5, 5), c(5, 7), c(7, 7), c(7, 5), c(5, 5)))));
    location.setStartTime(STARTTIME);
    locationService.insert(Collections.singletonList(location));
  }

  private Map<ApplicationKind, List<ApplicationSpecifier>> createKindsWithSpecifiers() {
    Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
    kindsWithSpecifiers.put(ApplicationKind.AGILE_KIOSK_AREA, Collections.singletonList(ApplicationSpecifier.ASPHALT));
    return kindsWithSpecifiers;
  }

  private Event createExtension() {
    Event extension = new Event();
    extension.setAttendees(29);
    extension.setBuildSeconds(20);
    extension.setDescription("extension description");
    extension.setFoodSales(true);
    return extension;
  }

  private DistributionEntry createDistributionEntry() {
    DistributionEntry distributionEntry = new DistributionEntry();
    distributionEntry.setEmail("foo@bar.fi");
    distributionEntry.setDistributionType(DistributionType.EMAIL);
    return distributionEntry;
  }
}
