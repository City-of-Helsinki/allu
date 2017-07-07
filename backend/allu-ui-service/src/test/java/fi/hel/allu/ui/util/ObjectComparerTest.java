package fi.hel.allu.ui.util;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.util.ObjectComparer.Difference;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObjectComparerTest {

  private ObjectComparer comparer;

  @Before
  public void setup() {
    comparer = new ObjectComparer();
  }

  @Test
  public void testSimple() {
    UserJson user1 = new UserJson();
    user1.setActive(true);
    user1.setRealName("Jouko Turkko");
    user1.setAllowedApplicationTypes(Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.CABLE_REPORT));
    user1.setAssignedRoles(Arrays.asList(RoleType.ROLE_DECISION, RoleType.ROLE_INVOICING));
    user1.setEmailAddress("jouko@turkko.xx");
    user1.setUserName("jouko");
    user1.setTitle("Manager of managing");
    UserJson user2 = new UserJson();
    user2.setActive(true);
    user2.setRealName("Jouko Turkko");
    user2.setAllowedApplicationTypes(Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.CABLE_REPORT));
    user2.setAssignedRoles(Arrays.asList(RoleType.ROLE_DECISION, RoleType.ROLE_SUPERVISE));
    user2.setTitle("Manager of managing change");
    List<Difference> diff = comparer.compare(user1, user2);
    assertEquals(4, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/userName")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/title")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/emailAddress")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/assignedRoles/1")).count());
  }

  @Test
  public void testComplex() {
    ApplicationJson app1 = new ApplicationJson();
    app1.setEndTime(ZonedDateTime.parse("2017-02-03T10:15:30+02:00[Europe/Helsinki]"));
    CustomerJson customerJson = new CustomerJson();
    customerJson.setEmail("hakija@jossain.org");
    customerJson.setPostalAddress(new PostalAddressJson());
    customerJson.getPostalAddress().setCity("Siti");
    customerJson.setId(99);
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setRoleType(CustomerRoleType.APPLICANT);
    customerWithContactsJson.setCustomer(customerJson);
    app1.setCustomersWithContacts(Collections.singletonList(customerWithContactsJson));

    ApplicationJson app2 = new ApplicationJson();
    app2.setEndTime(ZonedDateTime.parse("2017-02-03T10:25:30+02:00[Europe/Helsinki]"));
    customerJson = new CustomerJson();
    customerJson.setEmail("jokumuu@jossainmuualla.org");
    customerJson.setPostalAddress(new PostalAddressJson());
    customerJson.getPostalAddress().setCity("Villits");
    customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setRoleType(CustomerRoleType.APPLICANT);
    customerWithContactsJson.setCustomer(customerJson);
    app2.setCustomersWithContacts(Collections.singletonList(customerWithContactsJson));

    List<Difference> diff = comparer.compare(app1, app2);
    assertEquals(4, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/customersWithContacts/0/customer/email")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/customersWithContacts/0/customer/id")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/endTime")).count());
    Difference cityDiff = diff.stream().filter(d -> d.keyName.equals("/customersWithContacts/0/customer/postalAddress/city")).findFirst().orElse(null);
    assertNotNull(cityDiff);
    assertEquals("Siti", cityDiff.oldValue);
    assertEquals("Villits", cityDiff.newValue);
  }

  @Test
  public void testEmptyArrayEqualsNull() {
    ApplicationJson applicationJson1 = new ApplicationJson();
    applicationJson1.setApplicationTags(null);
    applicationJson1.setLocations(Collections.emptyList());
    ApplicationJson applicationJson2 = new ApplicationJson();
    applicationJson2.setApplicationTags(Collections.emptyList());
    applicationJson2.setLocations(null);
    List<Difference> diff = comparer.compare(applicationJson1, applicationJson2);
    assertEquals(0, diff.size());
  }

  @Test
  public void testEmptyStringEqualsNull() {
    CustomerJson app1 = new CustomerJson();
    app1.setName("");
    app1.setPhone(null);
    CustomerJson app2 = new CustomerJson();
    app2.setName(null);
    app2.setPhone("");
    List<Difference> diff = comparer.compare(app1, app2);
    assertEquals(0, diff.size());
  }

  @Test
  public void testDetectArrayAddWithId() {
    ApplicationJson applicationJson1 = new ApplicationJson();
    applicationJson1.setAttachmentList(createAttachmentList(2));
    ApplicationJson applicationJson2 = new ApplicationJson();
    applicationJson2.setAttachmentList(createAttachmentList(4));
    List<Difference> diff = comparer.compare(applicationJson1, applicationJson2);
    // 2 new elements with id and description -> 4 changes
    assertEquals(4, diff.size());
    assertEquals(2, diff.stream().map(d -> d.keyName).filter(k -> k.matches("/attachmentList/\\d*/id")).count());
    assertEquals(2,
        diff.stream().map(d -> d.keyName).filter(k -> k.matches("/attachmentList/\\d*/description")).count());
  }

  @Test
  public void testDetectArrayAddWithoutId() {
    ApplicationJson applicationJson1 = new ApplicationJson();
    applicationJson1.setApplicationTags(createTagList(2));
    ApplicationJson applicationJson2 = new ApplicationJson();
    applicationJson2.setApplicationTags(createTagList(4));
    List<Difference> diff = comparer.compare(applicationJson1, applicationJson2);
    // 2 new elements with type -> 2 changes
    assertEquals(2, diff.stream().map(d -> d.keyName).filter(k -> k.matches("/applicationTags/\\d*/type")).count());
  }

  private List<AttachmentInfoJson> createAttachmentList(int numAttachments) {
    List<AttachmentInfoJson> result = new ArrayList<>();
    for (int i = 1; i <= numAttachments; ++i) {
      AttachmentInfoJson aij = new AttachmentInfoJson();
      aij.setId(420 + i);
      aij.setDescription("Attachment " + i);
      result.add(aij);
    }
    return result;
  }

  private List<ApplicationTagJson> createTagList(int numTags) {
    List<ApplicationTagJson> result = new ArrayList<>();
    final ApplicationTagType[] values = ApplicationTagType.values();
    for (int i = 1; i <= numTags; ++i) {
      ApplicationTagJson at = new ApplicationTagJson();
      at.setType(values[i % values.length]);
      result.add(at);
    }
    return result;
  }

}
