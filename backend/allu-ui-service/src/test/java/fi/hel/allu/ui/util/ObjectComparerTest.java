package fi.hel.allu.ui.util;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.PostalAddressJson;
import fi.hel.allu.ui.domain.UserJson;
import fi.hel.allu.ui.util.ObjectComparer.Difference;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObjectComparerTest {

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
    ObjectComparer oc = new ObjectComparer();
    List<Difference> diff = oc.compare(user1, user2);
    assertEquals(4, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/userName")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/title")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/emailAddress")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/assignedRoles")).count());
  }

  @Test
  public void testComplex() {
    ApplicationJson app1 = new ApplicationJson();
    app1.setEndTime(ZonedDateTime.parse("2017-02-03T10:15:30+02:00[Europe/Helsinki]"));
    app1.setApplicant(new ApplicantJson());
    app1.getApplicant().setEmail("hakija@jossain.org");
    app1.getApplicant().setPostalAddress(new PostalAddressJson());
    app1.getApplicant().getPostalAddress().setCity("Siti");
    app1.getApplicant().setId(99);
    ApplicationJson app2 = new ApplicationJson();
    app2.setApplicant(new ApplicantJson());
    app2.setEndTime(ZonedDateTime.parse("2017-02-03T10:25:30+02:00[Europe/Helsinki]"));
    app2.getApplicant().setEmail("jokumuu@jossainmuualla.org");
    app2.getApplicant().setPostalAddress(new PostalAddressJson());
    app2.getApplicant().getPostalAddress().setCity("Villits");
    ObjectComparer oc = new ObjectComparer();
    List<Difference> diff = oc.compare(app1, app2);
    assertEquals(4, diff.size());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/applicant/email")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/applicant/id")).count());
    assertEquals(1, diff.stream().filter(d -> d.keyName.equals("/endTime")).count());
    Difference cityDiff = diff.stream().filter(d -> d.keyName.equals("/applicant/postalAddress/city")).findFirst()
        .orElse(null);
    assertNotNull(cityDiff);
    assertEquals("Siti", cityDiff.oldValue);
    assertEquals("Villits", cityDiff.newValue);
  }

  @Test
  public void testEmptyArrayEqualsNull() {
    ApplicationJson applicationJson1 = new ApplicationJson();
    applicationJson1.setApplicationTags(null);
    ApplicationJson applicationJson2 = new ApplicationJson();
    applicationJson2.setApplicationTags(Collections.emptyList());
    ObjectComparer oc = new ObjectComparer();
    List<Difference> diff = oc.compare(applicationJson1, applicationJson2);
    assertEquals(0, diff.size());
  }

  @Test
  public void testEmptyStringEqualsNull() {
    ApplicantJson app1 = new ApplicantJson();
    app1.setName("");
    ApplicantJson app2 = new ApplicantJson();
    app2.setName(null);
    ObjectComparer oc = new ObjectComparer();
    List<Difference> diff = oc.compare(app1, app2);
    assertEquals(0, diff.size());
  }
}
