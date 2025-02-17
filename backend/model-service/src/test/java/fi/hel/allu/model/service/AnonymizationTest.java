package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.TestCommon;

import java.time.ZonedDateTime;
import java.util.List;

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
    Geometry geometry = polygon(3879,
      ring(c(25492000, 6675000), c(25492500, 6675000), c(25492100, 6675100), c(25492000, 6675000)));
    Location loc1 = new Location();
    loc1.setGeometry(geometry);
    loc1.setAdditionalInfo("XXX");
    loc1.setStartTime(ZonedDateTime.of(2025, 6 ,1 ,12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
    loc1.setEndTime(ZonedDateTime.of(2025, 6 ,5 ,12, 0, 0, 0, TimeUtil.HelsinkiZoneId));
    loc1.setPaymentTariffOverride("3");
    app1.setLocations(List.of(loc1));
    app1 = applicationService.insert(app1, 3);
    Application app2 = testCommon.dummyExcavationAnnouncementApplication("Application2", "Client2");
    Location loc2 = new Location();
    loc2.setGeometry(geometry);
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
}
