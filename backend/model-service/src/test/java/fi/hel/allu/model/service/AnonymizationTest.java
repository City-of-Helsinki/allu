package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.TestCommon;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.geolatte.geom.builder.DSL.*;
import static org.geolatte.geom.builder.DSL.c;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class AnonymizationTest {
  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private DistributionEntryDao distributionEntryDao;
  @Autowired
  private DecisionDao decisionDao;
  @Autowired
  private AttachmentDao attachmentDao;
  @Autowired
  private SupervisionTaskDao supervisionTaskDao;
  @Autowired
  private CommentDao commentDao;

  private final Geometry testGeometry = polygon(3879, ring(c(25492000, 6675000), c(25492500, 6675000), c(25492100, 6675100), c(25492000, 6675000)));


  @Test
  public void shouldSetStatusToAnonymized() {
    Application app1 = testCommon.dummyExcavationAnnouncementApplication("Application1", "Client1");
    app1 = applicationService.insert(app1, 3);
    Application app2 = testCommon.dummyExcavationAnnouncementApplication("Application2", "Client2");
    app2 = applicationService.insert(app2, 3);

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Application anonApp1 = applicationService.findById(app1.getId());
    assertEquals(StatusType.ANONYMIZED, anonApp1.getStatus());
    Application anonApp2 = applicationService.findById(app2.getId());
    assertEquals(StatusType.ANONYMIZED, anonApp2.getStatus());
  }

  @Test
  public void shouldRemoveAllTags() {
    Application app1 = testCommon.dummyExcavationAnnouncementApplication("Application1", "Client1");
    app1.setApplicationTags(List.of(
      new ApplicationTag(3, ApplicationTagType.DEPOSIT_PAID),
      new ApplicationTag(3, ApplicationTagType.OTHER_CHANGES)
    ));
    app1 = applicationService.insert(app1, 3);
    Application app2 = testCommon.dummyExcavationAnnouncementApplication("Application2", "Client2");
    app2.setApplicationTags(List.of(
      new ApplicationTag(3, ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)
    ));
    app2 = applicationService.insert(app2, 3);

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Application anonApp1 = applicationService.findById(app1.getId());
    assertEquals(0, anonApp1.getApplicationTags().size());
    Application anonApp2 = applicationService.findById(app2.getId());
    assertEquals(0, anonApp2.getApplicationTags().size());
  }

  @Test
  public void shouldRemoveCustomers() {
    Customer testCustomer = testCommon.insertPerson();
    Contact testContact = testCommon.insertContact(testCustomer.getId());

    Application app1 = testCommon.dummyExcavationAnnouncementApplication("Application1", "Client1");
    app1.setCustomersWithContacts(List.of(
      new CustomerWithContacts(
        CustomerRoleType.APPLICANT,
        testCustomer,
        List.of(testContact)
      ),
      new CustomerWithContacts(
        CustomerRoleType.CONTRACTOR,
        testCustomer,
        List.of(testContact)
      )
    ));
    app1 = applicationService.insert(app1, 3);

    Application app2 = testCommon.dummyExcavationAnnouncementApplication("Application2", "Client2");
    app2.setCustomersWithContacts(List.of(
      new CustomerWithContacts(
        CustomerRoleType.APPLICANT,
        testCustomer,
        List.of(testContact)
      )
    ));
    app2 = applicationService.insert(app2, 3);

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Application anonApp1 = applicationService.findById(app1.getId());
    assertEquals(0, anonApp1.getCustomersWithContacts().size());
    Application anonApp2 = applicationService.findById(app2.getId());
    assertEquals(0, anonApp2.getCustomersWithContacts().size());
  }

  @Test
  public void shouldRemoveDistributionEntries() {
    Application app1 = testCommon.dummyExcavationAnnouncementApplication("Application1", "Client1");
    app1 = applicationService.insert(app1, 3);
    DistributionEntry dist1 = new DistributionEntry();
    dist1.setDistributionType(DistributionType.EMAIL);
    dist1.setEmail("test.email@test.net");
    dist1.setName("Teuvo Testaaja");
    dist1.setApplicationId(app1.getId());
    DistributionEntry dist2 = new DistributionEntry();
    dist2.setDistributionType(DistributionType.PAPER);
    dist2.setName("Pekka Paperimies");
    dist2.setPostalAddress(new PostalAddress("Paperikatu 3", "52400", "Jämsänkoski"));
    dist2.setApplicationId(app1.getId());
    distributionEntryDao.insert(List.of(dist1, dist2));
    Application app2 = testCommon.dummyExcavationAnnouncementApplication("Application2", "Client2");
    app2 = applicationService.insert(app2, 3);
    DistributionEntry dist3 = new DistributionEntry();
    dist3.setDistributionType(DistributionType.EMAIL);
    dist3.setEmail("mun.maili@foomail.com");
    dist3.setName("Keijo Kokeilija");
    dist3.setApplicationId(app2.getId());
    distributionEntryDao.insert(List.of(dist3));

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Application anonApp1 = applicationService.findById(app1.getId());
    assertEquals(0, anonApp1.getDecisionDistributionList().size());
    Application anonApp2 = applicationService.findById(app2.getId());
    assertEquals(0, anonApp2.getDecisionDistributionList().size());
  }

  @Test
  public void shouldRemoveLocationAdditionalInfo() {
    Application app1 = testCommon.dummyExcavationAnnouncementApplication("Application1", "Client1");
    Location loc1 = new Location();
    loc1.setGeometry(testGeometry);
    loc1.setAdditionalInfo("XXX");
    loc1.setStartTime(ZonedDateTime.of(2025, 6 ,1 ,12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
    loc1.setEndTime(ZonedDateTime.of(2025, 6 ,5 ,12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
    loc1.setPaymentTariffOverride("3");
    app1.setLocations(List.of(loc1));
    app1 = applicationService.insert(app1, 3);
    Application app2 = testCommon.dummyExcavationAnnouncementApplication("Application2", "Client2");
    Location loc2 = new Location();
    loc2.setGeometry(testGeometry);
    loc2.setAdditionalInfo("YYY");
    loc2.setStartTime(ZonedDateTime.of(2025, 6 ,1 ,12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
    loc2.setEndTime(ZonedDateTime.of(2025, 6 ,5 ,12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
    loc2.setPaymentTariffOverride("3");
    app2.setLocations(List.of(loc2));
    app2 = applicationService.insert(app2, 3);

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Application anonApp1 = applicationService.findById(app1.getId());
    for (Location loc : anonApp1.getLocations()) {
      assertEquals("", loc.getAdditionalInfo());
    }
    Application anonApp2 = applicationService.findById(app2.getId());
    for (Location loc : anonApp2.getLocations()) {
      assertEquals("", loc.getAdditionalInfo());
    }
  }

  @Test
  public void shouldRemoveAdditionalInfoFromCableInfos() {
    Application app1 = testCommon.dummyCableReportApplication("Application1", "Client1");
    CableReport ext = (CableReport)app1.getExtension();
    CableInfoEntry entry1 = new CableInfoEntry();
    entry1.setType(DefaultTextType.ELECTRICITY);
    entry1.setAdditionalInfo("Beware for electricity");
    CableInfoEntry entry2 = new CableInfoEntry();
    entry2.setType(DefaultTextType.SEWAGE_PIPE);
    entry2.setAdditionalInfo("Down the sewer");
    ext.setInfoEntries(List.of(entry1, entry2));
    app1 = applicationService.insert(app1, 3);

    Application app2 = testCommon.dummyCableReportApplication("Application2", "Client2");
    CableReport ext2 = (CableReport)app2.getExtension();
    CableInfoEntry entry3 = new CableInfoEntry();
    entry3.setType(DefaultTextType.TELECOMMUNICATION);
    entry3.setAdditionalInfo("Don't cut the cables");
    ext2.setInfoEntries(List.of(entry3));
    app2 = applicationService.insert(app2, 3);

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Application anonApp1 = applicationService.findById(app1.getId());
    CableReport anonExt1 = (CableReport)anonApp1.getExtension();
    for (CableInfoEntry entry : anonExt1.getInfoEntries()) {
      assertEquals("", entry.getAdditionalInfo());
    }
    Application anonApp2 = applicationService.findById(app2.getId());
    CableReport anonExt2 = (CableReport)anonApp2.getExtension();
    for (CableInfoEntry entry : anonExt2.getInfoEntries()) {
      assertEquals("", entry.getAdditionalInfo());
    }
  }

  @Test
  public void shouldRemoveDecision() {
    Application app1 = testCommon.dummyCableReportApplication("Application1", "Client1");
    app1 = applicationService.insert(app1, 3);
    decisionDao.storeDecision(app1.getId(), "Lorem ipsum".getBytes(StandardCharsets.UTF_8));
    Application app2 = testCommon.dummyCableReportApplication("Application2", "Client2");
    app2 = applicationService.insert(app2, 3);
    decisionDao.storeDecision(app2.getId(), "dolor sit amet".getBytes());

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    Optional<byte[]> decision = decisionDao.getDecision(app1.getId());
    assert(decision.isEmpty());
    Optional<byte[]> decision2 = decisionDao.getDecision(app2.getId());
    assert(decision2.isEmpty());
  }

  @Test
  public void shouldRemoveAttachments() {
    DefaultAttachmentInfo defaultInfo = attachmentDao.insertDefault(new DefaultAttachmentInfo(
      null,
      3,
      AttachmentType.DEFAULT,
      "application/pdf",
      "defaulttest",
      "defaulttest",
      null,
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId),
      false,
      null,
      List.of(ApplicationType.CABLE_REPORT),
      42
    ), "lorem ipsum".getBytes(StandardCharsets.UTF_8));

    Application app1 = testCommon.dummyCableReportApplication("Application1", "Client1");
    app1 = applicationService.insert(app1 ,3);
    attachmentDao.linkApplicationToAttachment(app1.getId(), defaultInfo.getId());
    AttachmentInfo attachment1 = attachmentDao.insert(app1.getId(), new AttachmentInfo(
      null,
      3,
      AttachmentType.ADDED_BY_HANDLER,
      "application/pdf",
      "test1",
      "test1",
      null,
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId),
      false
    ), "dolor sit amet".getBytes(StandardCharsets.UTF_8));

    Application app2 = testCommon.dummyCableReportApplication("Application2", "Client2");
    app2 = applicationService.insert(app2 ,3);
    attachmentDao.linkApplicationToAttachment(app2.getId(), defaultInfo.getId());
    AttachmentInfo attachment2 = attachmentDao.insert(app2.getId(), new AttachmentInfo(
      null,
      3,
      AttachmentType.ADDED_BY_HANDLER,
      "application/pdf",
      "test2",
      "test2",
      null,
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId),
      false
    ), "dolor sit amet".getBytes(StandardCharsets.UTF_8));

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    assertEquals(0, attachmentDao.findByApplication(app1.getId()).size());
    assertEquals(0, attachmentDao.findByApplication(app2.getId()).size());
    assert(attachmentDao.findDefaultById(defaultInfo.getId()).isPresent());
    assert(attachmentDao.findById(attachment1.getId()).isEmpty());
    assert(attachmentDao.findById(attachment2.getId()).isEmpty());
  }

  @Test
  public void shouldAnonymizeSupervisionTasks() {
    Location loc = testCommon.createLocation("Testikatu 42", testGeometry, ZonedDateTime.now(TimeUtil.HelsinkiZoneId), ZonedDateTime.now(TimeUtil.HelsinkiZoneId).plusWeeks(1));

    Application  app1 = applicationService.insert(testCommon.dummyCableReportApplication("Application1", "Client1"), 3);
    SupervisionTask task1 = new SupervisionTask(
      null,
      app1.getId(),
      SupervisionTaskType.SUPERVISION,
      3,
      3,
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId),
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId).plusWeeks(1),
      null,
      SupervisionTaskStatusType.OPEN,
      "Description here",
      "Result here",
      loc.getId()
    );
    supervisionTaskDao.insert(task1);
    SupervisionTask task2 = new SupervisionTask(
      null,
      app1.getId(),
      SupervisionTaskType.OPERATIONAL_CONDITION,
      3,
      3,
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId),
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId).plusWeeks(1),
      null,
      SupervisionTaskStatusType.OPEN,
      "Another description here",
      "Result here too",
      loc.getId()
    );
    supervisionTaskDao.insert(task2);

    Application app2 = applicationService.insert(testCommon.dummyCableReportApplication("Application2", "Client2"), 3);
    SupervisionTask task3 = new SupervisionTask(
      null,
      app2.getId(),
      SupervisionTaskType.SUPERVISION,
      3,
      3,
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId),
      ZonedDateTime.now(TimeUtil.HelsinkiZoneId).plusWeeks(1),
      null,
      SupervisionTaskStatusType.OPEN,
      "Lorem ipsum",
      "Hocus pocus",
      loc.getId()
    );
    supervisionTaskDao.insert(task3);

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    for (SupervisionTask task : supervisionTaskDao.findByApplicationId(app1.getId())) {
      assertNull(task.getCreatorId());
      assertNull(task.getOwnerId());
      assertNull(task.getDescription());
      assertNull(task.getResult());
    }
    for (SupervisionTask task : supervisionTaskDao.findByApplicationId(app2.getId())) {
      assertNull(task.getCreatorId());
      assertNull(task.getOwnerId());
      assertNull(task.getDescription());
      assertNull(task.getResult());
    }
  }

  @Test
  public void shouldRemoveComments() {
    Application app1 = applicationService.insert(testCommon.dummyCableReportApplication("Application1", "Client1"), 3);
    Comment comment1 = new Comment();
    comment1.setText("Lorem ipsum");
    comment1.setType(CommentType.INTERNAL);
    comment1.setUserId(3);
    commentDao.insertForApplication(comment1,app1.getId());
    Comment comment2 = new Comment();
    comment2.setText("dolor sit amet");
    comment2.setType(CommentType.EXTERNAL_SYSTEM);
    comment2.setUserId(3);
    commentDao.insertForApplication(comment2,app1.getId());

    Application app2 = applicationService.insert(testCommon.dummyCableReportApplication("Application2", "Client2"), 3);
    Comment comment3 = new Comment();
    comment3.setText("consectetur adipiscing elit");
    comment3.setType(CommentType.INTERNAL);
    comment3.setUserId(3);
    commentDao.insertForApplication(comment3,app2.getId());

    applicationService.anonymizeApplications(List.of(app1.getId(), app2.getId()));

    assertEquals(0, commentDao.findByApplicationId(app1.getId()).size());
    assertEquals(0, commentDao.findByApplicationId(app2.getId()).size());
  }
}
