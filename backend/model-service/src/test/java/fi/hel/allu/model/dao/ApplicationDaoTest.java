package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.TestCommon;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationDaoTest {

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private ContactDao contactDao;
  @Autowired
  private DistributionEntryDao distributionEntryDao;

  DistributionEntry testDistributionEntry;

  @Before
  public void init() throws Exception {
    testDistributionEntry = new DistributionEntry();
    testDistributionEntry.setDistributionType(DistributionType.PAPER);
    testDistributionEntry.setEmail("foobar@foo.fi");
  }

  @Test
  public void testFindApplicationsWithContacts() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    Contact contact = new Contact();
    final String testContactName = "kontakti ihminen";
    final String testEmail = "test@emai.fi";
    final String testPhone = "124214";
    Customer customer = application.getCustomersWithContacts().get(0).getCustomer();
    contact.setName(testContactName);
    contact.setEmail(testEmail);
    contact.setPhone(testPhone);
    contact.setCustomerId(customer.getId());
    // retrieve contacts with no postal address first
    Contact insertedContact = contactDao.insert(Collections.singletonList(contact)).get(0);
    CustomerWithContacts customerWithContacts = new CustomerWithContacts(CustomerRoleType.CONTRACTOR, customer, Collections.singletonList(insertedContact));
    application.setCustomersWithContacts(Collections.singletonList(customerWithContacts));
    Application insertedApplication = applicationDao.insert(application);
    List<ApplicationWithContacts> applicationsWithContacts =
        applicationDao.findRelatedApplicationsWithContacts(Collections.singletonList(insertedContact.getId()));
    assertEquals(1, applicationsWithContacts.size());
    assertEquals((int) insertedApplication.getId(), applicationsWithContacts.get(0).getApplicationId());
    List<Contact> contacts = applicationsWithContacts.get(0).getContacts();
    assertEquals(1, contacts.size());
    assertEquals(testContactName, contacts.get(0).getName());
    assertEquals(testEmail, contacts.get(0).getEmail());
    assertEquals(testPhone, contacts.get(0).getPhone());

    // make sure postal address retrieval also works
    PostalAddress testPostalAddress = new PostalAddress("katu 1", "12345", "testikaupunki");
    insertedContact.setPostalAddress(testPostalAddress);
    insertedContact = contactDao.update(Collections.singletonList(insertedContact)).get(0);

    applicationsWithContacts = applicationDao.findRelatedApplicationsWithContacts(Collections.singletonList(insertedContact.getId()));
    assertEquals(1, applicationsWithContacts.size());
    contacts = applicationsWithContacts.get(0).getContacts();
    assertEquals(testPostalAddress.getStreetAddress(), contacts.get(0).getPostalAddress().getStreetAddress());
    assertEquals(testPostalAddress.getPostalCode(), contacts.get(0).getPostalAddress().getPostalCode());
    assertEquals(testPostalAddress.getCity(), contacts.get(0).getPostalAddress().getCity());
  }

  @Test
  public void testFindApplicationsByCustomer() {
    Application application1 = testCommon.dummyOutdoorApplication("Test Application 1", "Test Owner 1");
    Application application2 = testCommon.dummyOutdoorApplication("Test Application 2", "Test Owner 2");
    application2.setCustomersWithContacts(application1.getCustomersWithContacts());
    Application insertedApplication1 = applicationDao.insert(application1);
    Application insertedApplication2 = applicationDao.insert(application2);
    Map<Integer, List<CustomerRoleType>> applicationsWithCrt = applicationDao.findByCustomer(
        insertedApplication1.getCustomersWithContacts().get(0).getCustomer().getId());
    assertEquals(2, applicationsWithCrt.size());
    assertTrue(Arrays.asList(insertedApplication1.getId(), insertedApplication2.getId()).containsAll(applicationsWithCrt.keySet()));
    assertEquals(
        2,
        applicationsWithCrt.values().stream().flatMap(crtList -> crtList.stream()).filter(crt -> CustomerRoleType.APPLICANT.equals(crt)).count());
  }

  @Test
  public void testCreateApplicationIdString() {
    ApplicationSequenceDao applicationSequenceDaoMock = Mockito.mock(ApplicationSequenceDao.class);
    Mockito.when(applicationSequenceDaoMock.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP)).thenReturn(1600001L);
    StructureMetaDao structureMetaDaoMock = Mockito.mock(StructureMetaDao.class);
    Mockito.when(structureMetaDaoMock.getLatestMetadataVersion()).thenReturn(1);
    ApplicationDao applicationDao = new ApplicationDao(
        null,
        applicationSequenceDaoMock,
        distributionEntryDao,
        structureMetaDaoMock,
        Mockito.mock(CustomerDao.class),
        Mockito.mock(AttachmentDao.class),
        Mockito.mock(LocationDao.class));
    Assert.assertEquals("TP1600001", applicationDao.createApplicationId(ApplicationType.EVENT));
  }

  @Test
  public void testInsertApplication() {

    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
    kindsWithSpecifiers.put(ApplicationKind.OUTDOOREVENT, Collections.EMPTY_LIST);
    kindsWithSpecifiers.put(ApplicationKind.MILITARY_EXCERCISE, Collections.singletonList(ApplicationSpecifier.OTHER));
    kindsWithSpecifiers.put(ApplicationKind.ART,
        Arrays.asList(ApplicationSpecifier.ASPHALT, ApplicationSpecifier.BRIDGE));
    application.setKindsWithSpecifiers(kindsWithSpecifiers);
    application.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    Application applOut = applicationDao.insert(application);

    assertEquals(application.getName(), applOut.getName());
    assertNotEquals(application.getCreationTime(), applOut.getCreationTime());
    assertEquals(application.getKindsWithSpecifiers().size(), applOut.getKindsWithSpecifiers().size());
  }

  @Test
  public void testUpdateApplication() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    Application applOut = applicationDao.insert(application);
    applOut.setName("Updated application");
    applOut.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    Application updated = applicationDao.update(applOut.getId(), applOut);

    assertEquals("Updated application", updated.getName());
    assertNotEquals(applOut.getCreationTime(), updated.getCreationTime());
  }

  @Test
  public void testUpdateStatus() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    Application applOut = applicationDao.insert(application);
    Application updated = applicationDao.updateStatus(applOut.getId(), StatusType.CANCELLED);
    assertEquals(StatusType.CANCELLED, updated.getStatus());
  }

  @Test
  public void testDecisionStatus() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    Application applOut = applicationDao.insert(application);
    Application updated = applicationDao.updateDecision(applOut.getId(), StatusType.REJECTED, application.getOwner(), application.getOwner());
    assertEquals(StatusType.REJECTED, updated.getStatus());
    assertEquals(application.getOwner(), updated.getDecisionMaker());
    assertNotNull(updated.getDecisionTime());
  }

  @Test
  public void testInsertApplicationTags() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    newApplication.setApplicationTags(Collections.singletonList(createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application application = applicationDao.insert(newApplication);
    assertNotNull(application.getApplicationTags());
    assertEquals(1, application.getApplicationTags().size());
    assertEquals(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, application.getApplicationTags().get(0).getType());
  }

  @Test
  public void testAddApplicationTag() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    newApplication.setApplicationTags(
        Collections.singletonList(createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application application = applicationDao.insert(newApplication);
    // Add same tag twice, only one should be added
    applicationDao.addTag(application.getId(), createApplicationTag(ApplicationTagType.SAP_ID_MISSING));
    applicationDao.addTag(application.getId(), createApplicationTag(ApplicationTagType.SAP_ID_MISSING));
    Application afterAdd = applicationDao.findByIds(Collections.singletonList(application.getId())).get(0);
    assertEquals(2, afterAdd.getApplicationTags().size());
    assertEquals(1, afterAdd.getApplicationTags().stream()
        .filter(t -> ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED.equals(t.getType())).count());
    assertEquals(1, afterAdd.getApplicationTags().stream()
        .filter(t -> ApplicationTagType.SAP_ID_MISSING.equals(t.getType())).count());
  }

  @Test
  public void testRetriveApplicationTags() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Owner");
    Application secondApplication = testCommon.dummyOutdoorApplication("Test Application 2", "Test Owner 2");
    List<ApplicationTag> applicationTags =  new ArrayList<>();
    applicationTags.add(createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED));
    applicationTags.add(createApplicationTag(ApplicationTagType.SAP_ID_MISSING));
    newApplication.setApplicationTags(applicationTags);
    secondApplication.setApplicationTags(Collections.singletonList(createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application firstApplication = applicationDao.insert(newApplication);
    Application secondReturnedApplication = applicationDao.insert(secondApplication);
    List<Integer> applicationIds = new ArrayList<>();
    applicationIds.add(firstApplication.getId());
    applicationIds.add(secondReturnedApplication.getId());
    List<Application> resultApplication = applicationDao.findByIds(applicationIds);
    assertEquals(2, resultApplication.size());
    assertEquals(1, resultApplication.stream().filter(e -> e.getApplicationTags().size() == 2).count());
    assertEquals(1, resultApplication.stream().filter(e -> e.getApplicationTags().size() == 1).count());
  }

  @Test
  public void testRetrivalOfMultipleApplicationsCustomersList() {
    Application application1 = testCommon.dummyOutdoorApplication("Test Application 1", "Test Owner 1");
    Application application2 = testCommon.dummyOutdoorApplication("Test Application 2", "Test Owner 2");
    application1.setCustomersWithContacts(new ArrayList<>());
    application1.getCustomersWithContacts().add(testCommon.dummyCustomerWithContacts(CustomerRoleType.APPLICANT));
    application1.getCustomersWithContacts().add(testCommon.dummyCustomerWithContacts(CustomerRoleType.CONTRACTOR));
    Application returnedApplication = applicationDao.insert(application1);
    Application returnerApplication2 = applicationDao.insert(application2);
    List<Integer> applicationIds = new ArrayList<>();
    applicationIds.add(returnedApplication.getId());
    applicationIds.add(returnerApplication2.getId());
    List<Application> applications = applicationDao.findByIds(applicationIds);
    assertEquals(2, applications.size());
    assertEquals(1, applications.stream().filter(e -> e.getCustomersWithContacts().size() == 2 ).count());
    assertEquals(1, applications.stream().filter(e -> e.getCustomersWithContacts().size() == 1 ).count());
  }

  @Test
  public void testRetrivalOfMultipleApplicationsList() {
    Customer customer = testCommon.insertPerson();
    Application application1 = testCommon.dummyOutdoorApplication("Test Application 1", "Test Owner 1", customer);
    Application application2 = testCommon.dummyOutdoorApplication("Test Application 2", "Test Owner 2", customer);
    Application returnedApplication = applicationDao.insert(application1);
    Application returnerApplication2 = applicationDao.insert(application2);
    List<Integer> applicationIds = new ArrayList<>();
    applicationIds.add(returnedApplication.getId());
    applicationIds.add(returnerApplication2.getId());
    List<Application> applications = applicationDao.findByIds(applicationIds);
    assertEquals(2, applications.size());
  }

  @Test
  public void testfindByEndTimeNoSpecifiers() {
    // Insert three applications that end in different times in future, remember
    // their ids:
    List<Integer> applicationIds = new ArrayList<>();
    Application application = testCommon.dummyOutdoorApplication("Test application", "Test Owner");
    ZonedDateTime time0 = ZonedDateTime.parse("2015-12-03T10:15:30+02:00");
    application.setEndTime(time0.plusDays(100));
    applicationIds.add(applicationDao.insert(application).getId());
    application.setEndTime(time0.plusDays(30));
    application.setApplicationId(null);
    applicationIds.add(applicationDao.insert(application).getId());
    application.setEndTime(time0.plusDays(10));
    application.setApplicationId(null);
    applicationIds.add(applicationDao.insert(application).getId());

    // Find all that end within two weeks -- should return only the last
    List<Integer> matches = applicationDao.findByEndTime(time0, time0.plusDays(14), Collections.emptyList(),
        Collections.emptyList());
    assertEquals(1, matches.size());
    assertEquals(applicationIds.get(2), matches.get(0));
  }

  @Test
  public void testFindByEndTimeWithSpecifiers() {
    // Insert three applications that end in different times in future, remember
    // their ids, set different statuses for all:
    List<Integer> applicationIds = new ArrayList<>();
    Application application = testCommon.dummyOutdoorApplication("Test outdoor application", "Test Owner EINS");
    ZonedDateTime time0 = ZonedDateTime.parse("2015-12-03T10:15:30+02:00");
    application.setEndTime(time0.plusDays(100));
    applicationIds.add(applicationDao.insert(application).getId());
    application = testCommon.dummyAreaRentalApplication("Test area rental", "Test Owner ZWEI");
    application.setEndTime(time0.plusDays(30));
    applicationIds.add(applicationDao.insert(application).getId());
    application = testCommon.dummyBridgeBannerApplication("Test bridge banner", "Test Owner DREI");
    application.setEndTime(time0.plusDays(10));
    applicationIds.add(applicationDao.insert(application).getId());
    applicationDao.updateStatus(applicationIds.get(0), StatusType.HANDLING);
    applicationDao.updateStatus(applicationIds.get(1), StatusType.FINISHED);
    applicationDao.updateStatus(applicationIds.get(2), StatusType.PRE_RESERVED);

    // Test filters for application type ands status
    final List<ApplicationType> TYPE_LIST = Arrays.asList(ApplicationType.SHORT_TERM_RENTAL, ApplicationType.EVENT,
        ApplicationType.PLACEMENT_CONTRACT);
    final List<StatusType> STATUS_LIST = Arrays.asList(StatusType.DECISIONMAKING, StatusType.HANDLING,
        StatusType.CANCELLED, StatusType.FINISHED);

    // Find all that end within one year, matching application type
    // specifiers
    // -- should return only the first and last
    List<Integer> matches = applicationDao.findByEndTime(time0, time0.plusYears(1),
        TYPE_LIST,
        Collections.emptyList());
    assertEquals(2, matches.size());
    assertTrue(matches.contains(applicationIds.get(0)));
    assertTrue(matches.contains(applicationIds.get(2)));

    // Excluding sent reminders - shouldn't exclude any
    final List<Integer> filtered = applicationDao.excludeSentReminders(matches);
    assertEquals(2, filtered.size());
    matches.forEach(id -> assertTrue(filtered.contains(id)));

    // Mark the returned applications as "reminder sent" and filter -- should
    // now return empty list
    applicationDao.markReminderSent(matches);
    final List<Integer> filtered2 = applicationDao.excludeSentReminders(matches);
    assertTrue(filtered2.isEmpty());

    // Find all that end within one year, matching application statuses
    // -- should return only the first and second
    matches = applicationDao.findByEndTime(time0, time0.plusYears(1), Collections.emptyList(),
        STATUS_LIST);
    assertEquals(2, matches.size());
    assertTrue(matches.contains(applicationIds.get(0)));
    assertTrue(matches.contains(applicationIds.get(1)));

    // Finally, test with both type and status selectors.
    // -- should return only the first application
    matches = applicationDao.findByEndTime(time0, time0.plusYears(1), TYPE_LIST, STATUS_LIST);
    assertEquals(1, matches.size());
    assertTrue(matches.contains(applicationIds.get(0)));
  }

  @Test
  public void testMarkReminderSent() {
    // Insert three applications that end in different times in future, remember
    // their ids:
    List<Integer> applicationIds = new ArrayList<>();
    Application application = testCommon.dummyOutdoorApplication("Test application", "Test Owner");
    ZonedDateTime time0 = ZonedDateTime.parse("2015-12-03T10:15:30+02:00");
    application.setEndTime(time0.plusDays(100));
    applicationIds.add(applicationDao.insert(application).getId());
    application.setEndTime(time0.plusDays(30));
    application.setApplicationId(null);
    applicationIds.add(applicationDao.insert(application).getId());
    application.setEndTime(time0.plusDays(10));
    application.setApplicationId(null);
    applicationIds.add(applicationDao.insert(application).getId());
    application.setApplicationId(null);
    applicationIds.add(applicationDao.insert(application).getId());
    long inserted = applicationDao.markReminderSent(applicationIds);
    assertEquals(applicationIds.size(), inserted);
  }

  @Test
  public void testFindActiveExcavationAnnouncements() {
    // insert excavation announcements with all possible statuses
    int serial = 1;
    int replaceWith = -1;
    for (StatusType status : StatusType.values()) {
      if (status != StatusType.REPLACED) {
        Application application = testCommon.dummyExcavationAnnouncementApplication("Excavation " + serial, "Dig it " + serial++);
        application.setStatus(status);
        Application inserted = applicationDao.insert(application);
        if (status == StatusType.PENDING_CLIENT) replaceWith = inserted.getId();
      }
      else {
        Application application = testCommon.dummyExcavationAnnouncementApplication("Replaced excavation", "Replaced owner");
        application.setStatus(StatusType.HANDLING);
        Application inserted = applicationDao.insert(application);
        applicationDao.setApplicationReplaced(inserted.getId(), replaceWith);
      }
    }

    // insert some other types of applicatoins in active statuses
    Application outdoor = testCommon.dummyOutdoorApplication("Outdoor application", "Outsider");
    outdoor.setStatus(StatusType.HANDLING);
    applicationDao.insert(outdoor);

    Application areaRental = testCommon.dummyAreaRentalApplication("Area rental", "Renter");
    areaRental.setStatus(StatusType.DECISION);
    applicationDao.insert(areaRental);

    Application bridgeBanner = testCommon.dummyBridgeBannerApplication("Bridge Banner", "Bruce Banner");
    bridgeBanner.setStatus(StatusType.WAITING_INFORMATION);
    applicationDao.insert(bridgeBanner);

    Application placementContract = testCommon.dummyPlacementContractApplication("Placement contract", "Placer");
    placementContract.setStatus(StatusType.PENDING_CLIENT);
    applicationDao.insert(placementContract);

    List<Application> excavationAnnouncements = applicationDao.findActiveExcavationAnnouncements();

    List<StatusType> excludedStatuses = List.of(StatusType.ARCHIVED, StatusType.REPLACED, StatusType.CANCELLED, StatusType.FINISHED);

    for (Application app : excavationAnnouncements) {
      assertFalse(excludedStatuses.contains(app.getStatus()));
      assertEquals(ApplicationType.EXCAVATION_ANNOUNCEMENT, app.getType());
    }
  }

  private ApplicationTag createApplicationTag(ApplicationTagType applicationTagType) {
    ApplicationTag applicationTag = new ApplicationTag();
    applicationTag.setAddedBy(1);
    applicationTag.setCreationTime(ZonedDateTime.now());
    applicationTag.setType(applicationTagType);
    return applicationTag;
  }
}
