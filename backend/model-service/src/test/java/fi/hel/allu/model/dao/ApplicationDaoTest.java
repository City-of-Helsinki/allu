package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ApplicationTagType;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
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
  public void init() {
    testDistributionEntry = new DistributionEntry();
    testDistributionEntry.setDistributionType(DistributionType.PAPER);
    testDistributionEntry.setEmail("foobar@foo.fi");
  }

  @Test
  public void testFindApplicationsWithContacts() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Contact contact = new Contact();
    final String testContactName = "kontakti ihminen";
    final String testEmail = "test@emai.fi";
    final String testPhone = "124214";
    contact.setName(testContactName);
    contact.setEmail(testEmail);
    contact.setPhone(testPhone);
    contact.setApplicantId(application.getApplicantId());
    // retrieve contacts with no postal address first
    Contact insertedContact = contactDao.insert(contact);
    Application insertedApplication = applicationDao.insert(application);
    contactDao.setApplicationContacts(insertedApplication.getId(), Collections.singletonList(insertedContact));
    Map<Integer, List<Contact>> applicationToContacts = applicationDao.findRelatedApplicationsWithContacts(insertedContact.getId());
    assertEquals(1, applicationToContacts.size());
    assertNotNull(applicationToContacts.get(insertedApplication.getId()));
    List<Contact> contacts = applicationToContacts.get(insertedApplication.getId());
    assertEquals(1, contacts.size());
    assertEquals(testContactName, contacts.get(0).getName());
    assertEquals(testEmail, contacts.get(0).getEmail());
    assertEquals(testPhone, contacts.get(0).getPhone());

    // make sure postal address retrieval also works
    PostalAddress testPostalAddress = new PostalAddress("katu 1", "12345", "testikaupunki");
    insertedContact.setPostalAddress(testPostalAddress);
    insertedContact = contactDao.update(insertedContact.getId(), insertedContact);

    applicationToContacts = applicationDao.findRelatedApplicationsWithContacts(insertedContact.getId());
    assertNotNull(applicationToContacts.get(insertedApplication.getId()));
    contacts = applicationToContacts.get(insertedApplication.getId());
    assertEquals(testPostalAddress.getStreetAddress(), contacts.get(0).getPostalAddress().getStreetAddress());
    assertEquals(testPostalAddress.getPostalCode(), contacts.get(0).getPostalAddress().getPostalCode());
    assertEquals(testPostalAddress.getCity(), contacts.get(0).getPostalAddress().getCity());
  }

  @Test
  public void testFindApplicationsByApplicant() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application insertedApplication = applicationDao.insert(application);
    List<Integer> applicationIds = applicationDao.findByApplicant(insertedApplication.getApplicantId());
    assertEquals(1, applicationIds.size());
    assertEquals(insertedApplication.getId(), applicationIds.get(0));
  }

  @Test
  public void testCreateApplicationIdString() {
    ApplicationSequenceDao applicationSequenceDaoMock = Mockito.mock(ApplicationSequenceDao.class);
    Mockito.when(applicationSequenceDaoMock.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP)).thenReturn(1600001L);
    StructureMetaDao structureMetaDaoMock = Mockito.mock(StructureMetaDao.class);
    Mockito.when(structureMetaDaoMock.getLatestMetadataVersion()).thenReturn(1);
    ApplicationDao applicationDao =
        new ApplicationDao(null, applicationSequenceDaoMock, distributionEntryDao, structureMetaDaoMock);
    Assert.assertEquals("TP1600001", applicationDao.createApplicationId(ApplicationType.EVENT));
  }

  @Test
  public void insertApplication() {
    final int OVERRIDE_PRICE = 1234567;
    final String OVERRIDE_REASON = "Just felt like it";

    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    application.setPriceOverride(OVERRIDE_PRICE);
    application.setPriceOverrideReason(OVERRIDE_REASON);
    application.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    application.setDecisionDistributionList(Collections.singletonList(testDistributionEntry));
    Application applOut = applicationDao.insert(application);

    assertEquals(application.getName(), applOut.getName());
    assertEquals(OVERRIDE_PRICE, applOut.getPriceOverride().intValue());
    assertEquals(OVERRIDE_REASON, applOut.getPriceOverrideReason());
    assertNotEquals(application.getCreationTime(), applOut.getCreationTime());
    assertEquals(1, applOut.getDecisionDistributionList().size());
  }

  @Test
  public void testUpdateApplication() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    applOut.setName("Updated application");
    applOut.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    applOut.setDecisionDistributionList(Collections.singletonList(testDistributionEntry));
    Application updated = applicationDao.update(applOut.getId(), applOut);

    assertEquals("Updated application", updated.getName());
    assertNotEquals(applOut.getCreationTime(), updated.getCreationTime());
    assertEquals(1, updated.getDecisionDistributionList().size());
  }

  @Test
  public void testUpdateStatus() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    Application updated = applicationDao.updateStatus(applOut.getId(), StatusType.CANCELLED);
    assertEquals(StatusType.CANCELLED, updated.getStatus());
  }

  @Test
  public void testDecisionStatus() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    Application updated = applicationDao.updateDecision(applOut.getId(), StatusType.REJECTED, application.getHandler());
    assertEquals(StatusType.REJECTED, updated.getStatus());
    assertEquals(application.getHandler(), updated.getDecisionMaker());
    assertNotNull(updated.getDecisionTime());
  }

  @Test
  public void testInsertApplicationTags() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    newApplication.setApplicationTags(Collections.singletonList(createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application application = applicationDao.insert(newApplication);
    assertNotNull(application.getApplicationTags());
    assertEquals(1, application.getApplicationTags().size());
    assertEquals(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, application.getApplicationTags().get(0).getType());
  }

  @Test
  public void testUpdateApplicationTags() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    newApplication.setApplicationTags(Collections.singletonList(
        createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application application = applicationDao.insert(newApplication);
    application.setApplicationTags(Arrays.asList(
        createApplicationTag(ApplicationTagType.COMPENSATION_CLARIFICATION),
        createApplicationTag(ApplicationTagType.DEPOSIT_PAID)));
    application = applicationDao.update(application.getId(), application);
    assertNotNull(application.getApplicationTags());
    assertEquals(2, application.getApplicationTags().size());
    assertTrue(application.getApplicationTags().stream()
        .filter(tag -> tag.getType().equals(ApplicationTagType.COMPENSATION_CLARIFICATION)).findFirst().isPresent());
    assertTrue(application.getApplicationTags().stream()
        .filter(tag -> tag.getType().equals(ApplicationTagType.DEPOSIT_PAID)).findFirst().isPresent());
  }

  private ApplicationTag createApplicationTag(ApplicationTagType applicationTagType) {
    ApplicationTag applicationTag = new ApplicationTag();
    applicationTag.setAddedBy(1);
    applicationTag.setCreationTime(ZonedDateTime.now());
    applicationTag.setType(applicationTagType);
    return applicationTag;
  }
}
