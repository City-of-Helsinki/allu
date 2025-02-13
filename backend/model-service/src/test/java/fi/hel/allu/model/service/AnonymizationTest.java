package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.InvoiceRecipientDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import fi.hel.allu.model.testUtils.TestCommon;
import java.util.List;

import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
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
  private ApplicationDao applicationDao;

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
}
